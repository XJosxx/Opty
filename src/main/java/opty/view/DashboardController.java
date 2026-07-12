package opty.view;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import opty.model.entity.Usuario;
import opty.service.DashboardService;
import opty.service.DashboardService.*;
import opty.service.impl.DashboardServiceImpl;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController implements ModuleController {

    @FXML private Label lblVentasHoy;
    @FXML private Label lblPacientes;
    @FXML private Label lblBajoStock;
    @FXML private Label lblOrdenesPendientes;
    
    @FXML private AreaChart<String, Number> salesChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private PieChart paymentChart;

    @FXML private BarChart<String, Number> productsChart;
    @FXML private CategoryAxis prodXAxis;
    @FXML private NumberAxis prodYAxis;

    @FXML private VBox historialClinicoList;
    @FXML private VBox vendedoresLeaderboard;
    @FXML private VBox insumosBajoStockList;
    @FXML private VBox ordenesPendientesList;

    private final DashboardService dashboardService = new DashboardServiceImpl();
    private Stage stage;
    private Usuario usuario;

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarDatos();
    }

    private void cargarDatos() {
        var tiendaId = usuario.getTiendaId();

        lblVentasHoy.setText("S/ " + dashboardService.getVentasDelDia(tiendaId).toPlainString());
        lblPacientes.setText(String.valueOf(dashboardService.getPacientesCount(tiendaId)));

        var bajoStock = dashboardService.getProductosBajoStock(tiendaId);
        lblBajoStock.setText(String.valueOf(bajoStock.size()));

        var pendientes = dashboardService.getOrdenesPendientes();
        lblOrdenesPendientes.setText(String.valueOf(pendientes.size()));

        // Cargar gráficos e indicadores interactivos (visualización de vistas de 02_logica.sql)
        cargarGraficoVentas(tiendaId);
        cargarGraficoPagos(tiendaId);
        cargarGraficoProductos(tiendaId);
        cargarTopVendedores(tiendaId);
        cargarHistorialClinico(tiendaId);
        cargarInsumosBajoStock(tiendaId);
        cargarOrdenesPendientes(tiendaId);
    }

    private void cargarGraficoVentas(Integer tiendaId) {
        salesChart.getData().clear();
        
        var trendSales = dashboardService.getVentasUltimos7Dias(tiendaId);
        var seriesSales = new XYChart.Series<String, Number>();
        seriesSales.setName("Ingresos (Ventas)");
        var formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (var day : trendSales) {
            seriesSales.getData().add(new XYChart.Data<>(day.date().format(formatter), day.totalSales()));
        }

        var trendPurchases = dashboardService.getComprasUltimos7Dias(tiendaId);
        var seriesPurchases = new XYChart.Series<String, Number>();
        seriesPurchases.setName("Egresos (Compras)");

        for (var day : trendPurchases) {
            seriesPurchases.getData().add(new XYChart.Data<>(day.date().format(formatter), day.totalSales()));
        }

        salesChart.getData().addAll(seriesSales, seriesPurchases);
    }

    private void cargarGraficoPagos(Integer tiendaId) {
        paymentChart.getData().clear();
        var resumen = dashboardService.getResumenVentasTienda(tiendaId);
        if (resumen.isEmpty()) {
            return;
        }

        BigDecimal efectivo = BigDecimal.ZERO;
        BigDecimal yape = BigDecimal.ZERO;
        BigDecimal tarjeta = BigDecimal.ZERO;
        BigDecimal transferencia = BigDecimal.ZERO;

        for (var r : resumen) {
            efectivo = efectivo.add(r.cobradoEfectivo() != null ? r.cobradoEfectivo() : BigDecimal.ZERO);
            yape = yape.add(r.cobradoYape() != null ? r.cobradoYape() : BigDecimal.ZERO);
            tarjeta = tarjeta.add(r.cobradoTarjeta() != null ? r.cobradoTarjeta() : BigDecimal.ZERO);
            transferencia = transferencia.add(r.cobradoTransferencia() != null ? r.cobradoTransferencia() : BigDecimal.ZERO);
        }

        if (efectivo.compareTo(BigDecimal.ZERO) > 0) {
            paymentChart.getData().add(new PieChart.Data("Efectivo (S/ " + efectivo + ")", efectivo.doubleValue()));
        }
        if (yape.compareTo(BigDecimal.ZERO) > 0) {
            paymentChart.getData().add(new PieChart.Data("Yape (S/ " + yape + ")", yape.doubleValue()));
        }
        if (tarjeta.compareTo(BigDecimal.ZERO) > 0) {
            paymentChart.getData().add(new PieChart.Data("Tarjeta (S/ " + tarjeta + ")", tarjeta.doubleValue()));
        }
        if (transferencia.compareTo(BigDecimal.ZERO) > 0) {
            paymentChart.getData().add(new PieChart.Data("Transferencia (S/ " + transferencia + ")", transferencia.doubleValue()));
        }
    }

    private void cargarGraficoProductos(Integer tiendaId) {
        productsChart.getData().clear();
        var productos = dashboardService.getProductosDisponiblesView(tiendaId);
        
        var series = new XYChart.Series<String, Number>();
        
        // Agrupar stock por categoría
        java.util.Map<String, Integer> stockByCategory = new java.util.HashMap<>();
        for (var p : productos) {
            stockByCategory.merge(p.categoria(), p.stockActual(), Integer::sum);
        }
        
        for (var entry : stockByCategory.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        productsChart.getData().add(series);
    }

    private void cargarTopVendedores(Integer tiendaId) {
        vendedoresLeaderboard.getChildren().clear();
        var top = dashboardService.getTopVendedores(tiendaId, 5);

        if (top.isEmpty()) {
            var emptyLabel = new Label("No hay registros de ventas.");
            emptyLabel.setStyle("-fx-text-fill: -color-graphite-light; -fx-font-style: italic;");
            vendedoresLeaderboard.getChildren().add(emptyLabel);
            return;
        }

        for (var seller : top) {
            var hbox = new HBox(12);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setStyle("-fx-padding: 8px 12px; -fx-background-color: -color-warm-beige-light; -fx-background-radius: 8px;");

            var nameLabel = new Label(seller.employeeName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-graphite-soft;");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            var amountLabel = new Label("S/ " + seller.totalSales().toPlainString());
            amountLabel.getStyleClass().addAll("badge", "badge-info");

            hbox.getChildren().addAll(nameLabel, amountLabel);
            vendedoresLeaderboard.getChildren().add(hbox);
        }
    }

    private void cargarHistorialClinico(Integer tiendaId) {
        historialClinicoList.getChildren().clear();
        var historial = dashboardService.getHistorialClinicoPaciente(tiendaId, 5);
        if (historial.isEmpty()) {
            var emptyLabel = new Label("No hay historial clínico reciente.");
            emptyLabel.setStyle("-fx-text-fill: -color-graphite-light; -fx-font-style: italic;");
            historialClinicoList.getChildren().add(emptyLabel);
            return;
        }
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (var hc : historial) {
            var vbox = new VBox(4);
            vbox.setStyle("-fx-padding: 10px; -fx-background-color: -color-warm-beige-light; -fx-background-radius: 8px;");

            var titleHbox = new HBox(12);
            titleHbox.setAlignment(Pos.CENTER_LEFT);
            
            var nameLabel = new Label(hc.paciente() + " (Edad: " + hc.edad() + ")");
            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-graphite-dark;");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            var dateLabel = new Label(hc.fechaConsulta() != null ? hc.fechaConsulta().format(formatter) : "");
            dateLabel.setStyle("-fx-text-fill: -color-graphite-light; -fx-font-size: 11px;");

            titleHbox.getChildren().addAll(nameLabel, dateLabel);

            var motivoLabel = new Label("Motivo: " + hc.motivo());
            motivoLabel.setStyle("-fx-text-fill: -color-graphite-soft; -fx-font-size: 12px;");

            var medidasLabel = new Label("Medidas: OD " + hc.graduacionOd() + " | OI " + hc.graduacionOi());
            medidasLabel.setStyle("-fx-text-fill: -color-info-text; -fx-font-weight: bold; -fx-font-size: 12px;");

            var obsLabel = new Label("Obs: " + (hc.observaciones() != null ? hc.observaciones() : "Sin observaciones"));
            obsLabel.setStyle("-fx-text-fill: -color-graphite-light; -fx-font-style: italic; -fx-font-size: 11px;");

            vbox.getChildren().addAll(titleHbox, motivoLabel, medidasLabel, obsLabel);
            historialClinicoList.getChildren().add(vbox);
        }
    }

    private void cargarInsumosBajoStock(Integer tiendaId) {
        insumosBajoStockList.getChildren().clear();
        var insumos = dashboardService.getInsumosBajoMinimoView(tiendaId);
        if (insumos.isEmpty()) {
            var emptyLabel = new Label("No hay insumos bajo stock mínimo.");
            emptyLabel.setStyle("-fx-text-fill: -color-graphite-light; -fx-font-style: italic;");
            insumosBajoStockList.getChildren().add(emptyLabel);
            return;
        }
        for (var i : insumos) {
            var hbox = new HBox(12);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setStyle("-fx-padding: 8px 12px; -fx-background-color: -color-warm-beige-light; -fx-background-radius: 8px;");

            var nameLabel = new Label(i.nombre() + " (" + i.codigoInsumo() + ")");
            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-graphite-soft;");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            var diffLabel = new Label("Faltan: " + i.unidadesFaltantes());
            diffLabel.getStyleClass().addAll("badge", "badge-danger");

            hbox.getChildren().addAll(nameLabel, diffLabel);
            insumosBajoStockList.getChildren().add(hbox);
        }
    }

    private void cargarOrdenesPendientes(Integer tiendaId) {
        ordenesPendientesList.getChildren().clear();
        var ordenes = dashboardService.getOrdenesPendientesView(tiendaId);
        if (ordenes.isEmpty()) {
            var emptyLabel = new Label("No hay órdenes de trabajo pendientes.");
            emptyLabel.setStyle("-fx-text-fill: -color-graphite-light; -fx-font-style: italic;");
            ordenesPendientesList.getChildren().add(emptyLabel);
            return;
        }
        for (var ot : ordenes) {
            var hbox = new HBox(12);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setStyle("-fx-padding: 8px 12px; -fx-background-color: -color-warm-beige-light; -fx-background-radius: 8px;");

            var infoLabel = new Label("OT #" + ot.ordenId() + " — " + ot.paciente() + " (" + ot.tipoTrabajo() + ")");
            infoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-graphite-soft;");
            HBox.setHgrow(infoLabel, Priority.ALWAYS);

            var alertLabel = new Label(ot.alerta());
            if ("VENCIDA".equals(ot.alerta())) {
                alertLabel.getStyleClass().addAll("badge", "badge-danger");
            } else {
                alertLabel.getStyleClass().addAll("badge", "badge-success");
            }

            hbox.getChildren().addAll(infoLabel, alertLabel);
            ordenesPendientesList.getChildren().add(hbox);
        }
    }
}
