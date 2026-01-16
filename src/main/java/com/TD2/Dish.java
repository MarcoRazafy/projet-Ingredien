package com.TD2;

import java.util.ArrayList;
import java.util.List;

public class Dish {
    private int id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

    public Dish() {
        this.ingredients = new ArrayList<>();
    }

    public Dish(int id, String name, DishTypeEnum dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = new ArrayList<>();
    }

    public double getDishCost() {
        double total = 0;

        if (ingredients == null) return total;

        for (Ingredient ingredient : ingredients) {
            if (ingredient == null) continue;

            if (ingredient.getRequiredQuantity() == null) {
                throw new RuntimeException(
                        "Quantité requise inconnue pour l'ingrédient : " + ingredient.getName()
                );
            }

            double price = (ingredient.getPrice() == null) ? 0.0 : ingredient.getPrice();
            total += price * ingredient.getRequiredQuantity();
        }

        return total;
    }

    public double getDishPrice() {
        double total = 0;
        if (ingredients == null) return total;

        for (Ingredient ingredient : ingredients) {
            if (ingredient != null && ingredient.getPrice() != null) {
                total += ingredient.getPrice();
            }
        }
        return total;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = (ingredients == null) ? new ArrayList<>() : ingredients;
    }



}

