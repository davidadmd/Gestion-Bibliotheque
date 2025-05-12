package com.example.library.dao;

import com.example.library.model.User;
import com.example.library.util.DatabaseConnection;

import java.sql.*;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;

public class UserDAO {
    private Connection connection;

    // Constructeur
    public UserDAO() throws SQLException {
        try {
            // Tentative de connexion à la base de données avec plusieurs essais
            int maxRetries = 3;
            int attempts = 0;
            boolean connected = false;
            
            while (!connected && attempts < maxRetries) {
                try {
                    attempts++;
                    this.connection = DatabaseConnection.getInstance();
                    
                    if (this.connection != null && this.connection.isValid(2)) {
                        connected = true;
                        System.out.println("Connexion à la base de données réussie après " + attempts + " tentative(s)");
                    } else {
                        System.err.println("Tentative de connexion #" + attempts + " échouée - connexion invalide");
                        Thread.sleep(1000); // Attendre 1 seconde avant de réessayer
                    }
                } catch (SQLException sqle) {
                    System.err.println("Tentative de connexion #" + attempts + " échouée: " + sqle.getMessage());
                    if (attempts < maxRetries) {
                        System.out.println("Nouvelle tentative dans 1 seconde...");
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            if (!connected) {
                System.err.println("Impossible de se connecter à la base de données après " + maxRetries + " tentatives");
                System.err.println("L'application va continuer en mode dégradé (certaines fonctionnalités seront limitées)");
            } else {
                // Vérifier si la colonne password existe dans la table users, sinon l'ajouter
                ensurePasswordColumnExists();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de UserDAO : " + e.getMessage());
            // Ne pas relancer l'exception pour permettre le fonctionnement en mode dégradé
        }
    }

    // Vérifier si la colonne password existe dans la table users, sinon l'ajouter
    private void ensurePasswordColumnExists() {
        try {
            // Vérifier d'abord si la table users existe
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet tables = meta.getTables(null, null, "users", null);
            
            if (tables.next()) { // La table existe
                // Vérifier si la colonne password existe
                ResultSet columns = meta.getColumns(null, null, "users", "password");
                
                if (!columns.next()) {
                    // La colonne password n'existe pas, on l'ajoute
                    String sql = "ALTER TABLE users ADD COLUMN password VARCHAR(100) NOT NULL DEFAULT 'admin123'";
                    
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(sql);
                        System.out.println("Colonne password ajoutée à la table users.");
                    }
                } else {
                    System.out.println("La colonne password existe déjà dans la table users.");
                }
            } else {
                System.out.println("La table users n'existe pas, impossible d'ajouter la colonne password.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification/ajout de la colonne password : " + e.getMessage());
            // On ne lance pas d'exception pour permettre à l'application de continuer
        }
    }
    
    // Récupérer tous les utilisateurs
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        
        try {
            String sql = "SELECT * FROM users";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),  // Utilisation de la colonne 'name' au lieu de 'username'
                        rs.getString("password"),
                        "USER"  // Par défaut, on considère tous les utilisateurs comme étant de rôle "USER"
                    );
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        
        return users;
    }

    // Authentifier un utilisateur
    public User authenticate(String username, String password) {
        try {
            // Essayons d'abord avec l'email comme nom d'utilisateur
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                            rs.getInt("id"),
                            rs.getString("name"),  // On utilise name comme username
                            rs.getString("password"),
                            "ADMIN"  // Pour simplifier, on donne le rôle ADMIN au premier utilisateur qui se connecte
                        );
                    }
                }
            } catch (SQLException e) {
                // Si on a une erreur ici, c'est peut-être que la colonne password n'existe pas encore
                System.err.println("Erreur lors de l'authentification : " + e.getMessage());
            }
            
            // Essayons maintenant avec le name comme nom d'utilisateur
            sql = "SELECT * FROM users WHERE name = ? AND password = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("password"),
                            "ADMIN"  // Pour simplifier, on donne le rôle ADMIN au premier utilisateur qui se connecte
                        );
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'authentification avec name : " + e.getMessage());
            }
            
            // Mode de secours - toujours autoriser admin/admin123
            if (username.equals("admin") && password.equals("admin123")) {
                System.out.println("Connexion de secours utilisée avec l'utilisateur admin (mode développement)");
                return new User(1, "Administrateur", "admin123", "ADMIN");
            }
        } catch (Exception e) {
            System.err.println("Erreur générale lors de l'authentification : " + e.getMessage());
            
            // Mode de secours pour le développement - en cas d'erreur, on permet quand même la connexion admin
            if (username.equals("admin") && password.equals("admin123")) {
                System.out.println("Connexion de secours (mode erreur) utilisée avec admin");
                return new User(1, "Administrateur", "admin123", "ADMIN");
            }
        }
        
        return null; // Aucun utilisateur correspondant trouvé
    }
}
