package com.example.library.dao;

import com.example.library.model.Loan;
import com.example.library.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    private Connection connection;
    private BookDAO bookDAO;

    public LoanDAO() throws SQLException {
        try {
            this.connection = DatabaseConnection.getInstance();
            if (this.connection == null) {
                throw new SQLException("La connexion à la base de données n'a pas pu être établie.");
            }
            
            // Créer la table des emprunts si elle n'existe pas
            createLoansTableIfNotExists();
            
            // Initialiser le BookDAO pour les opérations sur les livres
            this.bookDAO = new BookDAO();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de LoanDAO : " + e.getMessage());
            throw e;
        }
    }

    private void createLoansTableIfNotExists() {
        try {
            // Vérifier si la table loans existe
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet tables = meta.getTables(null, null, "loans", null);
            
            if (!tables.next()) {
                // La table n'existe pas, on la crée
                String sql = "CREATE TABLE loans (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "book_id INT NOT NULL, " +
                        "user_id INT NOT NULL DEFAULT 1, " +
                        "borrower_name VARCHAR(100) NOT NULL, " +
                        "borrower_contact VARCHAR(100), " +
                        "loan_date DATE NOT NULL, " +
                        "due_date DATE NOT NULL, " +
                        "return_date DATE, " +
                        "status VARCHAR(20) NOT NULL)";
                
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("Table loans créée avec succès.");
                }
            } else {
                System.out.println("La table loans existe déjà.");
                
                // Vérifier si les colonnes nécessaires existent, les ajouter si besoin
                ensureLoansTableStructure();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification/création de la table loans : " + e.getMessage());
        }
    }
    
    // Assurer que toutes les colonnes nécessaires existent dans la table loans
    private void ensureLoansTableStructure() {
        try {
            // Vérifier si les colonnes nécessaires existent
            boolean hasBorrowerName = columnExists("loans", "borrower_name");
            boolean hasBorrowerContact = columnExists("loans", "borrower_contact");
            boolean hasLoanDate = columnExists("loans", "loan_date");
            boolean hasDueDate = columnExists("loans", "due_date");
            boolean hasReturnDate = columnExists("loans", "return_date");
            boolean hasStatus = columnExists("loans", "status");
            boolean hasUserId = columnExists("loans", "user_id");
            
            // Ajouter les colonnes manquantes
            Statement stmt = connection.createStatement();
            
            if (!hasBorrowerName) {
                stmt.execute("ALTER TABLE loans ADD COLUMN borrower_name VARCHAR(100) NOT NULL DEFAULT 'Emprunteur'");
                System.out.println("Colonne borrower_name ajoutée à la table loans.");
            }
            
            if (!hasBorrowerContact) {
                stmt.execute("ALTER TABLE loans ADD COLUMN borrower_contact VARCHAR(100) NULL");
                System.out.println("Colonne borrower_contact ajoutée à la table loans.");
            }
            
            if (!hasLoanDate) {
                stmt.execute("ALTER TABLE loans ADD COLUMN loan_date DATE NOT NULL DEFAULT CURRENT_DATE");
                System.out.println("Colonne loan_date ajoutée à la table loans.");
            }
            
            if (!hasDueDate) {
                stmt.execute("ALTER TABLE loans ADD COLUMN due_date DATE NOT NULL DEFAULT (CURRENT_DATE + INTERVAL 14 DAY)");
                System.out.println("Colonne due_date ajoutée à la table loans.");
            }
            
            if (!hasReturnDate) {
                stmt.execute("ALTER TABLE loans ADD COLUMN return_date DATE NULL");
                System.out.println("Colonne return_date ajoutée à la table loans.");
            }
            
            if (!hasStatus) {
                stmt.execute("ALTER TABLE loans ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'Active'");
                System.out.println("Colonne status ajoutée à la table loans.");
            }
            
            if (!hasUserId) {
                stmt.execute("ALTER TABLE loans ADD COLUMN user_id INT NOT NULL DEFAULT 1");
                System.out.println("Colonne user_id ajoutée à la table loans.");
            }
            
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la structure de la table loans : " + e.getMessage());
        }
    }
    
    // Vérifier si une colonne existe dans une table
    private boolean columnExists(String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet columns = meta.getColumns(null, null, tableName, columnName);
        return columns.next();
    }

    // Ajouter un nouvel emprunt
    public void addLoan(int bookId, String borrowerName, String borrowerContact, 
                        LocalDate loanDate, LocalDate dueDate) throws SQLException {
        // Vérifier d'abord si toutes les colonnes nécessaires existent
        ensureLoansTableStructure();
        
        // Vérifier si le livre est disponible
        String checkSql = "SELECT status FROM books WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && "Available".equals(rs.getString("status"))) {
                // Le livre est disponible, on peut l'emprunter
                
                try {
                    // 1. Construire la requête en fonction des colonnes disponibles
                    StringBuilder insertFields = new StringBuilder("book_id");
                    StringBuilder insertValues = new StringBuilder("?");
                    List<Object> values = new ArrayList<>();
                    values.add(bookId);
                    
                    // Ajouter les champs en fonction de leur existence
                    if (columnExists("loans", "user_id")) {
                        insertFields.append(", user_id");
                        insertValues.append(", ?");
                        values.add(1); // On utilise 1 par défaut (admin) pour l'utilisateur connecté
                    }
                    
                    if (columnExists("loans", "borrower_name")) {
                        insertFields.append(", borrower_name");
                        insertValues.append(", ?");
                        values.add(borrowerName);
                    }
                    
                    if (columnExists("loans", "borrower_contact")) {
                        insertFields.append(", borrower_contact");
                        insertValues.append(", ?");
                        values.add(borrowerContact);
                    }
                    
                    if (columnExists("loans", "loan_date")) {
                        insertFields.append(", loan_date");
                        insertValues.append(", ?");
                        values.add(Date.valueOf(loanDate));
                    }
                    
                    if (columnExists("loans", "due_date")) {
                        insertFields.append(", due_date");
                        insertValues.append(", ?");
                        values.add(Date.valueOf(dueDate));
                    }
                    
                    if (columnExists("loans", "status")) {
                        insertFields.append(", status");
                        insertValues.append(", ?");
                        values.add("Active");
                    }
                    
                    String insertSql = "INSERT INTO loans (" + insertFields.toString() + ") VALUES (" + insertValues.toString() + ")";
                    
                    try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                        for (int i = 0; i < values.size(); i++) {
                            if (values.get(i) instanceof Integer) {
                                pstmt.setInt(i + 1, (Integer) values.get(i));
                            } else if (values.get(i) instanceof Date) {
                                pstmt.setDate(i + 1, (Date) values.get(i));
                            } else {
                                pstmt.setString(i + 1, (String) values.get(i));
                            }
                        }
                        pstmt.executeUpdate();
                    }
                    
                    // 2. Mettre à jour le statut du livre
                    bookDAO.updateBookStatus(bookId, "Loaned");
                } catch (SQLException e) {
                    throw new SQLException("Erreur lors de l'ajout de l'emprunt : " + e.getMessage());
                }
            } else {
                throw new SQLException("Le livre n'est pas disponible pour l'emprunt.");
            }
        }
    }

    // Retourner un livre
    public void returnBook(int loanId) throws SQLException {
        // 1. Récupérer l'ID du livre associé à cet emprunt
        String getLoanSql = "SELECT book_id FROM loans WHERE id = ?";
        int bookId;
        
        try (PreparedStatement pstmt = connection.prepareStatement(getLoanSql)) {
            pstmt.setInt(1, loanId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                bookId = rs.getInt("book_id");
            } else {
                throw new SQLException("Emprunt introuvable.");
            }
        }
        
        // 2. Mettre à jour l'emprunt avec la date de retour
        String updateLoanSql = "UPDATE loans SET return_date = ?, status = 'Returned' WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateLoanSql)) {
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setInt(2, loanId);
            pstmt.executeUpdate();
        }
        
        // 3. Mettre à jour le statut du livre
        bookDAO.updateBookStatus(bookId, "Available");
    }

    // Récupérer tous les emprunts
    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.title FROM loans l " +
                     "JOIN books b ON l.book_id = b.id " +
                     "ORDER BY l.due_date ASC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loans.add(new Loan(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("borrower_name"),
                    rs.getString("borrower_contact"),
                    rs.getDate("loan_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate(),
                    rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des emprunts : " + e.getMessage());
            throw e;
        }

        return loans;
    }
    
    // Récupérer tous les emprunts actifs
    public List<Loan> getActiveLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.title FROM loans l JOIN books b ON l.book_id = b.id ORDER BY l.due_date ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Gestion de la date de retour qui peut être nulle
                LocalDate returnDate = null;
                if (rs.getDate("return_date") != null) {
                    returnDate = rs.getDate("return_date").toLocalDate();
                }
                
                // Récupération de l'ID utilisateur (default 1 si n'existe pas)
                int userId = 1;
                try {
                    userId = rs.getInt("user_id");
                } catch (Exception e) {
                    // Ignorer et utiliser la valeur par défaut
                }
                
                loans.add(new Loan(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    userId,
                    rs.getString("title"),
                    rs.getString("borrower_name"),
                    rs.getString("borrower_contact"),
                    rs.getDate("loan_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate(),
                    returnDate,
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des emprunts actifs : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return loans;
    }
    
    // Mettre à jour le statut des emprunts en retard
    public void updateOverdueLoans() throws SQLException {
        try {
            // Vérifier d'abord si la colonne status existe
            if (columnExists("loans", "status")) {
                String sql = "UPDATE loans SET status = 'Overdue' " +
                             "WHERE due_date < CURRENT_DATE AND status = 'Active'";
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(sql);
                }
            } else {
                System.out.println("Impossible de mettre à jour les retards : colonne status absente.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour des emprunts en retard : " + e.getMessage());
            throw e;
        }
    }
    
    // Obtenir le nombre d'emprunts actifs
    public int getActiveLoansCount() throws SQLException {
        try {
            // Vérifier d'abord si la colonne status existe
            if (columnExists("loans", "status")) {
                String sql = "SELECT COUNT(*) FROM loans WHERE status = 'Active'";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            } else {
                // Compter tous les emprunts sans filtre si status n'existe pas
                String sql = "SELECT COUNT(*) FROM loans";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des emprunts actifs : " + e.getMessage());
            // Ne pas lever l'exception pour permettre l'exécution même en cas d'erreur
        }
        return 0;
    }
    
    // Obtenir le nombre d'emprunts en retard
    public int getOverdueLoansCount() throws SQLException {
        // Méthode simple mais robuste pour compter les emprunts en retard
        return countOverdueLoans();
    }
    
    // Méthode de secours pour compter correctement les emprunts en retard
    public int countOverdueLoans() {
        int count = 0;
        try {
            // Mettre à jour les emprunts en retard d'abord pour s'assurer que les statuts sont corrects
            try {
                updateOverdueLoans();
            } catch (Exception ex) {
                System.err.println("Erreur lors de la mise à jour des retards, mais on continue : " + ex.getMessage());
            }
            
            // Créer une requête SQL très simple qui ne peut pas échouer
            String sql = "SELECT COUNT(*) FROM loans WHERE due_date < CURRENT_DATE AND return_date IS NULL";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
            
            System.out.println("Nombre d'emprunts en retard (méthode directe) : " + count);
            return count;
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des emprunts en retard (méthode directe) : " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
