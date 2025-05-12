package com.example.library.controller;

import com.example.library.Main;
import com.example.library.dao.BookDAO;
import com.example.library.dao.LoanDAO;
import com.example.library.model.Book;
import com.example.library.model.Loan;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryController {
    // Onglets et contrôles généraux
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Label statusLabel;
    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label availableBooksLabel;
    @FXML
    private Label loanedBooksLabel;
    
    // Onglet 1: Liste des livres
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeComboBox;
    @FXML
    private TableView<Book> allBooksTable;
    @FXML
    private TableColumn<Book, String> allTitleColumn;
    @FXML
    private TableColumn<Book, String> allAuthorColumn;
    @FXML
    private TableColumn<Book, String> allGenreColumn;
    @FXML
    private TableColumn<Book, String> allEditionColumn;
    @FXML
    private TableColumn<Book, String> allIsbnColumn;
    @FXML
    private TableColumn<Book, String> allStatusColumn;
    
    // Onglet 2: Gestion des livres
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> genreColumn;
    @FXML
    private TableColumn<Book, String> editionColumn;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, String> statusColumn;
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField genreField;
    @FXML
    private TextField editionField;
    @FXML
    private Button saveBookButton;
    

    
    // Onglet 3: Gestion des emprunts
    @FXML
    private ComboBox<Book> bookComboBox;
    @FXML
    private TextField borrowerNameField;
    @FXML
    private TextField borrowerContactField;
    @FXML
    private DatePicker loanDatePicker;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private TableView<Loan> loansTable;
    @FXML
    private TableColumn<Loan, Integer> loanIdColumn;
    @FXML
    private TableColumn<Loan, String> bookTitleColumn;
    @FXML
    private TableColumn<Loan, String> borrowerNameColumn;
    @FXML
    private TableColumn<Loan, String> borrowerContactColumn;
    @FXML
    private TableColumn<Loan, String> loanDateColumn;
    @FXML
    private TableColumn<Loan, String> dueDateColumn;
    @FXML
    private TableColumn<Loan, Integer> daysRemainingColumn;
    @FXML
    private TableColumn<Loan, String> loanStatusColumn;
    @FXML
    private Label activeLoansLabel;
    @FXML
    private Label overdueLoansLabel;
    
    // DAOs
    private BookDAO bookDAO;
    private LoanDAO loanDAO;
    
    // Collections pour les données
    private ObservableList<Book> allBooks = FXCollections.observableArrayList();
    private ObservableList<Loan> activeLoans = FXCollections.observableArrayList();

    public void initialize() {
        statusLabel.setText("Prêt");
        
        // Configuration de l'interface
        searchTypeComboBox.getItems().addAll("Titre", "Auteur", "Genre", "ISBN", "Status");
        searchTypeComboBox.setValue("Titre");
        setupMainBooksList();
        setupBookManagement();
        setupLoans();
        
        // Chargement des données
        loadData();
    }
    
    private void setupMainBooksList() {
        // Configuration des colonnes avec les propriétés des livres
        allTitleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        allAuthorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        allGenreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));
        allEditionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEdition()));
        allIsbnColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        allStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        allBooksTable.setItems(allBooks);
    }
    
    private void setupBookManagement() {
        // Même configuration mais pour la table de gestion des livres
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));
        editionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEdition()));
        isbnColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        bookTable.setItems(allBooks);
    }

    
    private void setupLoans() {
        try {
            // Configurer les colonnes des emprunts
            loanIdColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
            bookTitleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookTitle()));
            borrowerNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBorrowerName()));
            borrowerContactColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBorrowerContact()));
            loanDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormattedLoanDate()));
            dueDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormattedDueDate()));
            daysRemainingColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDaysRemaining()).asObject());
            loanStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
            
            // Mise en forme des lignes selon l'état de l'emprunt
            loansTable.setRowFactory(tv -> new TableRow<Loan>() {
                @Override
                protected void updateItem(Loan loan, boolean empty) {
                    super.updateItem(loan, empty);
                    if (loan == null || empty) {
                        setStyle("");
                    } else if ("Overdue".equals(loan.getStatus())) {
                        setStyle("-fx-background-color: #ffcccc;"); // Rouge pour retards
                    } else if (loan.getDaysRemaining() <= 3 && loan.getDaysRemaining() >= 0) {
                        setStyle("-fx-background-color: #ffffcc;"); // Jaune pour échéances proches
                    } else {
                        setStyle("");
                    }
                }
            });
            
            loansTable.setItems(activeLoans);
            
            // Configurer l'affichage des livres dans la ComboBox
            bookComboBox.setConverter(new StringConverter<Book>() {
                @Override
                public String toString(Book book) {
                    return book != null ? book.getTitle() + " (" + book.getAuthor() + ")" : "";
                }
                
                @Override
                public Book fromString(String string) {
                    return null;
                }
            });
            
            // Initialiser avec les dates par défaut
            loanDatePicker.setValue(LocalDate.now());
            dueDatePicker.setValue(LocalDate.now().plusDays(14));
        } catch (Exception e) {
            System.err.println("Erreur: configuration des emprunts - " + e.getMessage());
        }
    }
    
    private void loadData() {
        try {
            // Initialisation des DAOs
            bookDAO = new BookDAO();
            loanDAO = new LoanDAO();
            
            // Charger les livres
            refreshBookData();
            
            // Charger les emprunts
            loadLoans();
            
            statusLabel.setText("Données chargées avec succès");
        } catch (SQLException e) {
            showErrorDialog("Erreur lors du chargement des données : " + e.getMessage());
            statusLabel.setText("Erreur lors du chargement des données");
        }
    }
    
    private void refreshBookData() throws SQLException {
        // Charger tous les livres
        List<Book> books = bookDAO.getAllBooks();
        allBooks.clear();
        allBooks.addAll(books);
        
        // Actualiser les statistiques et la liste de livres disponibles
        updateBookStats();
        updateAvailableBooks();
    }

    @FXML
    private void addBook() {
        // Récupérer les valeurs des champs
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre = genreField.getText().trim();
        String edition = editionField.getText().trim();

        // Valider les données
        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || edition.isEmpty()) {
            showErrorDialog("Veuillez remplir tous les champs !");
            return;
        }

        try {
            // Créer et ajouter le livre
            Book book = new Book(0, title, author, generateISBN(), "Available", genre, edition);
            bookDAO.addBook(book);
            
            // Actualiser les données
            refreshBookData();
            clearFields();
            
            // Confirmation
            showInfoDialog("Livre ajouté avec succès !");
            statusLabel.setText("Livre ajouté: " + title);
        } catch (SQLException e) {
            showErrorDialog("Erreur lors de l'ajout du livre : " + e.getMessage());
            statusLabel.setText("Erreur lors de l'ajout du livre");
        }
    }

    @FXML
    private void deleteBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showErrorDialog("Veuillez sélectionner un livre à supprimer !");
            return;
        }

        try {
            bookDAO.deleteBook(selectedBook.getId()); // Appel à la méthode delete dans BookDAO
            allBooks.remove(selectedBook); // Retirer le livre de la liste observable (mise à jour des deux tableaux)
            updateBookStats();
            

            
            showInfoDialog("Livre supprimé avec succès !");
            statusLabel.setText("Livre supprimé: " + selectedBook.getTitle());
        } catch (SQLException e) {
            showErrorDialog("Erreur lors de la suppression du livre : " + e.getMessage());
            statusLabel.setText("Erreur lors de la suppression du livre");
        }
    }

    @FXML
    private void clearFields() {
        titleField.clear();
        authorField.clear();
        genreField.clear();
        editionField.clear();
        statusLabel.setText("Champs réinitialisés");
    }

    @FXML
    private void editBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showErrorDialog("Veuillez sélectionner un livre à modifier !");
            return;
        }
        
        // Remplir les champs avec les données du livre sélectionné
        titleField.setText(selectedBook.getTitle());
        authorField.setText(selectedBook.getAuthor());
        genreField.setText(selectedBook.getGenre());
        editionField.setText(selectedBook.getEdition());
        
        // Stocker l'ID du livre sélectionné comme propriété temporaire pour l'utiliser lors de la sauvegarde
        titleField.getProperties().put("selectedBookId", selectedBook.getId());
        titleField.getProperties().put("selectedBookIsbn", selectedBook.getIsbn());
        titleField.getProperties().put("selectedBookStatus", selectedBook.getStatus());
        
        statusLabel.setText("Modification du livre: " + selectedBook.getTitle());
    }
    
    @FXML
    private void saveBookChanges() {
        // Vérifier que les champs ne sont pas vides
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre = genreField.getText().trim();
        String edition = editionField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || edition.isEmpty()) {
            showErrorDialog("Veuillez remplir tous les champs !");
            return;
        }
        
        // Récupérer l'ID du livre sélectionné depuis les propriétés temporaires
        if (!titleField.getProperties().containsKey("selectedBookId")) {
            showErrorDialog("Erreur: Aucun livre sélectionné pour la modification");
            return;
        }
        
        int bookId = (int) titleField.getProperties().get("selectedBookId");
        String isbn = (String) titleField.getProperties().get("selectedBookIsbn");
        String status = (String) titleField.getProperties().get("selectedBookStatus");
        
        // Créer un nouvel objet Book avec les données modifiées
        Book updatedBook = new Book(bookId, title, author, isbn, status, genre, edition);
        
        try {
            // Mettre à jour le livre dans la base de données
            bookDAO.updateBook(updatedBook);
            
            // Ajouter le livre mis à jour à la liste observable
            allBooks.add(updatedBook);
            
            // Mettre à jour les statistiques
            updateBookStats();
            

            
            // Nettoyer les champs et les propriétés temporaires
            clearFields();
            titleField.getProperties().remove("selectedBookId");
            titleField.getProperties().remove("selectedBookIsbn");
            titleField.getProperties().remove("selectedBookStatus");
            
            showInfoDialog("Livre modifié avec succès !");
            statusLabel.setText("Livre modifié: " + title);
        } catch (SQLException e) {
            showErrorDialog("Erreur lors de la modification du livre : " + e.getMessage());
            statusLabel.setText("Erreur lors de la modification du livre");
        }
    }
    
    @FXML
    private void changeStatus() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showErrorDialog("Veuillez sélectionner un livre pour changer son statut !");
            return;
        }
        
        String currentStatus = selectedBook.getStatus();
        String newStatus;
        
        if ("Available".equals(currentStatus)) {
            newStatus = "Loaned";
        } else {
            newStatus = "Available";
        }
        
        try {
            // Mise à jour du statut dans la base de données
            bookDAO.updateBookStatus(selectedBook.getId(), newStatus);
            
            // Mise à jour du statut dans l'objet
            selectedBook.setStatus(newStatus);
            
            // Forcer le rafraîchissement des tableaux
            allBooksTable.refresh();
            bookTable.refresh();
            
            // Mettre à jour la liste des livres disponibles pour l'emprunt
            updateAvailableBooks();
            
            updateBookStats();
            

            
            showInfoDialog("Statut du livre modifié avec succès !");
            statusLabel.setText("Statut modifié: " + selectedBook.getTitle() + " -> " + newStatus);
        } catch (SQLException e) {
            showErrorDialog("Erreur lors de la modification du statut : " + e.getMessage());
            statusLabel.setText("Erreur lors de la modification du statut");
        }
    }
    
    @FXML
    private void searchBooks() {
        String searchText = searchField.getText().trim().toLowerCase();
        String searchType = searchTypeComboBox.getValue();
        
        if (searchText.isEmpty()) {
            // Si le champ de recherche est vide, afficher tous les livres
            allBooksTable.setItems(allBooks);
            return;
        }
        
        // Filtrer les livres selon le critère de recherche
        List<Book> filteredBooks = allBooks.stream().filter(book -> {
            switch (searchType) {
                case "Titre":
                    return book.getTitle().toLowerCase().contains(searchText);
                case "Auteur":
                    return book.getAuthor().toLowerCase().contains(searchText);
                case "Genre":
                    return book.getGenre().toLowerCase().contains(searchText);
                case "ISBN":
                    return book.getIsbn().toLowerCase().contains(searchText);
                case "Status":
                    return book.getStatus().toLowerCase().contains(searchText);
                default:
                    return false;
            }
        }).collect(Collectors.toList());
        
        ObservableList<Book> filteredList = FXCollections.observableArrayList(filteredBooks);
        allBooksTable.setItems(filteredList);
        
        statusLabel.setText("Recherche: " + filteredBooks.size() + " livre(s) trouvé(s)");
    }
    
    @FXML
    private void refreshData() {
        try {
            // Actualiser toutes les données
            refreshBookData();
            loadLoans();
            
            // Réinitialiser la recherche
            searchField.clear();
            allBooksTable.setItems(allBooks);
            
            statusLabel.setText("Données rafraîchies avec succès");
        } catch (SQLException e) {
            showErrorDialog("Erreur lors du rafraîchissement des données : " + e.getMessage());
            statusLabel.setText("Erreur lors du rafraîchissement");
        }
    }
    

    
    // Méthodes pour la gestion des emprunts
    private void updateAvailableBooks() {
        try {
            // Rafraîchir les livres depuis la base de données
            List<Book> freshBooks = bookDAO.getAllBooks();
            allBooks.clear();
            allBooks.addAll(freshBooks);
            
            // Mettre à jour la liste déroulante avec tous les livres
            bookComboBox.setItems(allBooks);
            
            // Compter les livres disponibles
            long availableCount = allBooks.stream()
                .filter(book -> "Available".equals(book.getStatus()))
                .count();
            
            // Actualiser l'interface
            if (allBooks.isEmpty()) {
                statusLabel.setText("Aucun livre dans la bibliothèque");
            } else {
                statusLabel.setText(allBooks.size() + " livre(s) au total, dont " + availableCount + " disponible(s)");
            }
        } catch (Exception e) {
            System.err.println("Erreur dans updateAvailableBooks: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadLoans() {
        try {
            // Vérifier et mettre à jour les emprunts en retard
            loanDAO.updateOverdueLoans();
            
            // Charger les emprunts actifs
            List<Loan> loans = loanDAO.getActiveLoans();
            activeLoans.clear();
            activeLoans.addAll(loans);
            
            // Mettre à jour les compteurs
            int activeCount = loanDAO.getActiveLoansCount();
            int overdueCount = loanDAO.getOverdueLoansCount();
            
            activeLoansLabel.setText(String.valueOf(activeCount));
            overdueLoansLabel.setText(String.valueOf(overdueCount));
            
        } catch (SQLException e) {
            showErrorDialog("Erreur lors du chargement des emprunts : " + e.getMessage());
        }
    }
    
    @FXML
    private void refreshLoans() {
        try {
            // Recharger les livres pour mettre à jour les statuts
            List<Book> books = bookDAO.getAllBooks();
            allBooks.clear();
            allBooks.addAll(books);
            
            // Mettre à jour la liste des livres disponibles
            updateAvailableBooks();
            
            // Recharger les emprunts
            loadLoans();
            
            // Mettre à jour les statistiques
            updateBookStats();
            

            
            statusLabel.setText("Liste des emprunts rafraîchie");
        } catch (SQLException e) {
            showErrorDialog("Erreur lors du rafraîchissement des emprunts : " + e.getMessage());
            statusLabel.setText("Erreur lors du rafraîchissement des emprunts");
        }
    }
    
    @FXML
    private void checkOverdueLoans() {
        try {
            // Vérifier et mettre à jour les emprunts en retard
            loanDAO.updateOverdueLoans();
            
            // Recharger les emprunts
            loadLoans();
            

            
            statusLabel.setText("Vérification des retards effectuée");
        } catch (SQLException e) {
            showErrorDialog("Erreur lors de la vérification des retards : " + e.getMessage());
            statusLabel.setText("Erreur lors de la vérification des retards");
        }
    }
    
    @FXML
    private void saveLoan() {
        // Récupération des valeurs
        Book selectedBook = bookComboBox.getValue();
        String borrowerName = borrowerNameField.getText().trim();
        String borrowerContact = borrowerContactField.getText().trim();
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate dueDate = dueDatePicker.getValue();
        
        // Validation complète des données
        if (!validateLoanData(selectedBook, borrowerName, loanDate, dueDate)) {
            return;
        }
        
        try {
            // Enregistrer l'emprunt et mettre à jour le statut du livre
            loanDAO.addLoan(selectedBook.getId(), borrowerName, borrowerContact, loanDate, dueDate);
            bookDAO.updateBookStatus(selectedBook.getId(), "Loaned");
            
            // Actualiser les données
            refreshBookData();
            loadLoans();
            
            // Réinitialiser et confirmer
            clearLoanFields();
            showInfoDialog("Emprunt enregistré avec succès !");
            statusLabel.setText("Emprunt enregistré pour " + borrowerName);
        } catch (SQLException e) {
            showErrorDialog("Erreur lors de l'enregistrement de l'emprunt : " + e.getMessage());
            statusLabel.setText("Erreur lors de l'enregistrement de l'emprunt");
        }
    }
    
    private boolean validateLoanData(Book selectedBook, String borrowerName, LocalDate loanDate, LocalDate dueDate) {
        if (selectedBook == null) {
            showErrorDialog("Veuillez sélectionner un livre !");
            return false;
        }
        
        if (!"Available".equals(selectedBook.getStatus())) {
            showErrorDialog("Le livre '" + selectedBook.getTitle() + "' n'est pas disponible ! Statut actuel : " + selectedBook.getStatus());
            return false;
        }
        
        if (borrowerName.isEmpty()) {
            showErrorDialog("Veuillez entrer le nom de l'emprunteur !");
            return false;
        }
        
        if (loanDate == null || dueDate == null) {
            showErrorDialog("Veuillez sélectionner les dates d'emprunt et de retour !");
            return false;
        }
        
        if (dueDate.isBefore(loanDate)) {
            showErrorDialog("La date de retour ne peut pas être antérieure à la date d'emprunt !");
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void returnBook() {
        Loan selectedLoan = loansTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) {
            showErrorDialog("Veuillez sélectionner un emprunt à retourner !");
            return;
        }
        
        try {
            // Effectuer le retour et mettre à jour le statut
            loanDAO.returnBook(selectedLoan.getId());
            bookDAO.updateBookStatus(selectedLoan.getBookId(), "Available");
            
            // Actualiser les données
            refreshBookData();
            loadLoans();
            
            // Confirmer
            showInfoDialog("Livre retourné avec succès !");
            statusLabel.setText("Livre " + selectedLoan.getBookTitle() + " retourné");
        } catch (SQLException e) {
            showErrorDialog("Erreur lors du retour du livre : " + e.getMessage());
            statusLabel.setText("Erreur lors du retour du livre");
        }
    }
    
    @FXML
    private void clearLoanFields() {
        bookComboBox.setValue(null);
        borrowerNameField.clear();
        borrowerContactField.clear();
        loanDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
    }
    

    
    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("login_view.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            
            // Changer la scène
            stage.setTitle("Connexion - Logiciel de Gestion de Bibliothèque");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            showErrorDialog("Erreur lors de la déconnexion : " + e.getMessage());
        }
    }
    
    private void updateBookStats() {
        int total = allBooks.size();
        int available = 0;
        int loaned = 0;
        
        for (Book book : allBooks) {
            if ("Available".equals(book.getStatus())) {
                available++;
            } else if ("Loaned".equals(book.getStatus())) {
                loaned++;
            }
        }
        
        totalBooksLabel.setText(String.valueOf(total));
        availableBooksLabel.setText(String.valueOf(available));
        loanedBooksLabel.setText(String.valueOf(loaned));
    }

    private String generateISBN() {
        return "ISBN" + System.currentTimeMillis();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoDialog(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
