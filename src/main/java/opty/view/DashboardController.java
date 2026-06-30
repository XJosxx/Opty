package opty.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import opty.model.entity.OrdenTrabajo;
import opty.model.entity.Paciente;
import opty.model.entity.Usuario;
import opty.service.DashboardService;
import opty.service.impl.DashboardServiceImpl;

import java.util.List;

public class DashboardController implements ModuleController {

    @FXML private Label lblVentasHoy;
    @FXML private Label lblPacientes;
    @FXML private Label lblBajoStock;
    @FXML private Label lblOrdenesPendientes;
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

        cargarOrdenesVencidas(dashboardService.getOrdenesVencidas());
        cargarPacientesRecientes(dashboardService.getPacientesRecientes(tiendaId, 5));
    }

    private void cargarOrdenesVencidas(List<OrdenTrabajo> ordenes) {
        ordenesVencidasList.getChildren().clear();
        if (ordenes.isEmpty()) {
            ordenesVencidasList.getChildren().add(new Label("No hay órdenes vencidas"));
            return;
        }
        for (var ot : ordenes) {
            var label = new Label("OT #" + ot.getId() + " — " + ot.getTipoTrabajo());
            label.getStyleClass().add("badge");
            label.getStyleClass().add("badge-danger");
            ordenesVencidasList.getChildren().add(label);
        }
    }

    private void cargarPacientesRecientes(List<Paciente> pacientes) {
        pacientesRecientesList.getChildren().clear();
        if (pacientes.isEmpty()) {
            pacientesRecientesList.getChildren().add(new Label("No hay pacientes registrados"));
            return;
        }
        for (var p : pacientes) {
            var label = new Label(p.nombreCompleto());
            label.getStyleClass().add("badge");
            label.getStyleClass().add("badge-info");
            pacientesRecientesList.getChildren().add(label);
        }
    }
}
