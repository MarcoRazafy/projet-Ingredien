package com.TD2;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        Instant t = LocalDateTime
                .of(2024, 1, 6, 12, 0)
                .toInstant(ZoneOffset.UTC);

        for (int id = 1; id <= 5; id++) {
            Ingredient ingredient = dr.findIngredientById(id);
            StockValue stock = ingredient.getStockValueAt(t);

            System.out.println(
                    ingredient.getName()
                            + " stock = "
                            + stock.getQuantity()
                            + " "
                            + stock.getUnit()
            );
        }
    }
}
