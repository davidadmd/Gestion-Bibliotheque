package com.example.library.controller;

import com.example.library.dao.BookDAO;
import com.example.library.dao.LoanDAO;
import com.example.library.dao.UserDAO;
import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LoanController {
    @FXML
    private TextField userNameField; // Champ de saisie pour le nom de l'utilisateur
    @FXML
    private ComboBox<Book> bookCombo;
    @FXML
    private TableView<Loan> loanTable;
    @FXML
    private TableColumn<Loan, String> loanUserColumn;
    @FXML
    private TableColumn<Loan, String> loanBookColumn;
    @FXML
    private TableColumn<Loan, String> loanDateColumn;
    @FXML
    private TableColumn<Loan, String> dueDateColumn;

    private UserDAO userDAO;
    private BookDAO bookDAO;
    private LoanDAO loanDAO;

    public void initialize() {
        // Assurer que le champ utilisateur est éditable (par défaut c'est le cas)
        userNameField.setEditable(true);

        try {
            userDAO = new UserDAO();
            bookDAO = new BookDAO();
            loanDAO = new LoanDAO();

            // Charger la liste des livres dans la ComboBox
            List<Book> books = bookDAO.getAllBooks();
            bookCombo.getItems().clear();
            bookCombo.getItems().addAll(books);

            // Configurer la ComboBox pour n'afficher que "titre - auteur"
            bookCombo.setCellFactory(listView -> new ListCell<Book>() {
                @Override
                protected void updateItem(Book item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle() + " - " + item.getAuthor());
                    }
                }
            });
            bookCombo.setButtonCell(new ListCell<Book>() {
                @Override
                protected void updateItem(Book item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle() + " - " + item.getAuthor());
                    }
                }
            });
            bookCombo.setConverter(new StringConverter<Book>() {
                @Override
                public String toString(Book book) {
                    return (book != null) ? book.getTitle() + " - " + book.getAuthor() : "";
                }

                @Override
                public Book fromString(String string) {
                    // Non utilisé dans ce contexte
                    return null;
                }
            });

            // Configuration des colonnes du TableView
            loanUserColumn.setCellValueFactory(data -> {
                int userId = data.getValue().getUserId();
                try {
                    return new SimpleStringProperty(getUserNameById(userId));
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new SimpleStringProperty("Erreur");
                }
            });
            loanBookColumn.setCellValueFactory(data -> {
                int bookId = data.getValue().getBookId();
                try {
                    return new SimpleStringProperty(getBookTitleById(bookId));
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new SimpleStringProperty("Erreur");
                }
            });
            loanDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoanDate().toString()));
            dueDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDueDate().toString()));

            // Charger la liste des emprunts existants
            refreshLoanTable();
        } catch (SQLException e) {
            showErrorDialog("Erreur lors de l'initialisation : " + e.getMessage());
        }
    }

    @FXML
    private void handleLoan() {
        String userName = userNameField.getText().trim();
        if (userName.isEmpty()) {
            showErrorDialog("Veuillez saisir le nom de l'utilisateur !");
            return;
        }

        // Rechercher l'utilisateur correspondant
        User selectedUser = null;
        try {
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                if (user.getName().equalsIgnoreCase(userName)) {
                    selectedUser = user;
                    break;
                }
            }
            // Si l'utilisateur n'existe pas, le créer automatiquement
            if (selectedUser == null) {
                selectedUser = new User(0, userName, userName.toLowerCase() + "@example.com");
                userDAO.addUser(selectedUser);
            }
        } catch (SQLException e) {
            showErrorDialog("Erreur lors de la récupération ou création de l'utilisateur : " + e.getMessage());
            return;
        }

        Book selectedBook = bookCombo.getValue();
        if (selectedBook == null) {
            showErrorDialog("Veuillez sélectionner un livre !");
            return;
        }

        // Vérifier si le livre est déjà emprunté
        if ("Loaned".equalsIgnoreCase(selectedBook.getStatus())) {
            showErrorDialog("Ce livre est déjà emprunté. Veuillez le retourner avant de le réemprunter.");
            return;
        }

        // Créer l'emprunt
        Loan newLoan = new Loan();
        newLoan.setUserId(selectedUser.getId());
        newLoan.setBookId(selectedBook.getId());
        newLoan.setLoanDate(LocalDate.now());
        newLoan.setDueDate(LocalDate.now().plusWeeks(2)); // délai de 2 semaines

        try {
            // Ajouter l'emprunt en base
            loanDAO.addLoan(newLoan);

            // Mettre à jour le livre : statut et date d'emprunt
            selectedBook.setStatus("Loaned");
            selectedBook.setLoanDate(LocalDate.now());
            bookDAO.updateBookLoan(selectedBook);

            // Rafraîchir la table d'emprunts
            refreshLoanTable();
            showInfoDialog("Emprunt enregistré avec succès !");
        } catch (SQLException e) {
            showErrorDialog("Erreur lors de l'emprunt du livre : " + e.getMessage());
        }
    }

    /**
     * Méthode pour gérer le retour d'un livre.
     * L'utilisateur doit sélectionner un emprunt dans la table, puis cliquer sur "Retourner Livre".
     */
    @FXML
    private void handleReturn() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) {
            showErrorDialog("Veuillez sélectionner un emprunt à restituer !");
            return;
        }
        try {
            // Récupérer le livre correspondant
            Book book = null;
            for (Book b : bookDAO.getAllBooks()) {
                if (b.getId() == selectedLoan.getBookId()) {
                    book = b;
                    break;
                }
            }
            if (book == null) {
                showErrorDialog("Livre introuvable !");
                return;
            }
            // Mettre à jour le livre : le rendre disponible et supprimer la date d'emprunt
            book.setStatus("Available");
            book.setLoanDate(null);
            bookDAO.updateBookLoan(book);

            // Supprimer l'emprunt de la base de données
            loanDAO.deleteLoan(selectedLoan.getId());

            // Rafraîchir la table d'emprunts
            refreshLoanTable();
            showInfoDialog("Le livre a été retourné avec succès !");
        } catch (SQLException e) {
            showErrorDialog("Erreur lors du retour du livre : " + e.getMessage());
        }
    }

    private void refreshLoanTable() throws SQLException {
        loanTable.getItems().clear();
        loanTable.getItems().addAll(loanDAO.getAllLoans());
    }

    // Méthodes utilitaires pour obtenir le nom de l'utilisateur et le titre du livre à partir de leur ID
    private String getUserNameById(int userId) throws SQLException {
        for (User user : userDAO.getAllUsers()) {
            if (user.getId() == userId) {
                return user.getName();
            }
        }
        return "Inconnu";
    }

    private String getBookTitleById(int bookId) throws SQLException {
        for (Book book : bookDAO.getAllBooks()) {
            if (book.getId() == bookId) {
                return book.getTitle();
            }
        }
        return "Inconnu";
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
