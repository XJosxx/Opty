package opty.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import opty.model.entity.ConfigTienda;
import opty.model.entity.Usuario;
import opty.model.enums.Rol;
import opty.model.enums.TipoDocumento;
import opty.service.ConfigTiendaService;
import opty.service.UsuarioService;
import opty.service.impl.ConfigTiendaServiceImpl;
import opty.service.impl.UsuarioServiceImpl;

public class UsuariosController implements ModuleController {

    // --- Tabla de Usuarios ---
    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colUsrId;
    @FXML private TableColumn<Usuario, String> colUsrUsername;
    @FXML private TableColumn<Usuario, String> colUsrNombre;
    @FXML private TableColumn<Usuario, String> colUsrRol;
    @FXML private TableColumn<Usuario, Label> colUsrEstado;

    // --- Campos de Formulario ---
    @FXML private Label lblFormTitle;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoP;
    @FXML private TextField txtApellidoM;
    @FXML private ComboBox<TipoDocumento> comboTipoDoc;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<Rol> comboRol;
    @FXML private ComboBox<ConfigTienda> comboTienda;
    @FXML private CheckBox checkActivo;

    // --- Servicios ---
    private final UsuarioService usuarioService = new UsuarioServiceImpl();
    private final ConfigTiendaService configTiendaService = new ConfigTiendaServiceImpl();

    private Stage stage;
    private Usuario usuarioLogueado;
    private Usuario usuarioEditando = null;

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
        colUsrId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId().toString()));
        colUsrUsername.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        colUsrNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().nombreCompleto()));
        colUsrRol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getRol() != null ? cell.getValue().getRol().name() : "-"
        ));
        colUsrEstado.setCellValueFactory(cell -> {
            var u = cell.getValue();
            var label = new Label();
            if (u.getActivo() != null && u.getActivo()) {
                label.setText("ACTIVO");
                label.getStyleClass().addAll("badge", "badge-success");
            } else {
                label.setText("INACTIVO");
                label.getStyleClass().addAll("badge", "badge-danger");
            }
            return new SimpleObjectProperty<>(label);
        });

        // Evento de selección en la tabla
        tableUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarUsuarioEnForm(newVal);
            }
        });

        // Poblar Roles y Tipo de Documento
        comboRol.setItems(FXCollections.observableArrayList(Rol.values()));
        comboTipoDoc.setItems(FXCollections.observableArrayList(TipoDocumento.values()));
    }

    private void onCargarDatos() {
        var usuarios = usuarioService.findAll();
        tableUsuarios.setItems(FXCollections.observableArrayList(usuarios));

        var tiendas = configTiendaService.findAll();
        comboTienda.setItems(FXCollections.observableArrayList(tiendas));

        onNuevoUsuario(); // Resetear el formulario a modo de creación
    }

    @FXML
    private void onNuevoUsuario() {
        usuarioEditando = null;
        lblFormTitle.setText("REGISTRAR NUEVO USUARIO");

        txtNombre.clear();
        txtApellidoP.clear();
        txtApellidoM.clear();
        if (comboTipoDoc != null) comboTipoDoc.getSelectionModel().select(TipoDocumento.DNI);
        txtDocumento.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtPassword.setPromptText("Contraseña de acceso...");
        comboRol.getSelectionModel().clearSelection();
        comboTienda.getSelectionModel().clearSelection();
        checkActivo.setSelected(true);

        tableUsuarios.getSelectionModel().clearSelection();
    }

    private void cargarUsuarioEnForm(Usuario u) {
        usuarioEditando = u;
        lblFormTitle.setText("MODIFICAR USUARIO: " + u.getUsername().toUpperCase());

        txtNombre.setText(u.getNombre());
        txtApellidoP.setText(u.getApellidoP());
        txtApellidoM.setText(u.getApellidoM());
        if (comboTipoDoc != null) comboTipoDoc.setValue(u.getTipoDocumento());
        txtDocumento.setText(u.getNumDocumento());
        txtUsername.setText(u.getUsername());
        txtPassword.clear();
        txtPassword.setPromptText("(Sin cambios si se deja vacío)");

        comboRol.setValue(u.getRol());

        // Seleccionar sucursal correspondiente
        for (var tienda : comboTienda.getItems()) {
            if (tienda.getId().equals(u.getTiendaId())) {
                comboTienda.setValue(tienda);
                break;
            }
        }

        checkActivo.setSelected(u.getActivo() != null ? u.getActivo() : false);
    }

    @FXML
    private void onGuardarUsuario() {
        var nombre = txtNombre.getText();
        var apellidoP = txtApellidoP.getText();
        var apellidoM = txtApellidoM.getText();
        var tipoDoc = comboTipoDoc.getValue();
        var doc = txtDocumento.getText();
        var username = txtUsername.getText();
        var rol = comboRol.getValue();
        var tienda = comboTienda.getValue();
        var activo = checkActivo.isSelected();

        if (nombre == null || nombre.isBlank() ||
            apellidoP == null || apellidoP.isBlank() ||
            apellidoM == null || apellidoM.isBlank() ||
            tipoDoc == null ||
            doc == null || doc.isBlank() ||
            username == null || username.isBlank() ||
            rol == null || tienda == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Todos los campos (incluyendo el Tipo de Documento) son requeridos.");
            return;
        }

        try {
            if (usuarioEditando == null) {
                // Modo creación
                var pass = txtPassword.getText();
                if (pass == null || pass.isBlank()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Contraseña Requerida", "Debe establecer una contraseña para el nuevo usuario.");
                    return;
                }

                // Verificar si username ya existe
                var check = usuarioService.findByUsername(username);
                if (check.isPresent()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Usuario Duplicado", "El nombre de usuario ingresado ya está en uso.");
                    return;
                }

                var u = new Usuario();
                u.setNombre(nombre);
                u.setApellidoP(apellidoP);
                u.setApellidoM(apellidoM);
                u.setTipoDocumento(tipoDoc);
                u.setNumDocumento(doc);
                u.setUsername(username.trim().toLowerCase());
                u.setPassword(pass);
                u.setRol(rol);
                u.setTiendaId(tienda.getId());
                u.setActivo(activo);

                usuarioService.save(u);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario Creado", "El usuario fue guardado exitosamente.");
            } else {
                // Modo edición
                usuarioEditando.setNombre(nombre);
                usuarioEditando.setApellidoP(apellidoP);
                usuarioEditando.setApellidoM(apellidoM);
                usuarioEditando.setTipoDocumento(tipoDoc);
                usuarioEditando.setNumDocumento(doc);
                usuarioEditando.setUsername(username.trim().toLowerCase());
                usuarioEditando.setRol(rol);
                usuarioEditando.setTiendaId(tienda.getId());
                usuarioEditando.setActivo(activo);

                var pass = txtPassword.getText();
                if (pass != null && !pass.isBlank()) {
                    usuarioEditando.setPassword(pass);
                }

                usuarioService.update(usuarioEditando);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario Actualizado", "El usuario fue actualizado de manera exitosa.");
            }

            onCargarDatos(); // Recargar tabla y limpiar campos

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Guardado", "No se pudo guardar la información del usuario: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        var alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
