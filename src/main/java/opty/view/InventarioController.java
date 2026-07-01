package opty.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import opty.model.entity.Insumo;
import opty.model.entity.Kardex;
import opty.model.entity.Producto;
import opty.model.entity.Usuario;
import opty.model.enums.TipoMovimientoKardex;
import opty.service.InsumoService;
import opty.service.KardexService;
import opty.service.ProductoService;
import opty.service.impl.InsumoServiceImpl;
import opty.service.impl.KardexServiceImpl;
import opty.service.impl.ProductoServiceImpl;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventarioController implements ModuleController {

    // --- Tab 1: Productos ---
    @FXML private TextField txtBuscarProducto;
    @FXML private TableView<Producto> tableProductos;
    @FXML private TableColumn<Producto, String> colProdCodigo;
    @FXML private TableColumn<Producto, String> colProdNombre;
    @FXML private TableColumn<Producto, String> colProdCategoria;
    @FXML private TableColumn<Producto, String> colProdPrecio;
    @FXML private TableColumn<Producto, String> colProdStock;
    @FXML private TableColumn<Producto, String> colProdMinimo;
    @FXML private TableColumn<Producto, Label> colProdEstado;

    // --- Tab 2: Insumos ---
    @FXML private TextField txtBuscarInsumo;
    @FXML private TableView<Insumo> tableInsumos;
    @FXML private TableColumn<Insumo, String> colInsCodigo;
    @FXML private TableColumn<Insumo, String> colInsNombre;
    @FXML private TableColumn<Insumo, String> colInsCategoria;
    @FXML private TableColumn<Insumo, String> colInsStock;
    @FXML private TableColumn<Insumo, String> colInsMinimo;
    @FXML private TableColumn<Insumo, Label> colInsEstado;

    // --- Tab 3: Kardex ---
    @FXML private ComboBox<String> comboFiltroMovimiento;
    @FXML private TableView<Kardex> tableKardex;
    @FXML private TableColumn<Kardex, String> colKardexFecha;
    @FXML private TableColumn<Kardex, String> colKardexItem;
    @FXML private TableColumn<Kardex, String> colKardexTipo;
    @FXML private TableColumn<Kardex, String> colKardexMotivo;
    @FXML private TableColumn<Kardex, String> colKardexCantidad;

    // --- Servicios ---
    private final ProductoService productoService = new ProductoServiceImpl();
    private final InsumoService insumoService = new InsumoServiceImpl();
    private final KardexService kardexService = new KardexServiceImpl();

    private Stage stage;
    private Usuario usuario;

    private final Map<Integer, Producto> cacheProductos = new HashMap<>();
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
        // 1. Configurar columnas de Productos
        colProdCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodigo()));
        colProdNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colProdCategoria.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCategoria() != null ? cell.getValue().getCategoria().name() : "-"
        ));
        colProdPrecio.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getPrecioVenta().toPlainString()));
        colProdStock.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getStockActual())));
        colProdMinimo.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getStockMinimo())));
        colProdEstado.setCellValueFactory(cell -> {
            var p = cell.getValue();
            var label = new Label();
            if (p.getStockActual() <= p.getStockMinimo()) {
                label.setText("STOCK BAJO");
                label.getStyleClass().addAll("badge", "badge-danger");
            } else {
                label.setText("NORMAL");
                label.getStyleClass().addAll("badge", "badge-success");
            }
            return new SimpleObjectProperty<>(label);
        });

        // 2. Configurar columnas de Insumos
        colInsCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodigo()));
        colInsNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colInsCategoria.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getCategoria() != null ? cell.getValue().getCategoria().name() : "-"
        ));
        colInsStock.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getStockActual())));
        colInsMinimo.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getStockMinimo())));
        colInsEstado.setCellValueFactory(cell -> {
            var ins = cell.getValue();
            var label = new Label();
            if (ins.getStockActual() <= ins.getStockMinimo()) {
                label.setText("STOCK BAJO");
                label.getStyleClass().addAll("badge", "badge-danger");
            } else {
                label.setText("NORMAL");
                label.getStyleClass().addAll("badge", "badge-success");
            }
            return new SimpleObjectProperty<>(label);
        });

        // 3. Configurar columnas de Kardex
        var dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colKardexFecha.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFecha() != null ? cell.getValue().getFecha().format(dtf) : "-"
        ));
        colKardexItem.setCellValueFactory(cell -> {
            var k = cell.getValue();
            if (k.getProductoId() != null && k.getProductoId() > 0) {
                var prod = cacheProductos.get(k.getProductoId());
                return new SimpleStringProperty(prod != null ? "PROD: " + prod.getNombre() : "Producto #" + k.getProductoId());
            } else if (k.getInsumoId() != null && k.getInsumoId() > 0) {
                var ins = cacheInsumos.get(k.getInsumoId());
                return new SimpleStringProperty(ins != null ? "INS: " + ins.getNombre() : "Insumo #" + k.getInsumoId());
            }
            return new SimpleStringProperty("-");
        });
        colKardexTipo.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getTipoMovimiento() != null ? cell.getValue().getTipoMovimiento().name() : "-"
        ));
        colKardexMotivo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMotivo()));
        colKardexCantidad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCantidad().toString()));

        // Configurar filtro de movimientos
        comboFiltroMovimiento.setItems(FXCollections.observableArrayList("TODOS", "ENTRADA", "SALIDA"));
        comboFiltroMovimiento.getSelectionModel().select("TODOS");
    }

    @FXML
    private void onCargarDatos() {
        if (usuario == null) return;
        
        var tiendaId = usuario.getTiendaId();

        // 1. Cargar productos
        var prods = productoService.findDisponibles(tiendaId);
        tableProductos.setItems(FXCollections.observableArrayList(prods));
        cacheProductos.clear();
        for (var p : prods) cacheProductos.put(p.getId(), p);

        // 2. Cargar insumos
        var insumos = insumoService.findAll().stream()
                .filter(i -> i.getTiendaId().equals(tiendaId))
                .toList();
        tableInsumos.setItems(FXCollections.observableArrayList(insumos));
        cacheInsumos.clear();
        for (var i : insumos) cacheInsumos.put(i.getId(), i);

        // 3. Cargar Kardex
        var movimientos = kardexService.findByTiendaId(tiendaId);
        tableKardex.setItems(FXCollections.observableArrayList(movimientos));
    }

    @FXML
    private void onBuscarProducto() {
        var query = txtBuscarProducto.getText();
        if (query == null || query.isBlank()) {
            onCargarDatos();
            return;
        }
        var filtered = productoService.findDisponibles(usuario.getTiendaId()).stream()
                .filter(p -> p.getNombre().toLowerCase().contains(query.toLowerCase()))
                .toList();
        tableProductos.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void onBuscarInsumo() {
        var query = txtBuscarInsumo.getText();
        if (query == null || query.isBlank()) {
            onCargarDatos();
            return;
        }
        var filtered = insumoService.findAll().stream()
                .filter(i -> i.getTiendaId().equals(usuario.getTiendaId()) && i.getNombre().toLowerCase().contains(query.toLowerCase()))
                .toList();
        tableInsumos.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void onFiltrarKardex() {
        var filter = comboFiltroMovimiento.getValue();
        if (filter == null || filter.equals("TODOS")) {
            onCargarDatos();
            return;
        }

        var filtered = kardexService.findByTiendaId(usuario.getTiendaId()).stream()
                .filter(k -> k.getTipoMovimiento() != null && k.getTipoMovimiento().name().equals(filter))
                .toList();
        tableKardex.setItems(FXCollections.observableArrayList(filtered));
    }
}
