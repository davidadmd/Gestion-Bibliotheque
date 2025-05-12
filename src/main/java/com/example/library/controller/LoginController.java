package com.example.library.controller;

import com.example.library.Main;
import com.example.library.dao.UserDAO;
import com.example.library.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorMessage;
    
    private UserDAO userDAO;
    
    public void initialize() {
        errorMessage.setText("");
        
        try {
            userDAO = new UserDAO();
        } catch (SQLException e) {
            showError("Erreur de connexion à la base de données: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Vérification que les champs ne sont pas vides
        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }
        
        User user = userDAO.authenticate(username, password);
        
        if (user != null) {
            // Authentification réussie
            loadMainView();
        } else {
            // Authentification échouée
            showError("Nom d'utilisateur ou mot de passe incorrect.");
        }
    }
    
    private void showError(String message) {
        errorMessage.setText(message);
    }
    
    private void loadMainView() {
        try {
            // Charger la vue principale
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("library_view.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Changer la scène
            stage.setTitle("Logiciel de Gestion de Bibliothèque");
            stage.setScene(scene);
            stage.setWidth(900);
            stage.setHeight(600);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            showError("Erreur lors du chargement de la vue principale: " + e.getMessage());
        }
    }
}
