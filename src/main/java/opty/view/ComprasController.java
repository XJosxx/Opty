package opty.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import opty.model.entity.CompraCabecera;
import opty.model.entity.CompraDetalle;
import opty.model.entity.Insumo;
import opty.model.entity.Proveedor;
import opty.model.entity.Usuario;
import opty.model.enums.MetodoPago;
import opty.model.enums.EstadoFinanciero;
import opty.service.CompraService;
import opty.service.InsumoService;
import opty.service.ProveedorService;
import opty.service.impl.CompraServiceImpl;
import opty.service.impl.InsumoServiceImpl;
import opty.service.impl.ProveedorServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComprasController implements ModuleController {

    // --- Inputs Proveedor ---
    @FXML private ComboBox<Proveedor> comboProveedores;

    // --- Inputs Insumo ---
    @FXML private ComboBox<Insumo> comboInsumos;
    @FXML private Label lblStockIns;
    @FXML private TextField txtPrecioComp;
    @FXML private Spinner<Integer> spinCantidad;

    // --- Carrito de Compras ---
    @FXML private TableView<CompraDetalle> tableCarrito;
    @FXML private TableColumn<CompraDetalle, String> colCartInsumo;
    @FXML private TableColumn<CompraDetalle, String> colCartPrecio;
    @FXML private TableColumn<CompraDetalle, String> colCartCantidad;
    @FXML private TableColumn<CompraDetalle, String> colCartSubtotal;
    @FXML private TableColumn<CompraDetalle, Void> colCartAcciones;

    // --- Totales y Pagos ---
    @FXML private ComboBox<MetodoPago> comboMetodoPago;
    @FXML private Label lblResSubtotal;
    @FXML private Label lblResIgv;
    @FXML private Label lblResTotal;
    @FXML private TextField txtMontoCuenta;
    @FXML private Label lblSaldoPendiente;

    // --- Tab 2: Historial y Cuentas por Pagar ---
    @FXML private TableView<CompraCabecera> tableHistorial;
    @FXML private TableColumn<CompraCabecera, String> colHistId;
    @FXML private TableColumn<CompraCabecera, String> colHistOrden;
    @FXML private TableColumn<CompraCabecera, String> colHistProveedor;
    @FXML private TableColumn<CompraCabecera, String> colHistFecha;
    @FXML private TableColumn<CompraCabecera, String> colHistEstadoFin;
    @FXML private TableColumn<CompraCabecera, String> colHistTotal;

    @FXML private VBox paneDetalleHist;
    @FXML private Label lblHistDetTitle;
    @FXML private Label lblHistDetProveedor;
    @FXML private Label lblHistDetEstadoBadge;
    @FXML private TableView<CompraDetalle> tableDetalleHist;
    @FXML private TableColumn<CompraDetalle, String> colDetInsumo;
    @FXML private TableColumn<CompraDetalle, String> colDetPrecio;
    @FXML private TableColumn<CompraDetalle, String> colDetCantidad;
    @FXML private TableColumn<CompraDetalle, String> colDetSubtotal;

    @FXML private Label lblHistTotalCompra;
    @FXML private Label lblHistAbonado;
    @FXML private Label lblHistSaldo;
    @FXML private HBox boxAccionesCompra;

    // --- Servicios ---
    private final ProveedorService proveedorService = new ProveedorServiceImpl();
    private final InsumoService insumoService = new InsumoServiceImpl();
    private final CompraService compraService = new CompraServiceImpl();

    private Stage stage;
    private Usuario usuario;

    private final ObservableList<CompraDetalle> listaCarrito = FXCollections.observableArrayList();
    private final Map<Integer, Insumo> cacheInsumos = new HashMap<>();
    private final Map<Integer, Proveedor> cacheProveedores = new HashMap<>();

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        onCargarDatos();
    }

    @FXML
    public void initialize() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Configurar Spinner
        spinCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));

        // Configurar ComboBox Pago
        comboMetodoPago.setItems(FXCollections.observableArrayList(MetodoPago.values()));
        comboMetodoPago.getSelectionModel().select(MetodoPago.EFECTIVO);

        // Bind columnas del carrito
        colCartInsumo.setCellValueFactory(cell -> {
            var insId = cell.getValue().getInsumoId();
            var ins = cacheInsumos.get(insId);
            return new SimpleStringProperty(ins != null ? ins.getNombre() : "Insumo #" + insId);
        });

        colCartPrecio.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getPrecioUnitario().setScale(2, RoundingMode.HALF_UP).toPlainString()));
        colCartCantidad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCantidad().toString()));
        colCartSubtotal.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getSubtotal().setScale(2, RoundingMode.HALF_UP).toPlainString()));

        // Acciones: Eliminar del carrito
        colCartAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("X");
            {
                btnEliminar.getStyleClass().addAll("btn-danger");
                btnEliminar.setOnAction(event -> {
                    var item = getTableView().getItems().get(getIndex());
                    listaCarrito.remove(item);
                    recalcularTotales();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });

        tableCarrito.setItems(listaCarrito);

        // Listener de insumo
        comboInsumos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblStockIns.setText(String.valueOf(newVal.getStockActual()));
            } else {
                lblStockIns.setText("-");
            }
        });

        // Listener para abono/pago a cuenta
        txtMontoCuenta.textProperty().addListener((obs, oldVal, newVal) -> {
            BigDecimal total = BigDecimal.ZERO;
            for (var item : listaCarrito) {
                total = total.add(item.getSubtotal());
            }
            recalcularSaldoPendiente(total);
        });

        // --- Bind Columnas Historial (Tab 2) ---
        colHistId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId().toString()));
        colHistOrden.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroOrden()));
        colHistProveedor.setCellValueFactory(cell -> {
            var prov = cacheProveedores.get(cell.getValue().getProveedorId());
            return new SimpleStringProperty(prov != null ? prov.getNombreEmpresa() : "Proveedor #" + cell.getValue().getProveedorId());
        });
        colHistFecha.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFechaEmision() != null ? cell.getValue().getFechaEmision().format(dtf) : "-"
        ));
        colHistEstadoFin.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstadoFinanciero().name()));
        colHistTotal.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getMontoTotal().setScale(2, RoundingMode.HALF_UP).toPlainString()));

        // Bind Columnas Detalle de Historial
        colDetInsumo.setCellValueFactory(cell -> {
            var insId = cell.getValue().getInsumoId();
            var ins = cacheInsumos.get(insId);
            return new SimpleStringProperty(ins != null ? ins.getNombre() : "Insumo #" + insId);
        });
        colDetPrecio.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getPrecioUnitario().setScale(2, RoundingMode.HALF_UP).toPlainString()));
        colDetCantidad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCantidad().toString()));
        colDetSubtotal.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getSubtotal().setScale(2, RoundingMode.HALF_UP).toPlainString()));

        // Listener de selección en tabla historial
        tableHistorial.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mostrarDetalleHistorial(newVal);
            }
        });
    }

    private void onCargarDatos() {
        if (usuario == null) return;

        // Cargar proveedores
        var proveedores = proveedorService.findAll();
        comboProveedores.setItems(FXCollections.observableArrayList(proveedores));
        cacheProveedores.clear();
        for (var p : proveedores) {
            cacheProveedores.put(p.getId(), p);
        }

        // Cargar insumos correspondientes a esta tienda
        var insumos = insumoService.findAll().stream()
                .filter(i -> i.getTiendaId().equals(usuario.getTiendaId()))
                .toList();
        comboInsumos.setItems(FXCollections.observableArrayList(insumos));

        cacheInsumos.clear();
        for (var i : insumos) {
            cacheInsumos.put(i.getId(), i);
        }
    }

    @FXML
    private void onAgregarCarrito() {
        var insumo = comboInsumos.getValue();
        if (insumo == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione Insumo", "Por favor seleccione un insumo de la lista.");
            return;
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(txtPrecioComp.getText());
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "El precio de compra debe ser mayor a cero.");
                return;
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "Ingrese un precio numérico válido.");
            return;
        }

        int cantidad = spinCantidad.getValue();

        // Si ya existe en el carrito, sumar cantidad
        var existente = listaCarrito.stream()
                .filter(item -> item.getInsumoId().equals(insumo.getId()))
                .findFirst();

        if (existente.isPresent()) {
            var det = existente.get();
            det.setCantidad(det.getCantidad() + cantidad);
            det.setSubtotal(det.getPrecioUnitario().multiply(BigDecimal.valueOf(det.getCantidad())));
            tableCarrito.refresh();
        } else {
            var det = new CompraDetalle();
            det.setInsumoId(insumo.getId());
            det.setPrecioUnitario(precio);
            det.setCantidad(cantidad);
            det.setSubtotal(precio.multiply(BigDecimal.valueOf(cantidad)));
            listaCarrito.add(det);
        }

        recalcularTotales();
        comboInsumos.getSelectionModel().clearSelection();
        txtPrecioComp.clear();
        spinCantidad.getValueFactory().setValue(1);
    }

    private void recalcularTotales() {
        var total = BigDecimal.ZERO;
        for (var item : listaCarrito) {
            total = total.add(item.getSubtotal());
        }

        var subtotal = total.divide(BigDecimal.valueOf(1.18), 4, RoundingMode.HALF_UP);
        var igv = total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        lblResSubtotal.setText("S/ " + subtotal.toPlainString());
        lblResIgv.setText("S/ " + igv.toPlainString());
        lblResTotal.setText("S/ " + total.setScale(2, RoundingMode.HALF_UP).toPlainString());
        
        recalcularSaldoPendiente(total);
    }

    private void recalcularSaldoPendiente(BigDecimal total) {
        BigDecimal cuenta = BigDecimal.ZERO;
        try {
            String txt = txtMontoCuenta.getText().trim();
            if (!txt.isEmpty()) {
                cuenta = new BigDecimal(txt);
            } else {
                lblSaldoPendiente.setText("S/ 0.00");
                return;
            }
        } catch (NumberFormatException e) {
            // ignored
        }
        BigDecimal saldo = total.subtract(cuenta);
        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            saldo = BigDecimal.ZERO;
        }
        lblSaldoPendiente.setText("S/ " + saldo.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    @FXML
    private void onProcesarCompra() {
        var prov = comboProveedores.getValue();
        if (prov == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Proveedor Faltante", "Seleccione un proveedor o distribuidor.");
            return;
        }
        if (listaCarrito.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Carrito Vacío", "Agregue insumos al detalle antes de procesar.");
            return;
        }

        var metodo = comboMetodoPago.getValue();

        BigDecimal abono = null;
        try {
            String abonoText = txtMontoCuenta.getText().trim();
            if (!abonoText.isEmpty()) {
                abono = new BigDecimal(abonoText);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Monto inválido", "El monto ingresado a cuenta no es válido.");
            return;
        }

        try {
            var compra = compraService.registrarCompraMultiproducto(
                    prov.getId(),
                    usuario.getId(),
                    usuario.getTiendaId(),
                    listaCarrito,
                    metodo.name(),
                    abono
            );

            mostrarAlerta(Alert.AlertType.INFORMATION, "Compra Procesada", "La compra se registró exitosamente. Nro. Orden: " + compra.getNumeroOrden());
            
            // Resetear formulario
            listaCarrito.clear();
            comboProveedores.getSelectionModel().clearSelection();
            txtMontoCuenta.clear();
            recalcularTotales();
            onCargarDatos(); // Recargar inventarios para ver stocks actualizados

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Transacción", "No se pudo registrar la compra: " + e.getMessage());
        }
    }

    // --- Métodos de Tab 2: Historial ---
    @FXML
    public void onCargarHistorial() {
        if (usuario == null) return;
        // Carga los datos de los proveedores e insumos antes por seguridad
        onCargarDatos();

        // Cargar las órdenes de compra asociadas a esta sucursal/tienda
        var list = compraService.findByTiendaId(usuario.getTiendaId());
        tableHistorial.setItems(FXCollections.observableArrayList(list));
        
        paneDetalleHist.setVisible(false);
    }

    private void mostrarDetalleHistorial(CompraCabecera compra) {
        paneDetalleHist.setVisible(true);

        lblHistDetTitle.setText("DETALLE DE COMPRA #" + compra.getId() + " (" + compra.getNumeroOrden() + ")");
        var prov = cacheProveedores.get(compra.getProveedorId());
        lblHistDetProveedor.setText("Proveedor: " + (prov != null ? prov.getNombreEmpresa() : "Proveedor #" + compra.getProveedorId()));
        
        lblHistDetEstadoBadge.setText(compra.getEstadoFinanciero().name());
        lblHistDetEstadoBadge.getStyleClass().clear();
        lblHistDetEstadoBadge.getStyleClass().addAll("badge");
        switch (compra.getEstadoFinanciero()) {
            case POR_PAGAR -> lblHistDetEstadoBadge.getStyleClass().add("badge-danger");
            case PAGO_PARCIAL -> lblHistDetEstadoBadge.getStyleClass().add("badge-warning");
            case PAGADO -> lblHistDetEstadoBadge.getStyleClass().add("badge-success");
            case ANULADO -> lblHistDetEstadoBadge.getStyleClass().add("badge-danger");
        }

        // Cargar detalles de los insumos comprados
        var detalles = compraService.findDetallesByCompraId(compra.getId());
        tableDetalleHist.setItems(FXCollections.observableArrayList(detalles));

        // Calcular saldos financieros
        BigDecimal total = compra.getMontoTotal();
        BigDecimal pagado = getMontoAbonadoCompra(compra.getId());
        BigDecimal saldo = total.subtract(pagado);
        if (saldo.compareTo(BigDecimal.ZERO) < 0) saldo = BigDecimal.ZERO;

        lblHistTotalCompra.setText("S/ " + total.setScale(2, RoundingMode.HALF_UP).toPlainString());
        lblHistAbonado.setText("S/ " + pagado.setScale(2, RoundingMode.HALF_UP).toPlainString());
        lblHistSaldo.setText("S/ " + saldo.setScale(2, RoundingMode.HALF_UP).toPlainString());

        // Generar acciones (ej: pagar saldo pendiente)
        boxAccionesCompra.getChildren().clear();
        if (compra.getEstadoFinanciero() != EstadoFinanciero.PAGADO && compra.getEstadoFinanciero() != EstadoFinanciero.ANULADO && saldo.compareTo(BigDecimal.ZERO) > 0) {
            var btnPagar = new Button("Pagar Saldo Restante");
            btnPagar.getStyleClass().addAll("btn-primary");
            final BigDecimal saldoAPagar = saldo;
            btnPagar.setOnAction(e -> onPagarSaldoCompra(compra, saldoAPagar));
            boxAccionesCompra.getChildren().add(btnPagar);
        } else if (compra.getEstadoFinanciero() == EstadoFinanciero.PAGADO) {
            var lblOk = new Label("COMPRA TOTALMENTE CANCELADA");
            lblOk.getStyleClass().addAll("badge", "badge-success");
            boxAccionesCompra.getChildren().add(lblOk);
        }
    }

    private BigDecimal getMontoAbonadoCompra(Integer compraId) {
        BigDecimal sum = BigDecimal.ZERO;
        String sql = "SELECT SUM(monto) FROM movimientos_caja WHERE compra_id = ? AND tipo = 'SALIDA'";
        try (var conn = opty.config.DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, compraId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal res = rs.getBigDecimal(1);
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

    private void onPagarSaldoCompra(CompraCabecera compra, BigDecimal saldo) {
        var confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Pagar Saldo Pendiente");
        confirm.setHeaderText("Vas a pagar el saldo restante de S/ " + saldo.setScale(2, RoundingMode.HALF_UP).toPlainString() + " al proveedor.");
        confirm.setContentText("¿Deseas registrar este egreso de caja chica para saldar esta cuenta?");
        confirm.initOwner(stage);

        var btnSi = new ButtonType("Sí, Registrar Pago");
        var btnNo = new ButtonType("No, Mantener Deuda");
        confirm.getButtonTypes().setAll(btnSi, btnNo);

        var res = confirm.showAndWait();
        if (res.isPresent() && res.get() == btnSi) {
            registrarEgresoSaldoCompra(compra.getId(), compra.getTiendaId(), compra.getUsuarioId(), saldo);
            
            var okAlert = new Alert(Alert.AlertType.INFORMATION);
            okAlert.setTitle("Pago Registrado");
            okAlert.setHeaderText(null);
            okAlert.setContentText("Se registró el pago de saldo por S/ " + saldo.setScale(2, RoundingMode.HALF_UP).toPlainString() + " en caja chica.");
            okAlert.initOwner(stage);
            okAlert.showAndWait();

            // Recargar datos e historial
            onCargarHistorial();
        }
    }

    private void registrarEgresoSaldoCompra(Integer compraId, Integer tiendaId, Integer usuarioId, BigDecimal saldo) {
        String insertCaja = "INSERT INTO movimientos_caja (tienda_id, usuario_id, compra_id, tipo, metodo_pago, monto, descripcion) VALUES (?, ?, ?, 'SALIDA', 'EFECTIVO', ?, 'Pago de saldo pendiente a proveedor')";
        String updateCab = "UPDATE compras_cabecera SET estado_financiero = 'PAGADO' WHERE id = ?";
        
        java.sql.Connection conn = null;
        try {
            conn = opty.config.DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            try (var psCaja = conn.prepareStatement(insertCaja)) {
                psCaja.setInt(1, tiendaId);
                psCaja.setInt(2, usuarioId);
                psCaja.setInt(3, compraId);
                psCaja.setBigDecimal(4, saldo);
                psCaja.executeUpdate();
            }
            
            try (var psUpdate = conn.prepareStatement(updateCab)) {
                psUpdate.setInt(1, compraId);
                psUpdate.executeUpdate();
            }
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error al registrar pago de saldo a proveedor", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
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
