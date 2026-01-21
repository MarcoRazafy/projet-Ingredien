package com.TD2;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();
        int[] dishIds = {1, 2, 3, 4, 5};
        for (int id : dishIds) {
            Dish dish = dataRetriever.findDishById(id);
            System.out.println("Plat : " + dish.getName());

            try {
                System.out.println("Coût = " + dish.getDishCost());
            } catch (Exception e) {
                System.out.println("Erreur coût : " + e.getMessage());
            }

            try {
                System.out.println("Marge = " + dish.getGrossMargin());
            } catch (Exception e) {
                System.out.println("Marge : Exception (prix NULL)");
            }
        }

    }
}
