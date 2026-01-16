package com.TD2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                    select dish.id as dish_id,
                           dish.name as dish_name,
                           dish_type,
                           dish.selling_price as dish_price
                    from dish
                    where dish.id = ?;
                    """
            );
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));

                Object dishPrice = resultSet.getObject("dish_price");
                dish.setPrice(dishPrice == null ? null : resultSet.getDouble("dish_price"));

                dish.setDishIngredients(findDishIngredientsByDishId(id));

                dbConnection.closeConnection(connection);
                return dish;
            }

            dbConnection.closeConnection(connection);
            throw new RuntimeException("Dish not found " + id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DishIngredient + Ingredient join
    private List<DishIngredient> findDishIngredientsByDishId(Integer dishId) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();

        List<DishIngredient> dishIngredients = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                    select di.id as di_id,
                           di.quantity_required,
                           di.unit,
                           i.id as ingredient_id,
                           i.name as ingredient_name,
                           i.price as ingredient_price,
                           i.category as ingredient_category
                    from dish_ingredient di
                    join ingredient i on i.id = di.id_ingredient
                    where di.id_dish = ?
                    order by di.id;
                    """
            );
            preparedStatement.setInt(1, dishId);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("ingredient_id"));
                ingredient.setName(rs.getString("ingredient_name"));
                ingredient.setPrice(rs.getDouble("ingredient_price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("ingredient_category")));

                DishIngredient di = new DishIngredient();
                di.setId(rs.getInt("di_id"));
                di.setIngredient(ingredient);

                Object q = rs.getObject("quantity_required");
                di.setRequiredQuantity(q == null ? null : rs.getDouble("quantity_required"));

                di.setUnit(UnitTypeEnum.valueOf(rs.getString("unit")));

                dishIngredients.add(di);
            }

            dbConnection.closeConnection(connection);
            return dishIngredients;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Dish saveDish(Dish toSave) {
        String upsertDishSql = """
            INSERT INTO dish (id, selling_price, name, dish_type)
            VALUES (?, ?, ?, ?::dish_type)
            ON CONFLICT (id) DO UPDATE
            SET name = EXCLUDED.name,
                dish_type = EXCLUDED.dish_type,
                selling_price = EXCLUDED.selling_price
            RETURNING id
        """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);

            Integer dishId;

            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "dish", "id"));
                }

                if (toSave.getPrice() != null) {
                    ps.setDouble(2, toSave.getPrice());
                } else {
                    ps.setNull(2, Types.NUMERIC);
                }

                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            // Remplacer les liens dish_ingredient
            deleteDishIngredients(conn, dishId);
            insertDishIngredients(conn, dishId, toSave.getDishIngredients());

            conn.commit();
            return findDishById(dishId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteDishIngredients(Connection conn, Integer dishId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM dish_ingredient WHERE id_dish = ?"
        )) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void insertDishIngredients(Connection conn, Integer dishId, List<DishIngredient> dishIngredients)
            throws SQLException {

        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return;
        }

        String insertSql = """
            INSERT INTO dish_ingredient (id, id_dish, id_ingredient, quantity_required, unit)
            VALUES (?, ?, ?, ?, ?::unit_type)
        """;

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            for (DishIngredient di : dishIngredients) {

                if (di.getId() != null) {
                    ps.setInt(1, di.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "dish_ingredient", "id"));
                }

                ps.setInt(2, dishId);

                if (di.getIngredient() == null || di.getIngredient().getId() == null) {
                    throw new RuntimeException("Ingredient id is null in DishIngredient");
                }
                ps.setInt(3, di.getIngredient().getId());

                if (di.getRequiredQuantity() == null) {
                    throw new RuntimeException("requiredQuantity is null in DishIngredient");
                }
                ps.setDouble(4, di.getRequiredQuantity());

                if (di.getUnit() == null) {
                    throw new RuntimeException("unit is null in DishIngredient");
                }
                ps.setString(5, di.getUnit().name());

                ps.executeUpdate();
            }
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return List.of();
        }

        List<Ingredient> savedIngredients = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();

        try {
            conn.setAutoCommit(false);

            String insertSql = """
                INSERT INTO ingredient (id, name, category, price)
                VALUES (?, ?, ?::ingredient_category, ?)
                RETURNING id
            """;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {
                    if (ingredient.getId() != null) {
                        ps.setInt(1, ingredient.getId());
                    } else {
                        ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                    }

                    ps.setString(2, ingredient.getName());
                    ps.setString(3, ingredient.getCategory().name());
                    ps.setDouble(4, ingredient.getPrice());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        int generatedId = rs.getInt(1);
                        ingredient.setId(generatedId);
                        savedIngredients.add(ingredient);
                    }
                }

                conn.commit();
                return savedIngredients;

            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sql = "SELECT pg_get_serial_sequence(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException(
                    "Any sequence found for " + tableName + "." + columnName
            );
        }

        updateSequenceNextValue(conn, tableName, columnName, sequenceName);

        String nextValSql = "SELECT nextval(?)";

        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName)
            throws SQLException {

        String setValSql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName
        );

        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }
}
