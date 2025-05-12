package com.example.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;
    private static boolean databaseUnavailable = false;

    // Constructeur privé pour empêcher l'instanciation
    private DatabaseConnection() {}

    // Méthode pour obtenir l'instance unique de connexion
    public static synchronized Connection getInstance() throws SQLException {
        if (databaseUnavailable) {
            System.out.println("Base de données indisponible, tentative de reconnexion...");
        }

        if (connection == null || connection.isClosed()) {
            try {
                // Forcer le chargement du pilote JDBC
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Tentative de connexion avec un timeout plus long (5 secondes)
                DriverManager.setLoginTimeout(5);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                
                // Si on arrive ici, la connexion est réussie
                databaseUnavailable = false;
                System.out.println("Connexion à la base de données réussie!");
            } catch (ClassNotFoundException e) {
                System.err.println("Pilote JDBC MySQL introuvable : " + e.getMessage());
                databaseUnavailable = true;
                throw new SQLException("Pilote JDBC introuvable", e);
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
                databaseUnavailable = true;
                
                // Fournir des instructions plus détaillées
                System.err.println("Vérifiez que :");
                System.err.println("1. Le serveur MySQL est démarré");
                System.err.println("2. Les identifiants (root sans mot de passe) sont corrects");
                System.err.println("3. La base de données 'library_db' existe");
                System.err.println("4. Le port 3306 est disponible et non bloqué par un pare-feu");
                
                throw e; // Relance l'exception pour une gestion ultérieure
            }
        }
        return connection;
    }
    
    // Méthode pour tester la connexion sans exception
    public static boolean testConnection() {
        try {
            getInstance().isValid(3); // Test avec timeout de 3 secondes
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
