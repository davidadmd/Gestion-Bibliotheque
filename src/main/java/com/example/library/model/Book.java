package com.example.library.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String status;
    private String genre;    // Nouveau champ pour le genre
    private String edition;  // Nouveau champ pour l'édition

    // Constructeur complet
    public Book(int id, String title, String author, String isbn, String status, String genre, String edition) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
        this.genre = genre;
        this.edition = edition;
    }

    // Constructeur sans argument (utile pour JavaFX ou frameworks)
    public Book() {
    }

    // Getters et Setters pour les nouveaux champs
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

    // Getters et Setters pour les autres champs
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

    // Méthode toString (utile pour le débogage)
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", status='" + status + '\'' +
                ", genre='" + genre + '\'' +
                ", edition='" + edition + '\'' +
                '}';
    }
}
