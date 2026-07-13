package opty.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import opty.model.entity.Paciente;
import opty.model.entity.Producto;
import opty.model.entity.Usuario;
import opty.model.entity.VentaCabecera;
import opty.model.entity.VentaDetalle;
import opty.model.enums.MetodoPago;
import opty.model.enums.TipoComprobante;
import opty.model.enums.EstadoFinanciero;
import opty.service.PacienteService;
import opty.service.ProductoService;
import opty.service.VentaService;
import opty.service.impl.PacienteServiceImpl;
import opty.service.impl.ProductoServiceImpl;
import opty.service.impl.VentaServiceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VentasController implements ModuleController {

    // --- Inputs Cliente ---
    @FXML private TextField txtClienteDoc;
    @FXML private Label lblClienteNombre;

    // --- Selección de Producto ---
    @FXML private ComboBox<Producto> comboProductos;
    @FXML private Label lblStockProd;
    @FXML private Label lblPrecioProd;
    @FXML private Spinner<Integer> spinCantidad;

    // --- Selección de Luna / Custom ---
    @FXML private ComboBox<String> comboCustomTrabajo;
    @FXML private ComboBox<String> comboCustomMaterial;
    @FXML private ComboBox<String> comboCustomLado;
    @FXML private TextField txtCustomPrecio;
    @FXML private TextField txtCustomDetalles;
    @FXML private CheckBox fxIdCustomMonturaCliente; // Wait! In FXML it was chkCustomMonturaCliente! Let's check
    @FXML private CheckBox chkCustomMonturaCliente;
    @FXML private CheckBox chkCustomLunaCliente;

    // --- Carrito de Ventas ---
    @FXML private TableView<VentaDetalle> tableCarrito;
    @FXML private TableColumn<VentaDetalle, String> colCartProducto;
    @FXML private TableColumn<VentaDetalle, String> colCartPrecio;
    @FXML private TableColumn<VentaDetalle, String> colCartCantidad;
    @FXML private TableColumn<VentaDetalle, String> colCartSubtotal;
    @FXML private TableColumn<VentaDetalle, Void> colCartAcciones;

    // --- Totales y Pagos ---
    @FXML private ComboBox<MetodoPago> comboMetodoPago;
    @FXML private ComboBox<TipoComprobante> comboComprobante;
    @FXML private Label lblResSubtotal;
    @FXML private Label lblResIgv;
    @FXML private Label lblResTotal;
    @FXML private TextField txtMontoCuenta;
    @FXML private Label lblSaldoPendiente;

    // --- Tab 2: Historial de Ventas y Cuentas por Cobrar ---
    @FXML private TableView<VentaCabecera> tableHistorial;
    @FXML private TableColumn<VentaCabecera, String> colHistId;
    @FXML private TableColumn<VentaCabecera, String> colHistTicket;
    @FXML private TableColumn<VentaCabecera, String> colHistPaciente;
    @FXML private TableColumn<VentaCabecera, String> colHistFecha;
    @FXML private TableColumn<VentaCabecera, String> colHistEstadoFin;
    @FXML private TableColumn<VentaCabecera, String> colHistTotal;

    @FXML private VBox paneDetalleHist;
    @FXML private Label lblHistDetTitle;
    @FXML private Label lblHistDetPaciente;
    @FXML private Label lblHistDetEstadoBadge;
    @FXML private TableView<VentaDetalle> tableDetalleHist;
    @FXML private TableColumn<VentaDetalle, String> colDetProducto;
    @FXML private TableColumn<VentaDetalle, String> colDetPrecio;
    @FXML private TableColumn<VentaDetalle, String> colDetCantidad;
    @FXML private TableColumn<VentaDetalle, String> colDetSubtotal;

    @FXML private Label lblHistTotalVenta;
    @FXML private Label lblHistAbonado;
    @FXML private Label lblHistSaldo;
    @FXML private Label lblHistTipoEntrega;
    @FXML private Label lblHistEstadoEntrega;
    @FXML private HBox boxAccionesVenta;

    // --- Servicios ---
    private final PacienteService pacienteService = new PacienteServiceImpl();
    private final ProductoService productoService = new ProductoServiceImpl();
    private final VentaService ventaService = new VentaServiceImpl();
    private final opty.service.ConsultaService consultaService = new opty.service.impl.ConsultaServiceImpl();
    private final opty.service.OrdenTrabajoService ordenTrabajoService = new opty.service.impl.OrdenTrabajoServiceImpl();

    private Stage stage;
    private Usuario usuario;
    private Paciente pacienteSeleccionado;

    private final ObservableList<VentaDetalle> listaCarrito = FXCollections.observableArrayList();
    private final Map<Integer, Producto> cacheProductos = new HashMap<>();
    private final Map<Integer, Paciente> cachePacientes = new HashMap<>();

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarProductos();
        
        Paciente p = UserSession.getInstance().getPacienteSeleccionado();
        if (p != null) {
            this.pacienteSeleccionado = p;
            this.txtClienteDoc.setText(p.getNumDocumento());
            this.lblClienteNombre.setText(p.nombreCompleto());
            UserSession.getInstance().setPacienteSeleccionado(null);
        }
    }

    @FXML
    public void initialize() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Configurar Spinner de cantidad
        spinCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        // Configurar Comboboxes de pago y comprobante
        comboMetodoPago.setItems(FXCollections.observableArrayList(MetodoPago.values()));
        comboMetodoPago.getSelectionModel().select(MetodoPago.EFECTIVO);

        comboComprobante.setItems(FXCollections.observableArrayList(TipoComprobante.values()));
        comboComprobante.getSelectionModel().select(TipoComprobante.BOLETA);

        // Configurar combos de personalización de lunas
        var trabajos = FXCollections.observableArrayList(
            java.util.Arrays.stream(opty.model.enums.TipoTrabajo.values())
                .map(Enum::name)
                .toList()
        );
        comboCustomTrabajo.setItems(trabajos);
        comboCustomTrabajo.getSelectionModel().select(opty.model.enums.TipoTrabajo.LENTE_COMPLETO.name());

        comboCustomMaterial.setItems(FXCollections.observableArrayList("Resina Básica", "Resina Antireflex", "Resina Blue Defense", "Policarbonato", "Vidrio"));
        comboCustomMaterial.getSelectionModel().select("Resina Antireflex");

        comboCustomLado.setItems(FXCollections.observableArrayList("Ambos Ojos (Par)", "Luna Ojo Derecho (OD)", "Luna Ojo Izquierdo (OI)", "Solo Montura"));
        comboCustomLado.getSelectionModel().select("Ambos Ojos (Par)");

        // Configurar columnas de la tabla del carrito
        colCartProducto.setCellValueFactory(cell -> {
            var prodId = cell.getValue().getProductoId();
            var prod = cacheProductos.get(prodId);
            return new SimpleStringProperty(prod != null ? prod.getNombre() : "Producto #" + prodId);
        });

        colCartPrecio.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getPrecioUnitario().setScale(2, RoundingMode.HALF_UP).toPlainString()));
        colCartCantidad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCantidad().toString()));
        colCartSubtotal.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getSubtotal().setScale(2, RoundingMode.HALF_UP).toPlainString()));

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
                lblPrecioProd.setText("S/ " + newVal.getPrecioVenta().setScale(2, RoundingMode.HALF_UP).toPlainString());
            } else {
                lblStockProd.setText("-");
                lblPrecioProd.setText("-");
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
        colHistTicket.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroTicket()));
        colHistPaciente.setCellValueFactory(cell -> {
            var pac = cachePacientes.get(cell.getValue().getPacienteId());
            return new SimpleStringProperty(pac != null ? pac.nombreCompleto() : "Paciente #" + cell.getValue().getPacienteId());
        });
        colHistFecha.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFechaEmision() != null ? cell.getValue().getFechaEmision().format(dtf) : "-"
        ));
        colHistEstadoFin.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstadoFinanciero().name()));
        colHistTotal.setCellValueFactory(cell -> new SimpleStringProperty("S/ " + cell.getValue().getMontoTotal().setScale(2, RoundingMode.HALF_UP).toPlainString()));

        // Bind Columnas Detalle de Historial
        colDetProducto.setCellValueFactory(cell -> {
            var prodId = cell.getValue().getProductoId();
            var prod = cacheProductos.get(prodId);
            return new SimpleStringProperty(prod != null ? prod.getNombre() : "Producto #" + prodId);
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

        // 2. Intentar buscar por nombre
        var matches = pacienteService.searchByNombre(term);
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
    private void onAgregarCustomCarrito() {
        var trabajo = comboCustomTrabajo.getValue();
        var material = comboCustomMaterial.getValue();
        var lado = comboCustomLado.getValue();
        var precioStr = txtCustomPrecio.getText().trim();
        var detalles = txtCustomDetalles.getText().trim();

        if (trabajo == null || material == null || lado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, seleccione el Tipo de Trabajo, Material y Lado/Ojo.");
            return;
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(precioStr);
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "El precio de venta debe ser mayor a cero.");
                return;
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "Por favor, ingrese un precio numérico válido.");
            return;
        }

        String nombreProd = lado + " - " + material;
        if (!detalles.isEmpty()) {
            nombreProd += " (" + detalles + ")";
        }

        try {
            Producto prod = findOrCreateCustomProduct(nombreProd, precio);
            
            var det = new VentaDetalle();
            det.setProductoId(prod.getId());
            det.setPrecioUnitario(precio);
            det.setCantidad(1);
            det.setSubtotal(precio);
            listaCarrito.add(det);

            recalcularTotales();

            txtCustomPrecio.clear();
            txtCustomDetalles.clear();
            chkCustomMonturaCliente.setSelected(false);
            chkCustomLunaCliente.setSelected(false);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al crear producto", "No se pudo registrar el ítem personalizado: " + e.getMessage());
        }
    }

    private Producto findOrCreateCustomProduct(String nombre, BigDecimal precio) {
        var opt = productoService.findByNombreAndTienda(nombre, usuario.getTiendaId());
        if (opt.isPresent()) {
            return opt.get();
        }

        var p = new Producto();
        p.setTiendaId(usuario.getTiendaId());
        p.setNombre(nombre);
        p.setPrecioVenta(precio);
        p.setStockActual(9999);
        p.setStockMinimo(0);
        p.setActivo(true);
        p.setCategoria(opty.model.enums.CategoriaProducto.LENTE);

        String codigo = "TEMP-LUNA-" + System.currentTimeMillis() + "-" + (int)(100 + Math.random() * 900);
        p.setCodigo(codigo);

        return productoService.save(p);
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
            var venta = ventaService.registrarVentaMultiproducto(
                    pacienteSeleccionado.getId(),
                    usuario.getId(),
                    usuario.getTiendaId(),
                    comp,
                    listaCarrito,
                    metodo.name(),
                    abono
            );

            mostrarAlerta(Alert.AlertType.INFORMATION, "Venta Exitosa", "La venta fue registrada. Comprobante generado: " + venta.getNumeroTicket());
            
            // Si hay algún ítem en el carrito que sea categoría LENTE, requiere Orden de Trabajo en Laboratorio
            boolean requiereOT = false;
            String tipoTrabajoStr = "LENTE_COMPLETO";
            boolean usaMonturaCliente = false;
            String detMontura = "";
            boolean usaLunaCliente = false;
            String detLuna = "";

            boolean tieneLentes = false;
            for (var item : listaCarrito) {
                var prod = cacheProductos.get(item.getProductoId());
                if (prod == null) {
                    var prodOpt = productoService.findById(item.getProductoId());
                    if (prodOpt.isPresent()) {
                        prod = prodOpt.get();
                        cacheProductos.put(prod.getId(), prod);
                    }
                }
                if (prod != null && prod.getCategoria() == opty.model.enums.CategoriaProducto.LENTE) {
                    tieneLentes = true;
                    break;
                }
            }

            if (tieneLentes) {
                requiereOT = true;
                if (comboCustomTrabajo.getValue() != null) {
                    tipoTrabajoStr = comboCustomTrabajo.getValue();
                }
                usaMonturaCliente = chkCustomMonturaCliente.isSelected();
                detMontura = txtCustomDetalles.getText();
                usaLunaCliente = chkCustomLunaCliente.isSelected();
                detLuna = "Luna con medida/montaje";
            }

            if (requiereOT) {
                var otObj = new opty.model.entity.OrdenTrabajo();
                otObj.setVentaId(venta.getId());
                otObj.setEstadoFisico(opty.model.enums.EstadoFisicoOT.PENDIENTE);
                otObj.setTipoTrabajo(opty.model.enums.TipoTrabajo.valueOf(tipoTrabajoStr));
                otObj.setUsaMonturaCliente(usaMonturaCliente);
                otObj.setDetallesMonturaCliente(detMontura.isEmpty() ? null : detMontura);
                otObj.setUsaLunaCliente(usaLunaCliente);
                otObj.setDetallesLunaCliente(detLuna);
                
                var consultas = consultaService.findByPacienteId(pacienteSeleccionado.getId());
                if (consultas != null && !consultas.isEmpty()) {
                    var ultimaConsulta = consultas.get(consultas.size() - 1);
                    var hc = consultaService.findHistorialByConsultaId(ultimaConsulta.getId());
                    if (hc != null) {
                        otObj.setHistorialClinicoId(hc.getId());
                    }
                }
                
                otObj.setFechaPrometida(java.time.LocalDateTime.now().plusDays(3));
                
                var otService = new opty.service.impl.OrdenTrabajoServiceImpl();
                otService.save(otObj);
            }

            // Limpiar todo tras el registro
            listaCarrito.clear();
            pacienteSeleccionado = null;
            txtClienteDoc.clear();
            lblClienteNombre.setText("-");
            txtMontoCuenta.clear();
            recalcularTotales();
            cargarProductos(); 

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Procesar", "No se pudo concretar la venta: " + e.getMessage());
        }
    }

    // --- Métodos de Tab 2: Historial ---
    @FXML
    public void onCargarHistorial() {
        if (usuario == null) return;
        
        // Cargar todos los pacientes en caché
        var pacientes = pacienteService.findAll();
        cachePacientes.clear();
        for (var p : pacientes) {
            cachePacientes.put(p.getId(), p);
        }

        // Asegurar que cacheProductos esté cargado con los productos
        var todosProd = productoService.findAll();
        for (var p : todosProd) {
            cacheProductos.put(p.getId(), p);
        }

        // Cargar las ventas registradas para esta tienda/sucursal
        var list = ventaService.findByTiendaId(usuario.getTiendaId());
        tableHistorial.setItems(FXCollections.observableArrayList(list));
        
        paneDetalleHist.setVisible(false);
    }

    private void mostrarDetalleHistorial(VentaCabecera venta) {
        paneDetalleHist.setVisible(true);

        lblHistDetTitle.setText("DETALLE DE VENTA #" + venta.getId() + " (" + venta.getNumeroTicket() + ")");
        var pac = cachePacientes.get(venta.getPacienteId());
        lblHistDetPaciente.setText("Cliente: " + (pac != null ? pac.nombreCompleto() : "Paciente #" + venta.getPacienteId()));
        
        lblHistDetEstadoBadge.setText(venta.getEstadoFinanciero().name());
        lblHistDetEstadoBadge.getStyleClass().clear();
        lblHistDetEstadoBadge.getStyleClass().addAll("badge");
        switch (venta.getEstadoFinanciero()) {
            case POR_PAGAR -> lblHistDetEstadoBadge.getStyleClass().add("badge-danger");
            case PAGO_PARCIAL -> lblHistDetEstadoBadge.getStyleClass().add("badge-warning");
            case PAGADO -> lblHistDetEstadoBadge.getStyleClass().add("badge-success");
            case ANULADO -> lblHistDetEstadoBadge.getStyleClass().add("badge-danger");
        }

        // Cargar productos detallados de esta venta
        var detalles = ventaService.findDetallesByVentaId(venta.getId());
        tableDetalleHist.setItems(FXCollections.observableArrayList(detalles));

        // Calcular saldos financieros
        BigDecimal total = venta.getMontoTotal();
        BigDecimal abonado = getMontoCobradoVenta(venta.getId());
        BigDecimal saldo = total.subtract(abonado);
        if (saldo.compareTo(BigDecimal.ZERO) < 0) saldo = BigDecimal.ZERO;

        lblHistTotalVenta.setText("S/ " + total.setScale(2, RoundingMode.HALF_UP).toPlainString());
        lblHistAbonado.setText("S/ " + abonado.setScale(2, RoundingMode.HALF_UP).toPlainString());
        lblHistSaldo.setText("S/ " + saldo.setScale(2, RoundingMode.HALF_UP).toPlainString());

        // Acciones
        boxAccionesVenta.getChildren().clear();
        if (venta.getEstadoFinanciero() != EstadoFinanciero.PAGADO && venta.getEstadoFinanciero() != EstadoFinanciero.ANULADO && saldo.compareTo(BigDecimal.ZERO) > 0) {
            var btnCobrar = new Button("Cobrar Saldo Restante");
            btnCobrar.getStyleClass().addAll("btn-primary");
            final BigDecimal saldoACobrar = saldo;
            btnCobrar.setOnAction(e -> onCobrarSaldoVenta(venta, saldoACobrar));
            boxAccionesVenta.getChildren().add(btnCobrar);
        } else if (venta.getEstadoFinanciero() == EstadoFinanciero.PAGADO) {
            var lblOk = new Label("VENTA TOTALMENTE CANCELADA");
            lblOk.getStyleClass().addAll("badge", "badge-success");
            boxAccionesVenta.getChildren().add(lblOk);
        }

        // Buscar si existe OrdenTrabajo asociada a la venta para Tipo y Estado de Entrega
        var ots = ordenTrabajoService.findByVentaId(venta.getId());
        if (ots != null && !ots.isEmpty()) {
            var ot = ots.get(0);
            lblHistTipoEntrega.setText("ORDEN DE TRABAJO (TALLER)");
            lblHistTipoEntrega.setStyle("-fx-text-fill: -color-info-text; -fx-font-weight: bold;");
            
            lblHistEstadoEntrega.setText(ot.getEstadoFisico().name());
            switch (ot.getEstadoFisico()) {
                case PENDIENTE -> lblHistEstadoEntrega.setStyle("-fx-text-fill: -color-danger-text; -fx-font-weight: bold;");
                case LABORATORIO -> lblHistEstadoEntrega.setStyle("-fx-text-fill: -color-warning-text; -fx-font-weight: bold;");
                case LISTO -> lblHistEstadoEntrega.setStyle("-fx-text-fill: -color-primary; -fx-font-weight: bold;");
                case ENTREGADO -> lblHistEstadoEntrega.setStyle("-fx-text-fill: -color-success-text; -fx-font-weight: bold;");
            }

            // Si el estado de la OT es LISTO, y la venta ya está pagada (o es parcial), mostrar botón para entregar los lentes
            if (ot.getEstadoFisico() == opty.model.enums.EstadoFisicoOT.LISTO) {
                var btnEntregar = new Button("Entregar Pedido al Cliente");
                btnEntregar.getStyleClass().addAll("btn-success");
                btnEntregar.setOnAction(e -> onEntregarPedidoCliente(ot, venta));
                boxAccionesVenta.getChildren().add(btnEntregar);
            }
        } else {
            lblHistTipoEntrega.setText("VENTA DIRECTA (INMEDIATA)");
            lblHistTipoEntrega.setStyle("-fx-text-fill: -color-success-text; -fx-font-weight: bold;");
            lblHistEstadoEntrega.setText("ENTREGADO");
            lblHistEstadoEntrega.setStyle("-fx-text-fill: -color-success-text; -fx-font-weight: bold;");
        }
    }

    private BigDecimal getMontoCobradoVenta(Integer ventaId) {
        BigDecimal sum = BigDecimal.ZERO;
        String sql = "SELECT SUM(monto) FROM movimientos_caja WHERE venta_id = ? AND tipo = 'ENTRADA'";
        try (var conn = opty.config.DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ventaId);
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

    private void onCobrarSaldoVenta(VentaCabecera venta, BigDecimal saldo) {
        var confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cobrar Saldo Pendiente");
        confirm.setHeaderText("Vas a cobrar el saldo restante de S/ " + saldo.setScale(2, RoundingMode.HALF_UP).toPlainString() + " al cliente.");
        confirm.setContentText("¿Deseas registrar este ingreso en la caja chica?");
        confirm.initOwner(stage);

        var btnSi = new ButtonType("Sí, Registrar Cobro");
        var btnNo = new ButtonType("No, Mantener Deuda");
        confirm.getButtonTypes().setAll(btnSi, btnNo);

        var res = confirm.showAndWait();
        if (res.isPresent() && res.get() == btnSi) {
            registrarIngresoSaldoVenta(venta.getId(), venta.getTiendaId(), venta.getUsuarioId(), saldo);
            
            var okAlert = new Alert(Alert.AlertType.INFORMATION);
            okAlert.setTitle("Cobro Registrado");
            okAlert.setHeaderText(null);
            okAlert.setContentText("Se registró el cobro de saldo por S/ " + saldo.setScale(2, RoundingMode.HALF_UP).toPlainString() + " en caja chica.");
            okAlert.initOwner(stage);
            okAlert.showAndWait();

            // Recargar historial
            onCargarHistorial();
        }
    }

    private void registrarIngresoSaldoVenta(Integer ventaId, Integer tiendaId, Integer usuarioId, BigDecimal saldo) {
        String insertCaja = "INSERT INTO movimientos_caja (tienda_id, usuario_id, venta_id, tipo, metodo_pago, monto, descripcion) VALUES (?, ?, ?, 'ENTRADA', 'EFECTIVO', ?, 'Cobro de saldo pendiente de ticket')";
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
            throw new RuntimeException("Error al registrar cobro de saldo a cliente", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }

    private void onEntregarPedidoCliente(opty.model.entity.OrdenTrabajo ot, VentaCabecera venta) {
        var confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Entregar Pedido");
        confirm.setHeaderText("Vas a entregar los lentes listos al paciente.");
        confirm.setContentText("¿Confirmas que el paciente está recibiendo el producto a satisfacción?");
        confirm.initOwner(stage);

        var btnSi = new ButtonType("Sí, Entregar");
        var btnNo = new ButtonType("No, Cancelar");
        confirm.getButtonTypes().setAll(btnSi, btnNo);

        var res = confirm.showAndWait();
        if (res.isPresent() && res.get() == btnSi) {
            try {
                ot.setEstadoFisico(opty.model.enums.EstadoFisicoOT.ENTREGADO);
                ordenTrabajoService.update(ot);
                
                var okAlert = new Alert(Alert.AlertType.INFORMATION);
                okAlert.setTitle("Pedido Entregado");
                okAlert.setHeaderText(null);
                okAlert.setContentText("La orden de trabajo fue marcada como ENTREGADA.");
                okAlert.initOwner(stage);
                okAlert.showAndWait();

                // Recargar historial
                mostrarDetalleHistorial(venta);
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al entregar", "No se pudo actualizar el estado de la entrega: " + e.getMessage());
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
