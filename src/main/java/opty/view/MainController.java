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

import opty.model.enums.Rol;

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
    @FXML private Button btnProveedores;
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
        UserSession.getInstance().setMainController(this);
        userLabel.setText(usuario.nombreCompleto());
        storeLabel.setText("");
        
        // Aplicar restricciones de visualización basadas en el Rol del usuario
        if (usuario.getRol() == Rol.VENDEDOR) {
            // El Vendedor tiene acceso a Ventas, Pacientes, Inventario, Órdenes, pero NO a la gestión de Usuarios y Proveedores
            btnUsuarios.setVisible(false);
            btnUsuarios.setManaged(false);
            btnProveedores.setVisible(false);
            btnProveedores.setManaged(false);
            onDashboard();
        } else if (usuario.getRol() == Rol.MEDICO) {
            // El Médico (Optometrista) solo debe ver Pacientes y Órdenes de Trabajo para exámenes clínicos y recetas
            btnDashboard.setVisible(false);
            btnDashboard.setManaged(false);
            btnVentas.setVisible(false);
            btnVentas.setManaged(false);
            btnCompras.setVisible(false);
            btnCompras.setManaged(false);
            btnProveedores.setVisible(false);
            btnProveedores.setManaged(false);
            btnInventario.setVisible(false);
            btnInventario.setManaged(false);
            btnUsuarios.setVisible(false);
            btnUsuarios.setManaged(false);
            
            // Su pantalla de inicio por defecto es Pacientes y Consultas en lugar del Dashboard
            onPacientes();
        } else {
            // El ADMIN tiene acceso completo a todo
            onDashboard();
        }
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
    public void onDashboard() {
        loadModule("/opty/view/fxml/dashboard-view.fxml", "Dashboard");
        setActiveButton(btnDashboard);
    }

    @FXML
    public void onPacientes() {
        loadModule("/opty/view/fxml/pacientes-view.fxml", "Pacientes & Consultas");
        setActiveButton(btnPacientes);
    }

    @FXML
    public void onVentas() {
        loadModule("/opty/view/fxml/ventas-view.fxml", "Módulo de Ventas");
        setActiveButton(btnVentas);
    }

    @FXML
    public void onCompras() {
        loadModule("/opty/view/fxml/compras-view.fxml", "Módulo de Compras");
        setActiveButton(btnCompras);
    }

    @FXML
    public void onProveedores() {
        loadModule("/opty/view/fxml/proveedores-view.fxml", "Gestión de Proveedores");
        setActiveButton(btnProveedores);
    }

    @FXML
    public void onInventario() {
        loadModule("/opty/view/fxml/inventario-view.fxml", "Inventario & Kardex");
        setActiveButton(btnInventario);
    }

    @FXML
    public void onUsuarios() {
        loadModule("/opty/view/fxml/usuarios-view.fxml", "Gestión de Usuarios");
        setActiveButton(btnUsuarios);
    }

    @FXML
    public void onOrdenes() {
        loadModule("/opty/view/fxml/ordenes-view.fxml", "Órdenes de Trabajo");
        setActiveButton(btnOrdenes);
    }

    @FXML
    private void onLogout() {
        try {
            UserSession.getInstance().cleanSession();
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
