package com.example.library.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Loan {
    private int id;
    private int bookId;
    private int userId; // Ajout du champ pour stocker l'ID de l'utilisateur qui a effectué l'emprunt
    private String bookTitle;
    private String borrowerName;
    private String borrowerContact;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // "Active", "Returned", "Overdue"
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public Loan(int id, int bookId, String bookTitle, String borrowerName, String borrowerContact, 
                LocalDate loanDate, LocalDate dueDate, LocalDate returnDate, String status) {
        this(id, bookId, 1, bookTitle, borrowerName, borrowerContact, loanDate, dueDate, returnDate, status);
    }
    
    public Loan(int id, int bookId, int userId, String bookTitle, String borrowerName, String borrowerContact, 
                LocalDate loanDate, LocalDate dueDate, LocalDate returnDate, String status) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.borrowerName = borrowerName;
        this.borrowerContact = borrowerContact;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getBookId() {
        return bookId;
    }
    
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public String getBorrowerName() {
        return borrowerName;
    }
    
    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }
    
    public String getBorrowerContact() {
        return borrowerContact;
    }
    
    public void setBorrowerContact(String borrowerContact) {
        this.borrowerContact = borrowerContact;
    }
    
    public LocalDate getLoanDate() {
        return loanDate;
    }
    
    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Méthodes utilitaires pour formater les dates
    public String getFormattedLoanDate() {
        return loanDate != null ? loanDate.format(formatter) : "";
    }
    
    public String getFormattedDueDate() {
        return dueDate != null ? dueDate.format(formatter) : "";
    }
    
    public String getFormattedReturnDate() {
        return returnDate != null ? returnDate.format(formatter) : "-";
    }
    
    // Calculer le nombre de jours restants avant l'échéance
    public int getDaysRemaining() {
        if (dueDate == null) return 0;
        if ("Returned".equals(status)) return 0;
        
        LocalDate today = LocalDate.now();
        return (int) (today.until(dueDate).getDays());
    }
    
    // Vérifier si l'emprunt est en retard
    public boolean isOverdue() {
        if (dueDate == null || "Returned".equals(status)) return false;
        
        LocalDate today = LocalDate.now();
        return today.isAfter(dueDate);
    }
}
