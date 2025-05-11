package com.example.library.controller;

import com.example.library.dao.BookDAO;
import com.example.library.model.Book;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class LibraryController {
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book,String> titleColumn;
    @FXML private TableColumn<Book,String> authorColumn;
    @FXML private TableColumn<Book,String> genreColumn;
    @FXML private TableColumn<Book,String> editionColumn;
    @FXML private TableColumn<Book,String> isbnColumn;
    @FXML private TableColumn<Book,String> statusColumn;
    @FXML private TableColumn<Book,String> loanDateColumn;

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField genreField;
    @FXML private TextField editionField;

    private BookDAO bookDAO;

    @FXML
    public void initialize() {
        // Initialisation des colonnes
        titleColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        authorColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAuthor()));
        genreColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGenre()));
        editionColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEdition()));
        isbnColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIsbn()));
        statusColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        loanDateColumn.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getLoanDate()!=null
                        ? d.getValue().getLoanDate().toString()
                        : ""
        ));

        // Coloration des lignes en retard (orange)
        bookTable.setRowFactory(tv -> new TableRow<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getLoanDate() == null) {
                    setStyle("");
                } else if (LocalDate.now().isAfter(item.getLoanDate().plusWeeks(2))) {
                    setStyle("-fx-background-color: orange;");
                } else {
                    setStyle("");
                }
            }
        });

        // Chargement des livres depuis la BDD
        try {
            bookDAO = new BookDAO();
            bookTable.getItems().setAll(bookDAO.getAllBooks());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", e.getMessage());
        }
    }

    @FXML
    private void addBook() {
        if (titleField.getText().isBlank() || authorField.getText().isBlank()
                || genreField.getText().isBlank()  || editionField.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs !");
            return;
        }
        Book b = new Book(0,
                titleField.getText().trim(),
                authorField.getText().trim(),
                "ISBN"+System.currentTimeMillis(),
                "Available",
                genreField.getText().trim(),
                editionField.getText().trim()
        );
        try {
            bookDAO.addBook(b);
            bookTable.getItems().add(b);
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Ajout réussi", "Livre ajouté !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur ajout", e.getMessage());
        }
    }

    @FXML
    private void deleteBook() {
        Book sel = bookTable.getSelectionModel().getSelectedItem();
        if (sel==null) {
            showAlert(Alert.AlertType.ERROR, "Aucune sélection", "Sélectionnez un livre !");
            return;
        }
        try {
            bookDAO.deleteBook(sel.getId());
            bookTable.getItems().remove(sel);
            showAlert(Alert.AlertType.INFORMATION, "Suppression", "Livre supprimé !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur suppression", e.getMessage());
        }
    }

    /**
     * Ouvre une nouvelle fenêtre pour afficher les logs d'emprunts.
     * Implémentation standard JavaFX pour charger un FXML dans un nouveau Stage :contentReference[oaicite:2]{index=2}.
     */
    @FXML
    private void openLogs() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("logs_view.fxml"));
            Stage  st   = new Stage();
            st.setTitle("Historique des Emprunts");
            st.setScene(new Scene(root));
            st.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        titleField.clear(); authorField.clear();
        genreField.clear(); editionField.clear();
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert a = new Alert(type);
        a.setTitle(header);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
