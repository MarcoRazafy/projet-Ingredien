package com.TD2;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        DataRetriever dataRetriever = new DataRetriever();

        // Log before changes
        Dish dish = dataRetriever.findDishById(4);
        System.out.println(dish);

        // Log after changes: remplacer les liens dish_ingredient
        DishIngredient di1 = new DishIngredient();
        di1.setIngredient(new Ingredient(1));
        di1.setRequiredQuantity(0.20);

        DishIngredient di2 = new DishIngredient();
        di2.setIngredient(new Ingredient(2));
        di2.setRequiredQuantity(0.15);
        di2.setUnit(UnitTypeEnum.KG);

        dish.setDishIngredients(List.of(di1, di2));

        Dish newDish = dataRetriever.saveDish(dish);
        System.out.println(newDish);

        // Ingredient creations (référentiel unique)
        List<Ingredient> createdIngredients =
                dataRetriever.createIngredients(
                        List.of(new Ingredient(null, "Fromage", CategoryEnum.DAIRY, 1200.0))
                );
        System.out.println(createdIngredients);
    }
}
