package com.example.library.dao;

import com.example.library.model.Book;
import com.example.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Connection connection;

    // Constructeur
    public BookDAO() throws SQLException {
        try {
            this.connection = DatabaseConnection.getInstance();
            if (this.connection == null) {
                throw new SQLException("La connexion à la base de données n'a pas pu être établie.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de BookDAO : " + e.getMessage());
            throw e; // Relancer l'exception après le log
        }
    }

    // Ajouter un livre
    public void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, status, genre, edition) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getStatus());
            pstmt.setString(5, book.getGenre());
            pstmt.setString(6, book.getEdition());
            pstmt.executeUpdate();
        }
    }

    // Supprimer un livre
    public void deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du livre : " + e.getMessage());
            throw e;
        }
    }

    // Récupérer tous les livres
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("status"),
                        rs.getString("genre"),
                        rs.getString("edition")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des livres : " + e.getMessage());
            throw e;
        }

        return books;
    }

    // Met à jour la date d'emprunt et le statut d'un livre
    public void updateBookLoan(Book book) throws SQLException {
        String sql = "UPDATE books SET loanDate = ?, status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (book.getLoanDate() != null) {
                pstmt.setDate(1, Date.valueOf(book.getLoanDate()));
            } else {
                pstmt.setDate(1, null);
            }
            pstmt.setString(2, book.getStatus());
            pstmt.setInt(3, book.getId());
            pstmt.executeUpdate();
        }
    }
}
