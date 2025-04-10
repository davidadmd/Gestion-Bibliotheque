package com.example.library.controller;

import com.example.library.dao.UserDAO;
import com.example.library.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class UserManagementController {

    @FXML
    private TextField userNameField;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> userIdColumn;
    @FXML
    private TableColumn<User, String> userNameColumn;

    private UserDAO userDAO;
    private ObservableList<User> userList;

    public void initialize() {
        try {
            userDAO = new UserDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        userList = FXCollections.observableArrayList();

        // Configuration des colonnes du TableView
        userIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        // Liaison de la liste des utilisateurs avec le TableView
        userTable.setItems(userList);

        // Chargement des utilisateurs depuis la base de données
        loadUsers();
    }

    @FXML
    private void handleAddUser() {
        String userName = userNameField.getText().trim();
        if (userName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom de l'utilisateur ne peut pas être vide.");
            return;
        }

        User newUser = new User();
        newUser.setName(userName);

        try {
            userDAO.addUser(newUser);
            userList.add(newUser);
            userNameField.clear();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'utilisateur : " + e.getMessage());
        }
    }

    private void loadUsers() {
        try {
            userList.setAll(userDAO.getAllUsers());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les utilisateurs : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
