package com.TD2;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        DataRetriever dr = new DataRetriever();

        Dish dish1 = dr.findDishById(1);
        printDishSimple(dish1);

        try {
            double cost = dish1.getDishCost();
            System.out.print("COUT Dish 1 = " + cost + "\n");
        } catch (RuntimeException ex) {
            System.out.print("exeption : " + ex.getMessage());
        }

        Dish newDish = new Dish(99, "Soupe de légumes", DishTypeEnum.START);
        List<Ingredient> ingredientsToAttach = new ArrayList<>();

        Ingredient ing1 = new Ingredient();
        ing1.setId(1);
        ingredientsToAttach.add(ing1);

        newDish.setIngredients(ingredientsToAttach);

        Dish savedCreated = dr.saveDish(newDish);
        System.out.print("Dish sauvegardé: id=" + savedCreated.getId() + ", name=" + savedCreated.getName());

        Dish reloadedCreated = dr.findDishById(99);
        printDishSimple(reloadedCreated);

        try {
            double cost = reloadedCreated.getDishCost();
            System.out.print("COUT Dish 99 = " + cost);
        } catch (RuntimeException ex) {
            System.out.print("EXCEPTION : " + ex.getMessage());
        }


        Dish updateDish = new Dish(99, "Soupe de légumes (MAJ)", DishTypeEnum.START);
        List<Ingredient> updatedIngredients = new ArrayList<>();

        Ingredient ing2 = new Ingredient();
        ing2.setId(2);
        updatedIngredients.add(ing2);

        updateDish.setIngredients(updatedIngredients);

        Dish savedUpdated = dr.saveDish(updateDish);
        System.out.print("Dish sauvegardé (update) : id=" + savedUpdated.getId() + ", name=" + savedUpdated.getName() + "\n");

        Dish reloadedUpdated = dr.findDishById(99);
        printDishSimple(reloadedUpdated);

        try {
            double cost = reloadedUpdated.getDishCost();
            System.out.print("COUT Dish 99 (MAJ) = " + cost + "\n");
        } catch (RuntimeException ex) {
            System.out.print("EXCEPTION : " + ex.getMessage() + "\n");
        }

    }

    private static void printDishSimple(Dish dish) {
        System.out.print("Dish: id=" + dish.getId() + ", name=" + dish.getName() + ", type=" + dish.getDishType() + "\n");

        if (dish.getIngredients() == null || dish.getIngredients().isEmpty()) {
            return;
        }

        for (Ingredient i : dish.getIngredients()) {
            System.out.print("  - id=" + i.getId()
                    + ", name=" + i.getName()
                    + ", price=" + i.getPrice()
                    + ", requiredQuantity=" + i.getRequiredQuantity()
                    + "\n");
        }
    }
}
