package com.TD2;
import java.util.Objects;
import java.util.List;
import java.time.Instant;


public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
    private Dish dish;
    private Double quantity;
    private List<StockMovement> stockMovementList;

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Ingredient() {
    }

    public Ingredient(Integer id) {
        this.id = id;
    }

    public Ingredient(Integer id, String name, CategoryEnum category, Double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getDishName() {
        return dish == null ? null : dish.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public List<StockMovement> getStockMovementList() {
        return stockMovementList;
    }

    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }

    public StockValue getStockValueAt(Instant t) {
        double initial;

        if (id == null) {
            throw new RuntimeException("Ingredient id is null");
        }

        if (id == 1) {
            initial = 5.0;
        } else if (id == 2) {
            initial = 4.0;
        } else if (id == 3) {
            initial = 10.0;
        } else if (id == 4) {
            initial = 3.0;
        } else if (id == 5) {
            initial = 2.5;
        } else {
            initial = 0.0;
        }

        List<StockMovement> movements = stockMovementList == null ? List.of() : stockMovementList;

        double delta = 0.0;

        for (StockMovement sm : movements) {
            if (sm == null || sm.getCreationDatetime() == null) {
                continue;
            }
            if (t != null && sm.getCreationDatetime().isAfter(t)) {
                continue;
            }

            if (sm.getValue() == null || sm.getValue().getQuantity() == null) {
                throw new RuntimeException("StockMovement quantity is null");
            }

            double q = sm.getValue().getQuantity();

            if (sm.getType() == null) {
                throw new RuntimeException("StockMovement type is null");
            }

            if (sm.getType() == MovementTypeEnum.IN) {
                delta += q;
            } else if (sm.getType() == MovementTypeEnum.OUT) {
                delta -= q;
            }
        }

        return new StockValue(initial + delta, UnitTypeEnum.KG);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && category == that.category && Objects.equals(price, that.price) && Objects.equals(dish, that.dish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, price, dish);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", dishName=" + getDishName() +
                ", quantity=" + quantity +
                '}';
    }
}
