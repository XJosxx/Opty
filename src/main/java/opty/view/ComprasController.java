package opty.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import opty.model.entity.CompraCabecera;
import opty.model.entity.CompraDetalle;
import opty.model.entity.Insumo;
import opty.model.entity.Proveedor;
import opty.model.entity.Usuario;
import opty.model.enums.MetodoPago;
import opty.service.CompraService;
import opty.service.InsumoService;
import opty.service.ProveedorService;
import opty.service.impl.CompraServiceImpl;
import opty.service.impl.InsumoServiceImpl;
import opty.service.impl.ProveedorServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

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

    // --- Servicios ---
    private final ProveedorService proveedorService = new ProveedorServiceImpl();
    private final InsumoService insumoService = new InsumoServiceImpl();
    private final CompraService compraService = new CompraServiceImpl();

    private Stage stage;
    private Usuario usuario;

    private final ObservableList<CompraDetalle> listaCarrito = FXCollections.observableArrayList();
    private final Map<Integer, Insumo> cacheInsumos = new HashMap<>();

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

        colCartPrecio.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getPrecioUnitario().toPlainString()));
        colCartCantidad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCantidad().toString()));
        colCartSubtotal.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getSubtotal().toPlainString()));

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
    }

    private void onCargarDatos() {
        if (usuario == null) return;

        // Cargar proveedores
        var proveedores = proveedorService.findAll();
        comboProveedores.setItems(FXCollections.observableArrayList(proveedores));

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
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione Insumo", "Seleccione un insumo de la lista.");
            return;
        }

        var precioStr = txtPrecioComp.getText();
        if (precioStr == null || precioStr.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio Requerido", "Ingrese el precio unitario de compra facturado.");
            return;
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(precioStr);
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio Inválido", "Ingrese un monto numérico positivo.");
            return;
        }

        int cantidad = spinCantidad.getValue();

        // Validar si ya existe
        CompraDetalle existente = null;
        for (var item : listaCarrito) {
            if (item.getInsumoId().equals(insumo.getId())) {
                existente = item;
                break;
            }
        }

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + cantidad);
            existente.setPrecioUnitario(precio); // Actualizar al último precio
            existente.setSubtotal(precio.multiply(BigDecimal.valueOf(existente.getCantidad())));
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

        try {
            var compra = compraService.registrarCompraMultiproducto(
                    prov.getId(),
                    usuario.getId(),
                    usuario.getTiendaId(),
                    listaCarrito,
                    metodo.name()
            );

            mostrarAlerta(Alert.AlertType.INFORMATION, "Compra Procesada", "La compra se registró exitosamente. Nro. Orden: " + compra.getNumeroOrden());
            
            // Resetear formulario
            listaCarrito.clear();
            comboProveedores.getSelectionModel().clearSelection();
            recalcularTotales();
            onCargarDatos(); // Recargar inventarios para ver stocks actualizados

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Transacción", "No se pudo registrar la compra: " + e.getMessage());
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
