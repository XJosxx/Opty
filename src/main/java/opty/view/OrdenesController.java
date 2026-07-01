package opty.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import opty.model.entity.HistorialClinico;
import opty.model.entity.OrdenTrabajo;
import opty.model.entity.Usuario;
import opty.model.enums.EstadoFisicoOT;
import opty.service.ConsultaService;
import opty.service.OrdenTrabajoService;
import opty.service.impl.ConsultaServiceImpl;
import opty.service.impl.OrdenTrabajoServiceImpl;

import java.time.format.DateTimeFormatter;

public class OrdenesController implements ModuleController {

    // --- Columna Izquierda ---
    @FXML private ComboBox<String> comboFiltroEstado;
    @FXML private TableView<OrdenTrabajo> tableOrdenes;
    @FXML private TableColumn<OrdenTrabajo, String> colOrdId;
    @FXML private TableColumn<OrdenTrabajo, String> colOrdTipo;
    @FXML private TableColumn<OrdenTrabajo, String> colOrdPrometida;
    @FXML private TableColumn<OrdenTrabajo, String> colOrdEstado;

    // --- Columna Derecha: Vista Detalle ---
    @FXML private VBox paneDetalle;
    @FXML private Label lblOtId;
    @FXML private Label lblOtTipo;
    @FXML private Label lblOtEstadoBadge;
    @FXML private Label lblOtFecCreacion;
    @FXML private Label lblOtFecPrometida;

    // --- Graduación ---
    @FXML private Label lblOtGradOD;
    @FXML private Label lblOtGradOI;
    @FXML private Label lblOtObservaciones;

    // --- Especificaciones Materiales ---
    @FXML private Label lblOtUsaMontura;
    @FXML private Label lblOtUsaLuna;
    @FXML private Label lblOtDetallesMontura;
    @FXML private Label lblOtDetallesLuna;

    // --- Contenedor de Acciones ---
    @FXML private HBox boxAcciones;
    @FXML private VBox panePlaceholder;

    // --- Servicios ---
    private final OrdenTrabajoService ordenTrabajoService = new OrdenTrabajoServiceImpl();
    private final ConsultaService consultaService = new ConsultaServiceImpl();

