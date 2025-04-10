package com.example.library.dao;

import com.example.library.model.Loan;
import com.example.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    private Connection connection;

    public LoanDAO() throws SQLException {
        this.connection = DatabaseConnection.getInstance();
    }

    // Ajouter un nouvel emprunt
    public void addLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (user_id, book_id, loan_date, due_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loan.getUserId());
            pstmt.setInt(2, loan.getBookId());
            pstmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            pstmt.setDate(4, Date.valueOf(loan.getDueDate()));
            pstmt.executeUpdate();
        }
    }

    // Récupérer tous les emprunts
    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("book_id"),
                        rs.getDate("loan_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate()
                ));
            }
        }
        return loans;
    }

    // Supprimer un emprunt (par exemple lors du retour du livre)
    public void deleteLoan(int id) throws SQLException {
        String sql = "DELETE FROM loans WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
