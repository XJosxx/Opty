package opty;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OptyApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        var root = new VBox(20);
        root.getStyleClass().add("root");
        root.getChildren().add(new Label("Opty — Cargando..."));

        var scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/opty/view/styles/opty.css").toExternalForm());

        primaryStage.setTitle("Opty — Sistema de Gestión Óptica");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
