package com.example.library.controller;

import com.example.library.dao.LoanDAO;
import com.example.library.model.Loan;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;

public class LogsController {

    @FXML private TableView<Loan> logsTable;
    @FXML private TableColumn<Loan, String> userColumn;
    @FXML private TableColumn<Loan, String> bookColumn;
    @FXML private TableColumn<Loan, String> loanDateColumn;
    @FXML private TableColumn<Loan, String> dueDateColumn;
    @FXML private TableColumn<Loan, String> returnDateColumn;

    private LoanDAO loanDAO;

    @FXML
    public void initialize() {
        try {
            loanDAO = new LoanDAO();

            userColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(loanDAO.getUserNameByLoan(data.getValue()))
            );
            bookColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(loanDAO.getBookTitleByLoan(data.getValue()))
            );
            loanDateColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getLoanDate().toString())
            );
            dueDateColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().getDueDate().toString())
            );
            returnDateColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(
                            data.getValue().getReturnDate() != null
                                    ? data.getValue().getReturnDate().toString()
                                    : ""
                    )
            );

            logsTable.getItems().setAll(loanDAO.getAllLoansWithReturnDate());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
