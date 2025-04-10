package com.example.library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Création d'un TabPane
        TabPane tabPane = new TabPane();

        // Chargement de la vue de la bibliothèque (Library)
        FXMLLoader libraryLoader = new FXMLLoader(getClass().getResource("library_view.fxml"));
        Parent libraryRoot = libraryLoader.load();
        Tab libraryTab = new Tab("Bibliothèque", libraryRoot);
        libraryTab.setClosable(false);

        // Chargement de la vue des emprunts (Loan)
        FXMLLoader loanLoader = new FXMLLoader(getClass().getResource("loan_view.fxml"));
        Parent loanRoot = loanLoader.load();
        Tab loanTab = new Tab("Emprunts", loanRoot);
        loanTab.setClosable(false);

        // Ajout des onglets au TabPane
        tabPane.getTabs().addAll(libraryTab, loanTab);

        // Création et affichage de la scène
        Scene scene = new Scene(tabPane, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Gestion de Bibliothèque");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
