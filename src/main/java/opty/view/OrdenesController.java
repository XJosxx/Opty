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
    @FXML private TableColumn<OrdenTrabajo, String> colOrdTicket;
    @FXML private TableColumn<OrdenTrabajo, String> colOrdPaciente;
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
    @FXML private Label lblOtTicketVenta;
    @FXML private Label lblOtPaciente;

    // --- Graduación ---
    @FXML private Label lblOdEsfera;
    @FXML private Label lblOdCilindro;
    @FXML private Label lblOdEje;
    @FXML private Label lblOdAdicion;
    @FXML private Label lblOdAv;
    @FXML private Label lblOdDp;

    @FXML private Label lblOiEsfera;
    @FXML private Label lblOiCilindro;
    @FXML private Label lblOiEje;
    @FXML private Label lblOiAdicion;
    @FXML private Label lblOiAv;
    @FXML private Label lblOiDp;

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
        colOrdTicket.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getNumeroTicket() != null ? cell.getValue().getNumeroTicket() : "-"
        ));
        colOrdPaciente.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getNombrePaciente() != null ? cell.getValue().getNombrePaciente() : "-"
        ));
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
        lblOtTicketVenta.setText(ot.getNumeroTicket() != null ? ot.getNumeroTicket() : "-");
        lblOtPaciente.setText(ot.getNombrePaciente() != null ? ot.getNombrePaciente() : "-");

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
                setEyeLabels(hc.getGraduacionOd(), lblOdEsfera, lblOdCilindro, lblOdEje, lblOdAdicion, lblOdAv, lblOdDp);
                setEyeLabels(hc.getGraduacionOi(), lblOiEsfera, lblOiCilindro, lblOiEje, lblOiAdicion, lblOiAv, lblOiDp);
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

    private void setEyeLabels(String jsonStr, Label esf, Label cil, Label eje, Label add, Label av, Label dp) {
        if (jsonStr == null || jsonStr.isBlank()) {
            esf.setText("-"); cil.setText("-"); eje.setText("-"); add.setText("-"); av.setText("-"); dp.setText("-");
            return;
        }
        if (!jsonStr.trim().startsWith("{")) {
            esf.setText(jsonStr);
            cil.setText("-"); eje.setText("-"); add.setText("-"); av.setText("-"); dp.setText("-");
            return;
        }
        try {
            String valEsf = extractJsonValue(jsonStr, "esfera");
            String valCil = extractJsonValue(jsonStr, "cilindro");
            String valEje = extractJsonValue(jsonStr, "eje");
            String valAdd = extractJsonValue(jsonStr, "adicion");
            String valAv = extractJsonValue(jsonStr, "av");
            String valDp = extractJsonValue(jsonStr, "dp");

            esf.setText(valEsf.isBlank() ? "-" : valEsf);
            cil.setText(valCil.isBlank() ? "-" : valCil);
            eje.setText(valEje.isBlank() ? "-" : valEje);
            add.setText(valAdd.isBlank() ? "-" : valAdd);
            av.setText(valAv.isBlank() ? "-" : valAv);
            dp.setText(valDp.isBlank() ? "-" : valDp);
        } catch (Exception e) {
            esf.setText(jsonStr);
            cil.setText("-"); eje.setText("-"); add.setText("-"); av.setText("-"); dp.setText("-");
        }
    }

    private String extractJsonValue(String json, String key) {
        String pattern = "(?:\"|')?" + key + "(?:\"|')?\\s*:\\s*(?:\"([^\"]*)\"|'([^']*)'|([^,}]*))";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(json);
        if (m.find()) {
            if (m.group(1) != null) return m.group(1).trim();
            if (m.group(2) != null) return m.group(2).trim();
            if (m.group(3) != null) return m.group(3).trim();
        }
        return "";
    }

    private void setClinicalFallback() {
        lblOdEsfera.setText("-"); lblOdCilindro.setText("-"); lblOdEje.setText("-"); lblOdAdicion.setText("-"); lblOdAv.setText("-"); lblOdDp.setText("-");
        lblOiEsfera.setText("-"); lblOiCilindro.setText("-"); lblOiEje.setText("-"); lblOiAdicion.setText("-"); lblOiAv.setText("-"); lblOiDp.setText("-");
        lblOtObservaciones.setText("No hay prescripción clínica vinculada");
    }

    private void onActualizarEstadoOT(Integer id, EstadoFisicoOT nuevo) {
        try {
            ordenTrabajoService.actualizarEstado(id, nuevo);
            
            // Si el nuevo estado es ENTREGADO, verificar si hay un saldo financiero pendiente por cobrar
            if (nuevo == EstadoFisicoOT.ENTREGADO) {
                var otOpt = ordenTrabajoService.findById(id);
                if (otOpt.isPresent()) {
                    var ot = otOpt.get();
                    if (ot.getVentaId() != null) {
                        var ventaService = new opty.service.impl.VentaServiceImpl();
                        var ventaOpt = ventaService.findById(ot.getVentaId());
                        if (ventaOpt.isPresent()) {
                            var venta = ventaOpt.get();
                            if (venta.getEstadoFinanciero() != opty.model.enums.EstadoFinanciero.PAGADO) {
                                java.math.BigDecimal total = venta.getMontoTotal();
                                java.math.BigDecimal pagado = getMontoPagadoVenta(venta.getId());
                                java.math.BigDecimal saldo = total.subtract(pagado);
                                
                                if (saldo.compareTo(java.math.BigDecimal.ZERO) > 0) {
                                    var confirm = new Alert(Alert.AlertType.CONFIRMATION);
                                    confirm.setTitle("Saldo Pendiente de Cobro");
                                    confirm.setHeaderText("La venta asociada #" + venta.getNumeroTicket() + " tiene un saldo pendiente de S/ " + saldo.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
                                    confirm.setContentText("¿Desea registrar el cobro del saldo restante de S/ " + saldo.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() + " en este momento para marcar la venta como totalmente cancelada (PAGADA)?");
                                    confirm.initOwner(stage);
                                    
                                    var btnSi = new ButtonType("Sí, Cobrar");
                                    var btnNo = new ButtonType("No, Mantener Pendiente");
                                    confirm.getButtonTypes().setAll(btnSi, btnNo);
                                    
                                    var res = confirm.showAndWait();
                                    if (res.isPresent() && res.get() == btnSi) {
                                        registrarPagoSaldoVenta(venta.getId(), venta.getTiendaId(), venta.getUsuarioId(), saldo);
                                        
                                        var okAlert = new Alert(Alert.AlertType.INFORMATION);
                                        okAlert.setTitle("Cobro Registrado");
                                        okAlert.setHeaderText(null);
                                        okAlert.setContentText("Se registró el cobro por S/ " + saldo.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() + " en caja. La venta se marcó como PAGADA.");
                                        okAlert.initOwner(stage);
                                        okAlert.showAndWait();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
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

    private java.math.BigDecimal getMontoPagadoVenta(Integer ventaId) {
        java.math.BigDecimal sum = java.math.BigDecimal.ZERO;
        String sql = "SELECT SUM(monto) FROM movimientos_caja WHERE venta_id = ? AND tipo = 'ENTRADA'";
        try (var conn = opty.config.DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ventaId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.math.BigDecimal res = rs.getBigDecimal(1);
                    if (res != null) {
                        sum = res;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sum;
    }

    private void registrarPagoSaldoVenta(Integer ventaId, Integer tiendaId, Integer usuarioId, java.math.BigDecimal saldo) {
        String insertCaja = "INSERT INTO movimientos_caja (tienda_id, usuario_id, venta_id, tipo, metodo_pago, monto, descripcion) VALUES (?, ?, ?, 'ENTRADA', 'EFECTIVO', ?, 'Cobro de saldo restante al entregar orden de trabajo')";
        String updateCab = "UPDATE ventas_cabecera SET estado_financiero = 'PAGADO' WHERE id = ?";
        
        java.sql.Connection conn = null;
        try {
            conn = opty.config.DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            try (var psCaja = conn.prepareStatement(insertCaja)) {
                psCaja.setInt(1, tiendaId);
                psCaja.setInt(2, usuarioId);
                psCaja.setInt(3, ventaId);
                psCaja.setBigDecimal(4, saldo);
                psCaja.executeUpdate();
            }
            
            try (var psUpdate = conn.prepareStatement(updateCab)) {
                psUpdate.setInt(1, ventaId);
                psUpdate.executeUpdate();
            }
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error al registrar cobro de saldo", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
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
