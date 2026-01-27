package com.TD2;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    Ingredient findIngredientById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    select i.id,
                           i.name,
                           i.price,
                           i.category
                    from ingredient i
                    where i.id = ?
                    """
            );
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                ingredient.setStockMovementList(findStockMovementsByIngredientId(id));

                dbConnection.closeConnection(connection);
                return ingredient;
            }

            dbConnection.closeConnection(connection);
            throw new RuntimeException("Ingredient not found " + id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<StockMovement> findStockMovementsByIngredientId(Integer ingredientId) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();

        List<StockMovement> movements = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    select id,
                           quantity,
                           unit,
                           type,
                           creation_datetime
                    from stock_movement
                    where id_ingredient = ?
                    order by id
                    """
            );
            ps.setInt(1, ingredientId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StockMovement sm = new StockMovement();
                sm.setId(rs.getInt("id"));

                StockValue sv = new StockValue();
                sv.setQuantity(rs.getDouble("quantity"));
                sv.setUnit(UnitTypeEnum.valueOf(rs.getString("unit")));
                sm.setValue(sv);

                sm.setType(MovementTypeEnum.valueOf(rs.getString("type")));

                Timestamp ts = rs.getTimestamp("creation_datetime");
                sm.setCreationDatetime(ts.toInstant());

                movements.add(sm);
            }

            dbConnection.closeConnection(connection);
            return movements;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Ingredient saveIngredient(Ingredient toSave) {
        String upsertIngredientSql = """
            INSERT INTO ingredient (id, name, category, price)
            VALUES (?, ?, ?::ingredient_category, ?)
            ON CONFLICT (id) DO UPDATE
            SET name = EXCLUDED.name,
                category = EXCLUDED.category,
                price = EXCLUDED.price
            RETURNING id
        """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);

            Integer ingredientId;

            try (PreparedStatement ps = conn.prepareStatement(upsertIngredientSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                }

                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getCategory().name());
                ps.setDouble(4, toSave.getPrice());

                ResultSet rs = ps.executeQuery();
                rs.next();
                ingredientId = rs.getInt(1);
            }

            insertStockMovements(conn, ingredientId, toSave.getStockMovementList());

            conn.commit();
            return findIngredientById(ingredientId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertStockMovements(Connection conn, Integer ingredientId, List<StockMovement> movements)
            throws SQLException {

        if (movements == null || movements.isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO stock_movement
            (id, id_ingredient, quantity, type, unit, creation_datetime)
            VALUES (?, ?, ?, ?::movement_type, ?::unit_type, ?)
            ON CONFLICT (id) DO NOTHING
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (StockMovement sm : movements) {

                if (sm.getId() != null) {
                    ps.setInt(1, sm.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "stock_movement", "id"));
                }

                ps.setInt(2, ingredientId);

                ps.setDouble(3, sm.getValue().getQuantity());
                ps.setString(4, sm.getType().name());
                ps.setString(5, sm.getValue().getUnit().name());
                ps.setTimestamp(6, Timestamp.from(sm.getCreationDatetime()));

                ps.executeUpdate();
            }
        }
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sql = "SELECT pg_get_serial_sequence(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new RuntimeException("Sequence not found for " + tableName + "." + columnName);
        }

        updateSequenceNextValue(conn, tableName, columnName, sequenceName);

        PreparedStatement ps = conn.prepareStatement("SELECT nextval(?)");
        ps.setString(1, sequenceName);

        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName)
            throws SQLException {

        String sql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 1) FROM %s))",
                sequenceName, columnName, tableName
        );

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.executeQuery();
    }
}
