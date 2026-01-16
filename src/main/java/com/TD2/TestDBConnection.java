package com.TD2;

import java.sql.Connection;

public class TestDBConnection {

    public static void main(String[] args) {
        try (Connection connection = DBtestconnection.getDBConnection()) {
            System.out.println("Connexion réussie !");
        } catch (Exception e) {
            System.out.println("Échec de la connexion !");
            e.printStackTrace();
        }
    }
}
