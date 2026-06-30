package opty.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import opty.model.entity.Usuario;

import java.io.IOException;
import java.util.Arrays;

public class MainController implements ModuleController {

    @FXML private Label userLabel;
    @FXML private Label moduleTitle;
    @FXML private Label storeLabel;
    @FXML private StackPane contentArea;

    @FXML private Button btnDashboard;
    @FXML private Button btnPacientes;
    @FXML private Button btnVentas;
    @FXML private Button btnCompras;
    @FXML private Button btnInventario;
    @FXML private Button btnUsuarios;
    @FXML private Button btnOrdenes;

    private Stage stage;
    private Usuario usuario;
    private Button activeButton;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        userLabel.setText(usuario.nombreCompleto());
        storeLabel.setText("");
        onDashboard();
    }

    private void setActiveButton(Button btn) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("sidebar-btn-active");
        }
        activeButton = btn;
        if (activeButton != null) {
            activeButton.getStyleClass().add("sidebar-btn-active");
        }
    }

    private void loadModule(String fxmlPath, String title) {
        try {
            var loader = new FXMLLoader(getClass().getResource(fxmlPath));
            var view = loader.load();
            var controller = loader.getController();
            if (controller instanceof ModuleController mc) {
                mc.setStage(stage);
                mc.setUsuario(usuario);
            }
            contentArea.getChildren().setAll((Node) view);
            moduleTitle.setText(title);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar módulo: " + fxmlPath, e);
        }
    }

    @FXML
    private void onDashboard() {
        loadModule("/opty/view/fxml/dashboard-view.fxml", "Dashboard");
        setActiveButton(btnDashboard);
    }

    @FXML
    private void onPacientes() {
        moduleTitle.setText("Pacientes — Próximamente");
        setActiveButton(btnPacientes);
        contentArea.getChildren().clear();
    }

    @FXML
    private void onVentas() {
        moduleTitle.setText("Ventas — Próximamente");
        setActiveButton(btnVentas);
        contentArea.getChildren().clear();
    }

    @FXML
    private void onCompras() {
        moduleTitle.setText("Compras — Próximamente");
        setActiveButton(btnCompras);
        contentArea.getChildren().clear();
    }

    @FXML
    private void onInventario() {
        moduleTitle.setText("Inventario — Próximamente");
        setActiveButton(btnInventario);
        contentArea.getChildren().clear();
    }

    @FXML
    private void onUsuarios() {
        moduleTitle.setText("Usuarios — Próximamente");
        setActiveButton(btnUsuarios);
        contentArea.getChildren().clear();
    }

    @FXML
    private void onOrdenes() {
        moduleTitle.setText("Órdenes de Trabajo — Próximamente");
        setActiveButton(btnOrdenes);
        contentArea.getChildren().clear();
    }

    @FXML
    private void onLogout() {
        try {
            var loader = new FXMLLoader(getClass().getResource("/opty/view/fxml/login-view.fxml"));
            var root = (Parent) loader.load();
            var controller = (LoginController) loader.getController();
            controller.setStage(stage);

            var scene = new javafx.scene.Scene(root, 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/opty/view/styles/opty.css").toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException("Error al cerrar sesión", e);
        }
    }
}
