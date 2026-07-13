package opty.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import opty.model.entity.MovimientoCaja;
import opty.model.entity.Usuario;
import opty.model.enums.MetodoPago;
import opty.model.enums.TipoMovimientoCaja;
import opty.service.MovimientoCajaService;
import opty.service.impl.MovimientoCajaServiceImpl;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CajaController implements ModuleController {

    // --- Tarjetas de Resumen ---
    @FXML private Label lblTotalIngresos;
    @FXML private Label lblTotalEgresos;
    @FXML private Label lblSaldoCaja;

    // --- Filtros ---
    @FXML private ComboBox<String> comboFiltroTipo;
    @FXML private ComboBox<String> comboFiltroMetodo;

    // --- Tabla ---
    @FXML private TableView<MovimientoCaja> tableMovimientos;
    @FXML private TableColumn<MovimientoCaja, String> colCajaId;
    @FXML private TableColumn<MovimientoCaja, String> colCajaFecha;
    @FXML private TableColumn<MovimientoCaja, String> colCajaTipo;
    @FXML private TableColumn<MovimientoCaja, String> colCajaMetodo;
    @FXML private TableColumn<MovimientoCaja, String> colCajaMonto;
    @FXML private TableColumn<MovimientoCaja, String> colCajaDescripcion;

    // --- Registro Manual ---
    @FXML private ComboBox<TipoMovimientoCaja> comboManualTipo;
    @FXML private ComboBox<MetodoPago> comboManualMetodo;
    @FXML private TextField txtManualMonto;
    @FXML private TextArea txtManualDescripcion;

    private final MovimientoCajaService movimientoCajaService = new MovimientoCajaServiceImpl();
    private Stage stage;
    private Usuario usuario;
    private List<MovimientoCaja> allMovimientos = new ArrayList<>();

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        onCargarMovimientos();
    }

    @FXML
    public void initialize() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Vincular columnas de la tabla
        colCajaId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId().toString()));
        colCajaFecha.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFecha() != null ? cell.getValue().getFecha().format(dtf) : "-"
        ));
        colCajaTipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipo().name()));
        colCajaMetodo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMetodoPago().name()));
        colCajaMonto.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getMonto().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()));
        colCajaDescripcion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescripcion()));

        // Inicializar ComboBoxes de filtros
        var tiposFiltro = FXCollections.observableArrayList("TODOS");
        for (var t : TipoMovimientoCaja.values()) {
            tiposFiltro.add(t.name());
        }
        comboFiltroTipo.setItems(tiposFiltro);
        comboFiltroTipo.getSelectionModel().select("TODOS");

        var metodosFiltro = FXCollections.observableArrayList("TODOS");
        for (var m : MetodoPago.values()) {
            metodosFiltro.add(m.name());
        }
        comboFiltroMetodo.setItems(metodosFiltro);
        comboFiltroMetodo.getSelectionModel().select("TODOS");

        // Inicializar ComboBoxes de registro manual
        comboManualTipo.setItems(FXCollections.observableArrayList(TipoMovimientoCaja.values()));
        comboManualMetodo.setItems(FXCollections.observableArrayList(MetodoPago.values()));
    }

    @FXML
    public void onCargarMovimientos() {
        if (usuario == null) return;
        
        // Cargar movimientos de la tienda activa
        allMovimientos = movimientoCajaService.findByTiendaId(usuario.getTiendaId());
        
        // Recalcular métricas
        recalcularMetricas();
        
        // Aplicar filtros actuales
        onFiltrarMovimientos();
    }

    private void recalcularMetricas() {
        BigDecimal ingresos = BigDecimal.ZERO;
        BigDecimal egresos = BigDecimal.ZERO;

        for (var mov : allMovimientos) {
            if (mov.getTipo() == TipoMovimientoCaja.ENTRADA) {
                ingresos = ingresos.add(mov.getMonto());
            } else {
                egresos = egresos.add(mov.getMonto());
            }
        }

        BigDecimal balance = ingresos.subtract(egresos);

        lblTotalIngresos.setText("S/ " + ingresos.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        lblTotalEgresos.setText("S/ " + egresos.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
        lblSaldoCaja.setText("S/ " + balance.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString());
    }

    @FXML
    public void onFiltrarMovimientos() {
        var tipoFiltro = comboFiltroTipo.getValue();
        var metodoFiltro = comboFiltroMetodo.getValue();

        List<MovimientoCaja> filtrados = new ArrayList<>(allMovimientos);

        if (tipoFiltro != null && !tipoFiltro.equals("TODOS")) {
            filtrados.removeIf(m -> !m.getTipo().name().equals(tipoFiltro));
        }

        if (metodoFiltro != null && !metodoFiltro.equals("TODOS")) {
            filtrados.removeIf(m -> !m.getMetodoPago().name().equals(metodoFiltro));
        }

        tableMovimientos.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    public void onLimpiarFiltros() {
        comboFiltroTipo.getSelectionModel().select("TODOS");
        comboFiltroMetodo.getSelectionModel().select("TODOS");
        onFiltrarMovimientos();
    }

    @FXML
    public void onGuardarMovimientoManual() {
        var tipo = comboManualTipo.getValue();
        var metodo = comboManualMetodo.getValue();
        var montoStr = txtManualMonto.getText().trim();
        var desc = txtManualDescripcion.getText().trim();

        if (tipo == null || metodo == null || montoStr.isEmpty() || desc.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor complete todos los campos del formulario manual.");
            return;
        }

        BigDecimal monto;
        try {
            monto = new BigDecimal(montoStr);
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Monto Inválido", "El monto del movimiento debe ser mayor a cero.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Monto Inválido", "Por favor ingrese un monto numérico válido.");
            return;
        }

        try {
            var mc = new MovimientoCaja();
            mc.setTiendaId(usuario.getTiendaId());
            mc.setUsuarioId(usuario.getId());
            mc.setTipo(tipo);
            mc.setMetodoPago(metodo);
            mc.setMonto(monto);
            mc.setDescripcion(desc);
            mc.setFecha(java.time.LocalDateTime.now());

            movimientoCajaService.save(mc);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Movimiento Registrado", "El movimiento manual de caja se registró con éxito.");
            
            // Limpiar formulario manual
            comboManualTipo.getSelectionModel().clearSelection();
            comboManualMetodo.getSelectionModel().clearSelection();
            txtManualMonto.clear();
            txtManualDescripcion.clear();

            // Recargar movimientos
            onCargarMovimientos();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Registrar", "No se pudo registrar el movimiento manual: " + e.getMessage());
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
