package com.TD2;

import java.util.Objects;

public class DishIngredient {
    private Integer id;
    private Ingredient ingredient;
    private Double requiredQuantity;
    private UnitTypeEnum unit;

    public DishIngredient() {}

    public DishIngredient(Integer id, Ingredient ingredient, Double requiredQuantity, UnitTypeEnum unit) {
        this.id = id;
        this.ingredient = ingredient;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public Double getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(Double requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    public UnitTypeEnum getUnit() { return unit; }
    public void setUnit(UnitTypeEnum unit) { this.unit = unit; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DishIngredient)) return false;
        DishIngredient that = (DishIngredient) o;
        return Objects.equals(id, that.id)
                && Objects.equals(ingredient, that.ingredient)
                && Objects.equals(requiredQuantity, that.requiredQuantity)
                && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ingredient, requiredQuantity, unit);
    }
}
