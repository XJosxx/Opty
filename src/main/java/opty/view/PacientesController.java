package opty.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import opty.model.entity.Consulta;
import opty.model.entity.HistorialClinico;
import opty.model.entity.Paciente;
import opty.model.entity.Usuario;
import opty.model.entity.Producto;
import opty.model.enums.TipoComprobante;
import opty.model.enums.TipoDocumento;
import opty.service.ConsultaService;
import opty.service.PacienteService;
import opty.service.ProductoService;
import opty.service.impl.ConsultaServiceImpl;
import opty.service.impl.PacienteServiceImpl;
import opty.service.impl.ProductoServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PacientesController implements ModuleController {

    // --- Columna Izquierda ---
    @FXML private TextField txtBuscar;
    @FXML private TableView<Paciente> tablePacientes;
    @FXML private TableColumn<Paciente, String> colDocumento;
    @FXML private TableColumn<Paciente, String> colNombre;
    @FXML private TableColumn<Paciente, String> colTelefono;

    // --- Columna Derecha: Vista Detalle ---
    @FXML private VBox paneDetalle;
    @FXML private Label lblPacienteNombre;
    @FXML private Label lblPacienteDocumento;
    @FXML private Label lblPacienteFecNac;
    @FXML private Label lblPacienteTelf;
    @FXML private Label lblPacienteTienda;

    @FXML private TabPane tabPanePaciente;
    @FXML private Tab tabNuevaConsulta;
    
    // --- Tabla de Consultas ---
    @FXML private TableView<Consulta> tableConsultas;
    @FXML private TableColumn<Consulta, String> colConsFecha;
    @FXML private TableColumn<Consulta, String> colConsMotivo;
    @FXML private TableColumn<Consulta, String> colConsGraduacion;

    // --- Formulario Nueva Consulta ---
    @FXML private TextField txtConsMotivo;
    @FXML private TextField txtGraduacionOD;
    @FXML private TextField txtGraduacionOI;
    @FXML private TextArea txtConsObservaciones;
    @FXML private TextField txtConsCosto;
    @FXML private ComboBox<TipoComprobante> comboTipoComprobante;

    // --- Formulario Nuevo Paciente ---
    @FXML private VBox paneNuevoPaciente;
    @FXML private ComboBox<TipoDocumento> comboTipoDoc;
    @FXML private TextField txtPacDoc;
    @FXML private TextField txtPacNombre;
    @FXML private TextField txtPacApeP;
    @FXML private TextField txtPacApeM;
    @FXML private TextField txtPacTelf;
    @FXML private DatePicker dpPacFecNac;

    // --- Placeholder ---
    @FXML private VBox panePlaceholder;

    // --- Servicios y Contexto ---
    private final PacienteService pacienteService = new PacienteServiceImpl();
    private final ConsultaService consultaService = new ConsultaServiceImpl();
    private final ProductoService productoService = new ProductoServiceImpl();
    
    private Stage stage;
    private Usuario usuario;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarPacientes();
    }

    @FXML
    public void initialize() {
        // Enlazar columnas de la tabla de Pacientes
        colDocumento.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumDocumento()));
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().nombreCompleto()));
        colTelefono.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTelefono() != null ? cell.getValue().getTelefono() : "-"));

        // Enlazar columnas de la tabla de Consultas
        colConsFecha.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFecha() != null ? cell.getValue().getFecha().toString().substring(0, 10) : "-"
        ));
        colConsMotivo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMotivo()));
        colConsGraduacion.setCellValueFactory(cell -> {
            var consulta = cell.getValue();
            var hc = consultaService.findHistorialByConsultaId(consulta.getId());
            if (hc != null) {
                return new SimpleStringProperty("OD: " + hc.getGraduacionOd() + " | OI: " + hc.getGraduacionOi());
            }
            return new SimpleStringProperty("-");
        });

        // Escuchar cambios de selección en la tabla de pacientes
        tablePacientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallePaciente(newSelection);
            }
        });

        // Cargar Comboboxes
        comboTipoComprobante.setItems(FXCollections.observableArrayList(TipoComprobante.values()));
        comboTipoComprobante.getSelectionModel().select(TipoComprobante.BOLETA);

        comboTipoDoc.setItems(FXCollections.observableArrayList(TipoDocumento.values()));
        comboTipoDoc.getSelectionModel().select(TipoDocumento.DNI);
    }

    private void cargarPacientes() {
        if (usuario != null) {
            var list = pacienteService.findByTiendaId(usuario.getTiendaId());
            tablePacientes.setItems(FXCollections.observableArrayList(list));
        }
    }

    private void mostrarDetallePaciente(Paciente p) {
        panePlaceholder.setVisible(false);
        paneNuevoPaciente.setVisible(false);
        paneDetalle.setVisible(true);

        lblPacienteNombre.setText(p.nombreCompleto());
        lblPacienteDocumento.setText(p.getTipoDocumento() + ": " + p.getNumDocumento());
        lblPacienteFecNac.setText(p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : "-");
        lblPacienteTelf.setText(p.getTelefono() != null ? p.getTelefono() : "-");
        lblPacienteTienda.setText("Tienda #" + p.getTiendaId());

        // Cargar historial de consultas
        var consultas = consultaService.findByPacienteId(p.getId());
        tableConsultas.setItems(FXCollections.observableArrayList(consultas));

        // Limpiar formulario de consulta
        txtConsMotivo.clear();
        txtGraduacionOD.clear();
        txtGraduacionOI.clear();
        txtConsObservaciones.clear();
        txtConsCosto.setText("40.00");
        comboTipoComprobante.getSelectionModel().select(TipoComprobante.BOLETA);

        tabPanePaciente.getSelectionModel().select(0); // Mostrar pestaña de historial
    }

    @FXML
    private void onBuscar() {
        var query = txtBuscar.getText();
        if (query == null || query.isBlank()) {
            cargarPacientes();
            return;
        }
        var matches = pacienteService.searchByNombre(query);
        // Filtrar por la tienda del usuario
        if (usuario != null) {
            matches = matches.stream()
                    .filter(p -> p.getTiendaId().equals(usuario.getTiendaId()))
                    .toList();
        }
        tablePacientes.setItems(FXCollections.observableArrayList(matches));
    }

    @FXML
    private void onMostrarRegistroPaciente() {
        tablePacientes.getSelectionModel().clearSelection();
        panePlaceholder.setVisible(false);
        paneDetalle.setVisible(false);
        paneNuevoPaciente.setVisible(true);

        // Limpiar inputs
        txtPacDoc.clear();
        txtPacNombre.clear();
        txtPacApeP.clear();
        txtPacApeM.clear();
        txtPacTelf.clear();
        dpPacFecNac.setValue(null);
        comboTipoDoc.getSelectionModel().select(TipoDocumento.DNI);
    }

    @FXML
    private void onIniciarConsultaForm() {
        tabPanePaciente.getSelectionModel().select(tabNuevaConsulta);
    }

    @FXML
    private void onCancelarRegistro() {
        paneNuevoPaciente.setVisible(false);
        var selected = tablePacientes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            paneDetalle.setVisible(true);
        } else {
            panePlaceholder.setVisible(true);
        }
    }

    @FXML
    private void onGuardarPaciente() {
        var tipo = comboTipoDoc.getValue();
        var doc = txtPacDoc.getText();
        var nombre = txtPacNombre.getText();
        var apeP = txtPacApeP.getText();
        var apeM = txtPacApeM.getText();
        var telf = txtPacTelf.getText();
        var fecNac = dpPacFecNac.getValue();

        if (doc == null || doc.isBlank() || nombre == null || nombre.isBlank() ||
                apeP == null || apeP.isBlank() || apeM == null || apeM.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, complete todos los campos obligatorios del paciente.");
            return;
        }

        var p = new Paciente();
        p.setTipoDocumento(tipo);
        p.setNumDocumento(doc);
        p.setNombre(nombre);
        p.setApellidoP(apeP);
        p.setApellidoM(apeM);
        p.setTelefono(telf);
        p.setFechaNacimiento(fecNac);
        p.setTiendaId(usuario.getTiendaId());
        p.setEsDestacado(false);

        try {
            var guardado = pacienteService.save(p);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente Registrado", "El paciente se registró correctamente.");
            cargarPacientes();
            tablePacientes.getSelectionModel().select(guardado);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", "No se pudo guardar el paciente: " + e.getMessage());
        }
    }

    @FXML
    private void onGuardarConsulta() {
        var paciente = tablePacientes.getSelectionModel().getSelectedItem();
        if (paciente == null) return;

        var motivo = txtConsMotivo.getText();
        var costText = txtConsCosto.getText();
        var comp = comboTipoComprobante.getValue();

        if (motivo == null || motivo.isBlank() || costText == null || costText.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, indique el motivo de la consulta y el costo.");
            return;
        }

        BigDecimal costo;
        try {
            costo = new BigDecimal(costText);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Costo Inválido", "El costo del servicio debe ser un número decimal válido.");
            return;
        }

        // Buscar el producto de tipo examen/consulta de la sucursal activa
        var productos = productoService.findDisponibles(usuario.getTiendaId());
        var prodOpt = productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains("consulta"))
                .findFirst();

        if (prodOpt.isEmpty()) {
            // Intentar buscar cualquier producto disponible en la tienda para no colapsar la base de datos
            prodOpt = productos.stream().findFirst();
        }

        if (prodOpt.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Catálogo", "No se encontró ningún producto de tipo Consulta en esta tienda.");
            return;
        }

        var productoConsulta = prodOpt.get();

        try {
            // Registrar consulta y generar venta de manera atómica
            var consulta = consultaService.registrarConsultaConVenta(
                    paciente.getId(),
                    usuario.getId(),
                    usuario.getTiendaId(),
                    motivo,
                    productoConsulta.getId(),
                    costo,
                    comp.name()
            );

            // Registrar Historial Clínico de refracción
            var hc = new HistorialClinico();
            hc.setConsultaId(consulta.getId());
            hc.setGraduacionOd(txtGraduacionOD.getText());
            hc.setGraduacionOi(txtGraduacionOI.getText());
            hc.setObservaciones(txtConsObservaciones.getText());
            
            consultaService.saveHistorialClinico(hc);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta Registrada", "La consulta y su respectiva venta fueron registradas con éxito.");
            
            // Actualizar vista
            mostrarDetallePaciente(paciente);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Registrar", "No se pudo registrar la consulta: " + e.getMessage());
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
