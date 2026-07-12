package opty.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import opty.model.entity.Paciente;
import opty.model.entity.Producto;
import opty.model.entity.Usuario;
import opty.model.entity.VentaCabecera;
import opty.model.entity.VentaDetalle;
import opty.model.enums.MetodoPago;
import opty.model.enums.TipoComprobante;
import opty.service.PacienteService;
import opty.service.ProductoService;
import opty.service.VentaService;
import opty.service.impl.PacienteServiceImpl;
import opty.service.impl.ProductoServiceImpl;
import opty.service.impl.VentaServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VentasController implements ModuleController {

    // --- Inputs Cliente ---
    @FXML private TextField txtClienteDoc;
    @FXML private Label lblClienteNombre;

    // --- Inputs Producto ---
    @FXML private ComboBox<Producto> comboProductos;
    @FXML private Label lblStockProd;
    @FXML private Label lblPrecioProd;
    @FXML private Spinner<Integer> spinCantidad;

    // --- Carrito de Compras ---
    @FXML private TableView<VentaDetalle> tableCarrito;
    @FXML private TableColumn<VentaDetalle, String> colCartProducto;
    @FXML private TableColumn<VentaDetalle, String> colCartPrecio;
    @FXML private TableColumn<VentaDetalle, String> colCartCantidad;
    @FXML private TableColumn<VentaDetalle, String> colCartSubtotal;
    @FXML private TableColumn<VentaDetalle, Void> colCartAcciones;

    // --- Totales y Comprobantes ---
    @FXML private ComboBox<MetodoPago> comboMetodoPago;
    @FXML private ComboBox<TipoComprobante> comboComprobante;
    
    @FXML private Label lblResSubtotal;
    @FXML private Label lblResIgv;
    @FXML private Label lblResTotal;

    // --- Servicios e Inventario ---
    private final PacienteService pacienteService = new PacienteServiceImpl();
    private final ProductoService productoService = new ProductoServiceImpl();
    private final VentaService ventaService = new VentaServiceImpl();

    private Stage stage;
    private Usuario usuario;
    private Paciente pacienteSeleccionado;
    
    private final ObservableList<VentaDetalle> listaCarrito = FXCollections.observableArrayList();
    private final Map<Integer, Producto> cacheProductos = new HashMap<>();

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarProductos();
    }

    @FXML
    public void initialize() {
        // Configurar Spinner de cantidad
        spinCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        // Configurar Comboboxes de pago y comprobante
        comboMetodoPago.setItems(FXCollections.observableArrayList(MetodoPago.values()));
        comboMetodoPago.getSelectionModel().select(MetodoPago.EFECTIVO);

        comboComprobante.setItems(FXCollections.observableArrayList(TipoComprobante.values()));
        comboComprobante.getSelectionModel().select(TipoComprobante.BOLETA);

        // Configurar columnas de la tabla del carrito
        colCartProducto.setCellValueFactory(cell -> {
            var prodId = cell.getValue().getProductoId();
            var prod = cacheProductos.get(prodId);
            return new SimpleStringProperty(prod != null ? prod.getNombre() : "Producto #" + prodId);
        });

        colCartPrecio.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getPrecioUnitario().toPlainString()));
        colCartCantidad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCantidad().toString()));
        colCartSubtotal.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getSubtotal().toPlainString()));

        // Columna de acciones (Eliminar del carrito)
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

        // Escuchar selección de productos para actualizar stock y precio unitario
        comboProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblStockProd.setText(String.valueOf(newVal.getStockActual()));
                lblPrecioProd.setText("S/ " + newVal.getPrecioVenta().toPlainString());
            } else {
                lblStockProd.setText("-");
                lblPrecioProd.setText("-");
            }
        });
    }

    private void cargarProductos() {
        if (usuario != null) {
            var disponibles = productoService.findDisponibles(usuario.getTiendaId());
            comboProductos.setItems(FXCollections.observableArrayList(disponibles));
            cacheProductos.clear();
            for (var p : disponibles) {
                cacheProductos.put(p.getId(), p);
            }
        }
    }

    @FXML
    private void onBuscarCliente() {
        var term = txtClienteDoc.getText();
        if (term == null || term.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Búsqueda vacía", "Ingrese el número de documento o nombre del cliente.");
            return;
        }

        // 1. Intentar buscar por documento exacto
        var opt = pacienteService.findByNumDocumento(term);
        if (opt.isPresent()) {
            pacienteSeleccionado = opt.get();
            lblClienteNombre.setText(pacienteSeleccionado.nombreCompleto());
            return;
        }

        // 2. Intentar buscar por nombre (la consulta SQL en searchByNombre busca en nombre completo y documento)
        var matches = pacienteService.searchByNombre(term);
        // Filtrar por la tienda del usuario activo
        if (usuario != null) {
            matches = matches.stream()
                    .filter(p -> p.getTiendaId().equals(usuario.getTiendaId()))
                    .toList();
        }

        if (matches.isEmpty()) {
            pacienteSeleccionado = null;
            lblClienteNombre.setText("-");
            mostrarAlerta(Alert.AlertType.WARNING, "No encontrado", "No se encontró ningún paciente con el documento o nombre ingresado.");
        } else if (matches.size() == 1) {
            pacienteSeleccionado = matches.get(0);
            lblClienteNombre.setText(pacienteSeleccionado.nombreCompleto());
        } else {
            // Múltiples coincidencias, mostrar cuadro de diálogo para seleccionar
            var dialog = new ChoiceDialog<Paciente>(matches.get(0), matches);
            dialog.setTitle("Seleccionar Cliente");
            dialog.setHeaderText("Se encontraron múltiples clientes coincidentes.");
            dialog.setContentText("Seleccione el cliente correspondiente:");
            dialog.initOwner(stage);
            var result = dialog.showAndWait();
            if (result.isPresent()) {
                pacienteSeleccionado = result.get();
                lblClienteNombre.setText(pacienteSeleccionado.nombreCompleto());
            }
        }
    }

    @FXML
    private void onAgregarCarrito() {
        var producto = comboProductos.getValue();
        if (producto == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione Producto", "Por favor seleccione un producto del catálogo.");
            return;
        }

        int cantidad = spinCantidad.getValue();
        int stockDisponible = producto.getStockActual();

        // Validar si el producto ya está en el carrito para sumar cantidades
        int cantExistente = 0;
        VentaDetalle itemExistente = null;
        for (var item : listaCarrito) {
            if (item.getProductoId().equals(producto.getId())) {
                cantExistente = item.getCantidad();
                itemExistente = item;
                break;
            }
        }

        if (cantidad + cantExistente > stockDisponible) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock Insuficiente", "No puede agregar más unidades de las disponibles en el stock.");
            return;
        }

        if (itemExistente != null) {
            itemExistente.setCantidad(cantidad + cantExistente);
            itemExistente.setSubtotal(itemExistente.getPrecioUnitario().multiply(BigDecimal.valueOf(itemExistente.getCantidad())));
            tableCarrito.refresh();
        } else {
            var det = new VentaDetalle();
            det.setProductoId(producto.getId());
            det.setPrecioUnitario(producto.getPrecioVenta());
            det.setCantidad(cantidad);
            det.setSubtotal(producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)));
            listaCarrito.add(det);
        }

        recalcularTotales();
        comboProductos.getSelectionModel().clearSelection();
        spinCantidad.getValueFactory().setValue(1);
    }

    private void recalcularTotales() {
        var total = BigDecimal.ZERO;
        for (var item : listaCarrito) {
            total = total.add(item.getSubtotal());
        }

        // En Perú el precio de catálogo ya incluye el IGV
        var subtotal = total.divide(BigDecimal.valueOf(1.18), 4, RoundingMode.HALF_UP);
        var igv = total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        lblResSubtotal.setText("S/ " + subtotal.toPlainString());
        lblResIgv.setText("S/ " + igv.toPlainString());
        lblResTotal.setText("S/ " + total.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    @FXML
    private void onProcesarVenta() {
        if (pacienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Cliente Faltante", "Cargue un cliente/paciente válido mediante su documento.");
            return;
        }
        if (listaCarrito.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Carrito Vacío", "Debe agregar al menos un producto al carrito de compras.");
            return;
        }

        var metodo = comboMetodoPago.getValue();
        var comp = comboComprobante.getValue();

        try {
            var venta = ventaService.registrarVentaMultiproducto(
                    pacienteSeleccionado.getId(),
                    usuario.getId(),
                    usuario.getTiendaId(),
                    comp,
                    listaCarrito,
                    metodo.name()
            );

            mostrarAlerta(Alert.AlertType.INFORMATION, "Venta Exitosa", "La venta fue registrada. Comprobante generado: " + venta.getNumeroTicket());
            
            // Limpiar todo tras el registro
            listaCarrito.clear();
            pacienteSeleccionado = null;
            txtClienteDoc.clear();
            lblClienteNombre.setText("-");
            recalcularTotales();
            cargarProductos(); // Recargar catálogo para actualizar stocks

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Procesar", "No se pudo concretar la venta: " + e.getMessage());
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
