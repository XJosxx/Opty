package opty.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import opty.service.LoginService;
import opty.service.impl.LoginServiceImpl;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final LoginService loginService = new LoginServiceImpl();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        var opt = loginService.login(usernameField.getText(), passwordField.getText());
        if (opt.isEmpty()) {
            errorLabel.setText("Usuario o contraseña incorrectos");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }

        var usuario = opt.get();
        loginService.updateLastAccess(usuario.getId());
        UserSession.getInstance().setUsuario(usuario);

        try {
            var loader = new FXMLLoader(getClass().getResource("/opty/view/fxml/main-view.fxml"));
            var root = (Parent) loader.load();
            var controller = (MainController) loader.getController();
            controller.setStage(stage);
            controller.setUsuario(usuario);

            var scene = new Scene(root, 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/opty/view/styles/opty.css").toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            errorLabel.setText("Error al cargar la aplicación: " + e.getMessage());
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
}
