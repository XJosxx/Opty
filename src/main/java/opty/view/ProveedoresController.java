package opty.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import opty.model.entity.Proveedor;
import opty.model.entity.Usuario;
import opty.service.ProveedorService;
import opty.service.impl.ProveedorServiceImpl;

import java.time.LocalDateTime;

public class ProveedoresController implements ModuleController {

    @FXML private TableView<Proveedor> tableProveedores;
    @FXML private TableColumn<Proveedor, String> colProvId;
    @FXML private TableColumn<Proveedor, String> colProvRuc;
    @FXML private TableColumn<Proveedor, String> colProvEmpresa;
    @FXML private TableColumn<Proveedor, String> colProvContacto;
    @FXML private TableColumn<Proveedor, Label> colProvEstado;

    // --- Campos de Formulario ---
    @FXML private Label lblFormTitle;
    @FXML private TextField txtNombreEmpresa;
    @FXML private TextField txtRuc;
    @FXML private TextField txtNombreContacto;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private CheckBox checkActivo;

    // --- Servicios ---
    private final ProveedorService proveedorService = new ProveedorServiceImpl();

    private Stage stage;
    private Usuario usuarioLogueado;
    private Proveedor proveedorEditando = null;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuarioLogueado = usuario;
        onCargarDatos();
    }

    @FXML
    public void initialize() {
        // Enlazar columnas
        colProvId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getId())));
        colProvRuc.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRuc()));
        colProvEmpresa.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreEmpresa()));
        colProvContacto.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreContacto()));
        
        colProvEstado.setCellValueFactory(cell -> {
            var p = cell.getValue();
            var label = new Label();
            if (p.getActivo() != null && p.getActivo()) {
                label.setText("ACTIVO");
                label.getStyleClass().addAll("badge", "badge-success");
            } else {
                label.setText("INACTIVO");
                label.getStyleClass().addAll("badge", "badge-danger");
            }
            return new SimpleObjectProperty<>(label);
        });

        // Evento de selección en la tabla
        tableProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarProveedorEnForm(newVal);
            }
        });
    }

    private void onCargarDatos() {
        if (usuarioLogueado == null) return;
        var lista = proveedorService.findByTiendaId(usuarioLogueado.getTiendaId());
        tableProveedores.setItems(FXCollections.observableArrayList(lista));
        onNuevoProveedor();
    }

    @FXML
    private void onNuevoProveedor() {
        proveedorEditando = null;
        lblFormTitle.setText("REGISTRAR NUEVO PROVEEDOR");

        txtNombreEmpresa.clear();
        txtRuc.clear();
        txtNombreContacto.clear();
        txtTelefono.clear();
        txtEmail.clear();
        checkActivo.setSelected(true);

        tableProveedores.getSelectionModel().clearSelection();
    }

    private void cargarProveedorEnForm(Proveedor p) {
        proveedorEditando = p;
        lblFormTitle.setText("MODIFICAR PROVEEDOR: " + p.getNombreEmpresa().toUpperCase());

        txtNombreEmpresa.setText(p.getNombreEmpresa());
        txtRuc.setText(p.getRuc());
        txtNombreContacto.setText(p.getNombreContacto());
        txtTelefono.setText(p.getTelefono() != null ? p.getTelefono() : "");
        txtEmail.setText(p.getEmail() != null ? p.getEmail() : "");
        checkActivo.setSelected(p.getActivo() != null ? p.getActivo() : false);
    }

    @FXML
    private void onGuardarProveedor() {
        var empresa = txtNombreEmpresa.getText();
        var ruc = txtRuc.getText();
        var contacto = txtNombreContacto.getText();
        var telf = txtTelefono.getText();
        var email = txtEmail.getText();
        var activo = checkActivo.isSelected();

        if (empresa == null || empresa.isBlank() ||
            ruc == null || ruc.isBlank() ||
            contacto == null || contacto.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Los campos Empresa, RUC y Nombre de Contacto son obligatorios.");
            return;
        }

        try {
            if (proveedorEditando == null) {
                // Modo creación
                var check = proveedorService.findByRuc(ruc);
                if (check.isPresent()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "RUC Duplicado", "Ya existe un proveedor registrado con el RUC ingresado.");
                    return;
                }

                var p = new Proveedor();
                p.setNombreEmpresa(empresa);
                p.setRuc(ruc);
                p.setNombreContacto(contacto);
                p.setTelefono(telf);
                p.setEmail(email);
                p.setActivo(activo);
                p.setTiendaId(usuarioLogueado.getTiendaId());
                p.setFechaRegistro(LocalDateTime.now());

                proveedorService.save(p);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Proveedor Registrado", "El proveedor comercial fue guardado exitosamente.");
            } else {
                // Modo edición
                proveedorEditando.setNombreEmpresa(empresa);
                proveedorEditando.setRuc(ruc);
                proveedorEditando.setNombreContacto(contacto);
                proveedorEditando.setTelefono(telf);
                proveedorEditando.setEmail(email);
                proveedorEditando.setActivo(activo);

                proveedorService.update(proveedorEditando);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Proveedor Actualizado", "Los datos del proveedor comercial fueron actualizados exitosamente.");
            }

            onCargarDatos();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Guardado", "No se pudo guardar la información del proveedor: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        var alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        if (stage != null) alert.initOwner(stage);
        alert.showAndWait();
    }
}
