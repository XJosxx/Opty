package opty;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import opty.config.DatabaseConfig;
import opty.view.LoginController;

public class OptyApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseConfig.load();

        var loader = new FXMLLoader(getClass().getResource("/opty/view/fxml/login-view.fxml"));
        var root = (Parent) loader.load();

        var controller = (LoginController) loader.getController();
        controller.setStage(primaryStage);

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
