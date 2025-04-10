package com.example.library.model;

import java.time.LocalDate;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String status;
    private LocalDate loanDate; // Date d'emprunt, null si non emprunté
    private String genre;
    private String edition;

    // Constructeur complet
    public Book(int id, String title, String author, String isbn, String status, String genre, String edition) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
        this.genre = genre;
        this.edition = edition;
        this.loanDate = null; // Par défaut, le livre n'est pas emprunté
    }

    // Constructeur sans argument (si nécessaire)
    public Book() {
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    /**
     * Retourne la date limite d'emprunt (2 semaines après la date d'emprunt)
     * ou null si le livre n'est pas emprunté.
     */
    public LocalDate getDueDate() {
        return (loanDate != null) ? loanDate.plusWeeks(2) : null;
    }

    /**
     * Vérifie si le livre est en retard d'emprunt (délai de 2 semaines dépassé).
     * Retourne false si le livre n'est pas emprunté.
     */
    public boolean isOverdue() {
        if (loanDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(getDueDate());
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", status='" + status + '\'' +
                ", loanDate=" + loanDate +
                ", genre='" + genre + '\'' +
                ", edition='" + edition + '\'' +
                '}';
    }
}
