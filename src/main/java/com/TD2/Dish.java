package com.TD2;

import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;

    private Double price;

    private String name;
    private DishTypeEnum dishType;

    private List<DishIngredient> dishIngredients;

    public Dish() {}

    public Dish(Integer id, String name, DishTypeEnum dishType, List<DishIngredient> dishIngredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.dishIngredients = dishIngredients;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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

    public List<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public void setDishIngredients(List<DishIngredient> dishIngredients) {
        this.dishIngredients = dishIngredients;
    }

    public Double getDishCost() {
        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return 0.0;
        }

        double totalPrice = 0.0;

        for (int i = 0; i < dishIngredients.size(); i++) {
            DishIngredient di = dishIngredients.get(i);
            if (di == null) {
                continue;
            }

            Double quantity = di.getRequiredQuantity();
            if (quantity == null) {
                throw new RuntimeException("requiredQuantity is null");
            }

            Ingredient ingredient = di.getIngredient();
            if (ingredient == null || ingredient.getPrice() == null) {
                throw new RuntimeException("ingredient or ingredient.price is null");
            }

            totalPrice = totalPrice + ingredient.getPrice() * quantity;
        }

        return totalPrice;
    }

    public Double getGrossMargin() {
        if (price == null) {
            throw new RuntimeException("Price is null");
        }
        return price - getDishCost();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id)
                && Objects.equals(name, dish.name)
                && dishType == dish.dishType
                && Objects.equals(dishIngredients, dish.dishIngredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, dishIngredients);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", dishIngredients=" + dishIngredients +
                '}';
    }

    public void setIngredients(List<Ingredient> ingredients) {
    }
}
