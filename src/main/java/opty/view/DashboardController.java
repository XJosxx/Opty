package opty.view;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import opty.model.entity.OrdenTrabajo;
import opty.model.entity.Paciente;
import opty.model.entity.Usuario;
import opty.service.DashboardService;
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
    @FXML private VBox vendedoresLeaderboard;
    
    @FXML private VBox ordenesVencidasList;
    @FXML private VBox pacientesRecientesList;

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

        // Cargar gráficos e indicadores interactivos
        cargarGraficoVentas(tiendaId);
        cargarTopVendedores(tiendaId);

        cargarOrdenesVencidas(dashboardService.getOrdenesVencidas());
        cargarPacientesRecientes(dashboardService.getPacientesRecientes(tiendaId, 5));
    }

    private void cargarGraficoVentas(Integer tiendaId) {
        salesChart.getData().clear();
        var trend = dashboardService.getVentasUltimos7Dias(tiendaId);

        var series = new XYChart.Series<String, Number>();
        var formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (var day : trend) {
            series.getData().add(new XYChart.Data<>(day.date().format(formatter), day.totalSales()));
        }

        salesChart.getData().add(series);
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

    private void cargarOrdenesVencidas(List<OrdenTrabajo> ordenes) {
        ordenesVencidasList.getChildren().clear();
        if (ordenes.isEmpty()) {
            var emptyLabel = new Label("No hay órdenes vencidas");
            emptyLabel.setStyle("-fx-text-fill: -color-graphite-light; -fx-font-style: italic;");
            ordenesVencidasList.getChildren().add(emptyLabel);
            return;
        }
        for (var ot : ordenes) {
            var label = new Label("OT #" + ot.getId() + " — " + ot.getTipoTrabajo());
            label.getStyleClass().addAll("badge", "badge-danger");
            label.setMaxWidth(Double.MAX_VALUE);
            ordenesVencidasList.getChildren().add(label);
        }
    }

    private void cargarPacientesRecientes(List<Paciente> pacientes) {
        pacientesRecientesList.getChildren().clear();
        if (pacientes.isEmpty()) {
            var emptyLabel = new Label("No hay pacientes registrados");
            emptyLabel.setStyle("-fx-text-fill: -color-graphite-light; -fx-font-style: italic;");
            pacientesRecientesList.getChildren().add(emptyLabel);
            return;
        }
        for (var p : pacientes) {
            var label = new Label(p.nombreCompleto());
            label.getStyleClass().addAll("badge", "badge-info");
            label.setMaxWidth(Double.MAX_VALUE);
            pacientesRecientesList.getChildren().add(label);
        }
    }
}