    private Stage stage;
    private Usuario usuario;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        onCargarOrdenes();
    }

    @FXML
    public void initialize() {
        var dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Enlazar columnas
        colOrdId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId().toString()));
        colOrdTipo.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getTipoTrabajo() != null ? cell.getValue().getTipoTrabajo().name().replace("_", " ") : "-"
        ));
        colOrdPrometida.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFechaPrometida() != null ? cell.getValue().getFechaPrometida().format(dtf) : "-"
        ));
        colOrdEstado.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getEstadoFisico() != null ? cell.getValue().getEstadoFisico().name() : "-"
        ));

        // Listener de selección
        tableOrdenes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mostrarDetalleOrden(newVal);
            }
        });

        // Poblar filtro
        var filtros = FXCollections.observableArrayList("TODOS");
        for (var estado : EstadoFisicoOT.values()) {
            filtros.add(estado.name());
        }
        comboFiltroEstado.setItems(filtros);
        comboFiltroEstado.getSelectionModel().select("TODOS");
    }

    @FXML
    private void onCargarOrdenes() {
        var list = ordenTrabajoService.findAll();
        tableOrdenes.setItems(FXCollections.observableArrayList(list));
        paneDetalle.setVisible(false);
        panePlaceholder.setVisible(true);
    }

    private void mostrarDetalleOrden(OrdenTrabajo ot) {
        panePlaceholder.setVisible(false);
        paneDetalle.setVisible(true);

        lblOtId.setText("DETALLE DE ORDEN #" + ot.getId());
        lblOtTipo.setText(ot.getTipoTrabajo() != null ? ot.getTipoTrabajo().name().replace("_", " ") : "-");
        lblOtFecCreacion.setText(ot.getFechaCreacion() != null ? ot.getFechaCreacion().toString().substring(0, 16).replace("T", " ") : "-");
        lblOtFecPrometida.setText(ot.getFechaPrometida() != null ? ot.getFechaPrometida().toString().substring(0, 10) : "-");

        // Estado Badge
        var est = ot.getEstadoFisico();
        lblOtEstadoBadge.setText(est.name());
        lblOtEstadoBadge.getStyleClass().clear();
        lblOtEstadoBadge.getStyleClass().addAll("badge");
        switch (est) {
            case PENDIENTE -> lblOtEstadoBadge.getStyleClass().add("badge-danger");
            case LABORATORIO -> lblOtEstadoBadge.getStyleClass().add("badge-info");
            case LISTO -> lblOtEstadoBadge.getStyleClass().add("badge-warning");
            case ENTREGADO -> lblOtEstadoBadge.getStyleClass().add("badge-success");
        }

        // Cargar Historial Clínico de Refracción
        if (ot.getHistorialClinicoId() != null && ot.getHistorialClinicoId() > 0) {
            var hc = consultaService.findHistorialById(ot.getHistorialClinicoId());
            if (hc != null) {
                lblOtGradOD.setText(hc.getGraduacionOd() != null && !hc.getGraduacionOd().isBlank() ? hc.getGraduacionOd() : "Sin refracción registrada");
                lblOtGradOI.setText(hc.getGraduacionOi() != null && !hc.getGraduacionOi().isBlank() ? hc.getGraduacionOi() : "Sin refracción registrada");
                lblOtObservaciones.setText(hc.getObservaciones() != null && !hc.getObservaciones().isBlank() ? hc.getObservaciones() : "Sin observaciones adicionales");
            } else {
                setClinicalFallback();
            }
        } else {
            setClinicalFallback();
        }

        // Cargar detalles de materiales
        lblOtUsaMontura.setText(ot.getUsaMonturaCliente() != null && ot.getUsaMonturaCliente() ? "SÍ" : "NO");
        lblOtUsaLuna.setText(ot.getUsaLunaCliente() != null && ot.getUsaLunaCliente() ? "SÍ" : "NO");
        lblOtDetallesMontura.setText(ot.getDetallesMonturaCliente() != null && !ot.getDetallesMonturaCliente().isBlank() ? ot.getDetallesMonturaCliente() : "-");
        lblOtDetallesLuna.setText(ot.getDetallesLunaCliente() != null && !ot.getDetallesLunaCliente().isBlank() ? ot.getDetallesLunaCliente() : "-");

        // Controlar botones de acción de acuerdo al flujo de trabajo
        boxAcciones.getChildren().clear();
        if (est == EstadoFisicoOT.PENDIENTE) {
            var btnEnv = new Button("Enviar a Laboratorio");
            btnEnv.getStyleClass().addAll("btn-secondary");
            btnEnv.setOnAction(e -> onActualizarEstadoOT(ot.getId(), EstadoFisicoOT.LABORATORIO));
            boxAcciones.getChildren().add(btnEnv);
        } else if (est == EstadoFisicoOT.LABORATORIO) {
            var btnListo = new Button("Marcar como Listo");
            btnListo.getStyleClass().addAll("btn-primary");
            btnListo.setOnAction(e -> onActualizarEstadoOT(ot.getId(), EstadoFisicoOT.LISTO));
            boxAcciones.getChildren().add(btnListo);
        } else if (est == EstadoFisicoOT.LISTO) {
            var btnEnt = new Button("Entregar al Cliente");
            btnEnt.getStyleClass().addAll("btn-primary");
            btnEnt.setOnAction(e -> onActualizarEstadoOT(ot.getId(), EstadoFisicoOT.ENTREGADO));
            boxAcciones.getChildren().add(btnEnt);
        } else {
            var lblOk = new Label("ORDEN ENTREGADA AL CLIENTE");
            lblOk.getStyleClass().addAll("badge", "badge-success");
            boxAcciones.getChildren().add(lblOk);
        }
    }

    private void setClinicalFallback() {
        lblOtGradOD.setText("-");
        lblOtGradOI.setText("-");
        lblOtObservaciones.setText("No hay prescripción clínica vinculada");
    }

    private void onActualizarEstadoOT(Integer id, EstadoFisicoOT nuevo) {
        try {
            ordenTrabajoService.actualizarEstado(id, nuevo);
            
            // Recargar la tabla y seleccionar el mismo elemento para refrescar detalles
            var list = ordenTrabajoService.findAll();
            tableOrdenes.setItems(FXCollections.observableArrayList(list));
            
            for (var ot : list) {
                if (ot.getId().equals(id)) {
                    tableOrdenes.getSelectionModel().select(ot);
                    mostrarDetalleOrden(ot);
                    break;
                }
            }
        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Actualización");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cambiar el estado de la orden: " + e.getMessage());
            alert.initOwner(stage);
            alert.showAndWait();
        }
    }

    @FXML
    private void onFiltrarOrdenes() {
        var fit = comboFiltroEstado.getValue();
        if (fit == null || fit.equals("TODOS")) {
            onCargarOrdenes();
            return;
        }

        var filtered = ordenTrabajoService.findByEstado(EstadoFisicoOT.valueOf(fit));
        tableOrdenes.setItems(FXCollections.observableArrayList(filtered));
        paneDetalle.setVisible(false);
        panePlaceholder.setVisible(true);
    }
}
