package com.TD2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    public Dish findDishById(int id) {
        String sql =
                "SELECT dish.id, dish.name, dish.dish_type, " +
                        "ingredient.id, ingredient.name, ingredient.price, ingredient.category, ingredient.required_quantity " +
                        "FROM dish " +
                        "LEFT JOIN ingredient ON ingredient.id_dish = dish.id " +
                        "WHERE dish.id = ? " +
                        "ORDER BY ingredient.id";

        try (Connection conn = DBtestconnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            Dish dish = null;
            List<Ingredient> ingredients = new ArrayList<>();

            while (rs.next()) {
                if (dish == null) {
                    dish = new Dish();
                    dish.setId(rs.getInt("dish.id"));
                    dish.setName(rs.getString("dish.name"));
                    dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish.dish_type")));
                }

                int ingId = rs.getInt("ingredient.id");
                if (!rs.wasNull()) {
                    Ingredient ing = new Ingredient();
                    ing.setId(ingId);
                    ing.setName(rs.getString("ingredient.name"));
                    ing.setPrice(rs.getDouble("ingredient.price"));
                    ing.setCategory(CategoryEnum.valueOf(rs.getString("ingredient.category")));

                    Double rq = rs.getDouble("ingredient.required_quantity");
                    if (rs.wasNull()) rq = null;
                    ing.setRequiredQuantity(rq);

                    ing.setDish(dish);
                    ingredients.add(ing);
                }
            }

            if (dish == null) {
                throw new RuntimeException("Plat introuvable : id=" + id);
            }

            dish.setIngredients(ingredients);
            return dish;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dish saveDish(Dish dishToSave) {
        if (dishToSave == null) throw new RuntimeException("dishToSave is null");

        String sqlExists = "SELECT 1 FROM dish WHERE id = ?";
        String sqlInsert = "INSERT INTO dish(id, name, dish_type) VALUES (?, ?, ?)";
        String sqlUpdate = "UPDATE dish SET name = ?, dish_type = ? WHERE id = ?";
        String sqlDetach = "UPDATE ingredient SET id_dish = NULL WHERE id_dish = ?";
        String sqlAttach = "UPDATE ingredient SET id_dish = ? WHERE id = ?";

        try (Connection conn = DBtestconnection.getDBConnection()) {
            conn.setAutoCommit(false);

            try {
                boolean exists;

                try (PreparedStatement ps = conn.prepareStatement(sqlExists)) {
                    ps.setInt(1, dishToSave.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        exists = rs.next();
                    }
                }

                if (!exists) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                        ps.setInt(1, dishToSave.getId());
                        ps.setString(2, dishToSave.getName());
                        ps.setString(3, dishToSave.getDishType().name());
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                        ps.setString(1, dishToSave.getName());
                        ps.setString(2, dishToSave.getDishType().name());
                        ps.setInt(3, dishToSave.getId());
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlDetach)) {
                    ps.setInt(1, dishToSave.getId());
                    ps.executeUpdate();
                }

                if (dishToSave.getIngredients() != null) {
                    for (Ingredient ing : dishToSave.getIngredients()) {
                        if (ing == null) continue;

                        try (PreparedStatement ps = conn.prepareStatement(sqlAttach)) {
                            ps.setInt(1, dishToSave.getId());
                            ps.setInt(2, ing.getId());
                            ps.executeUpdate();
                        }
                    }
                }

                conn.commit();
                return dishToSave;

            } catch (RuntimeException ex) {
                conn.rollback();
                throw ex;
            } catch (SQLException ex) {
                conn.rollback();
                throw new RuntimeException("SQL error saveDish: " + ex.getMessage(), ex);
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("SQL connection error saveDish: " + e.getMessage(), e);
        }
    }


}
