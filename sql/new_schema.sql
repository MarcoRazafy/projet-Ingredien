
CREATE TYPE dish_type AS ENUM ('START', 'MAIN', 'DESSERT');

CREATE TYPE ingredient_category AS ENUM (
    'VEGETABLE',
    'ANIMAL',
    'MARINE',
    'DAIRY',
    'OTHER'
    );

CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');

ALTER TABLE dish
    ADD COLUMN selling_price NUMERIC(10,2);


CREATE TABLE IF NOT EXISTS ingredient (
                                          id SERIAL PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL,
                                          price NUMERIC(10,2) NOT NULL,
                                          category ingredient_category NOT NULL
);

CREATE TABLE IF NOT EXISTS dish_ingredient (
                                               id SERIAL PRIMARY KEY,
                                               id_dish INT NOT NULL,
                                               id_ingredient INT NOT NULL,
                                               quantity_required NUMERIC(10,2) NOT NULL,
                                               unit unit_type NOT NULL,

                                               CONSTRAINT fk_dish
                                                   FOREIGN KEY (id_dish)
                                                       REFERENCES dish(id)
                                                       ON DELETE CASCADE,

                                               CONSTRAINT fk_ingredient
                                                   FOREIGN KEY (id_ingredient)
                                                       REFERENCES ingredient(id)
                                                       ON DELETE CASCADE,

                                               CONSTRAINT uq_dish_ingredient
                                                   UNIQUE (id_dish, id_ingredient)
);
INSERT INTO dish_ingredient (id, id_dish, id_ingredient, quantity_required, unit) VALUES
                                                                                      (1, 1, 1, 0.20, 'KG'),
                                                                                      (2, 1, 2, 0.15, 'KG'),
                                                                                      (3, 2, 3, 1.00, 'KG'),
                                                                                      (4, 4, 4, 0.30, 'KG'),
                                                                                      (5, 4, 5, 0.20, 'KG');

UPDATE dish SET selling_price = 3500.00 WHERE id = 1;
UPDATE dish SET selling_price = 12000.00 WHERE id = 2;
UPDATE dish SET selling_price = NULL WHERE id = 3;
UPDATE dish SET selling_price = 8000.00 WHERE id = 4;
UPDATE dish SET selling_price = NULL WHERE id = 5;
