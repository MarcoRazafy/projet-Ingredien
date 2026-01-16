ALTER TABLE ingredient
    ADD COLUMN IF NOT EXISTS required_quantity NUMERIC(10,2);
UPDATE ingredient SET required_quantity = 1
WHERE name = 'Laitue';

UPDATE ingredient SET required_quantity = 2
WHERE name = 'Tomate';

UPDATE ingredient SET required_quantity = 0.5
WHERE name = 'Poulet';

UPDATE ingredient SET required_quantity = NULL
WHERE name = 'Chocolat';

UPDATE ingredient SET required_quantity = NULL
WHERE name = 'Beurre';
