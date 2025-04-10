package com.example.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    // Constructeur privé pour empêcher l'instanciation
    private DatabaseConnection() {}

    // Méthode pour obtenir l'instance unique de connexion
    public static synchronized Connection getInstance() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
                throw e; // Relance l'exception pour une gestion ultérieure
            }
        }
        return connection;
    }
}
