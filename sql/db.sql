DROP DATABASE IF EXISTS mini_dish_db;
DROP USER IF EXISTS mini_dish_db_manager;

CREATE USER mini_dish_db_manager WITH PASSWORD '123456';

CREATE DATABASE mini_dish_db
    OWNER mini_dish_db_manager;

GRANT ALL PRIVILEGES ON DATABASE mini_dish_db TO mini_dish_db_manager;
