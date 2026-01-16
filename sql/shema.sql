CREATE TYPE ingredient_category_enum AS ENUM (
  'VEGETABLE',
  'ANIMAL',
  'MARINE',
  'DAIRY',
  'OTHER'
);

CREATE TYPE dish_type_enum AS ENUM (
  'START',
  'MAIN',
  'DESSERT'
);

CREATE TABLE dish (
                      id   INT PRIMARY KEY,
                      name VARCHAR(150) NOT NULL,
                      dish_type dish_type_enum NOT NULL
);

CREATE TABLE ingredient (
                            id       INT PRIMARY KEY,
                            name     VARCHAR(150) NOT NULL,
                            price    NUMERIC(10,2) NOT NULL,
                            category ingredient_category_enum NOT NULL,
                            id_dish  INT NULL,

                            CONSTRAINT fk_ingredient_dish
                                FOREIGN KEY (id_dish) REFERENCES dish(id)
                                    ON DELETE SET NULL
                                    ON UPDATE CASCADE
);

DROP TABLE dish CASCADE;