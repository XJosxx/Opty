package opty.service;

import opty.model.entity.Insumo;
import opty.model.entity.OrdenTrabajo;
import opty.model.entity.Paciente;
import opty.model.entity.Producto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

    record EmployeeSales(String employeeName, BigDecimal totalSales) {}
    record DailySales(LocalDate date, BigDecimal totalSales) {}

    // Representation records for SQL Views in 02_logica.sql
    record ResumenVentasTienda(
        String tienda,
        String nombreOptica,
        int totalVentas,
        BigDecimal totalSubtotal,
        BigDecimal totalIgv,
        BigDecimal totalConIgv,
        BigDecimal cobradoEfectivo,
        BigDecimal cobradoYape,
        BigDecimal cobradoTarjeta,
        BigDecimal cobradoTransferencia
    ) {}

    record InsumoLiniado(
        String tienda,
        String codigoInsumo,
        String nombre,
        String categoria,
        int stockActual,
        int stockMinimo,
        int unidadesFaltantes
    ) {}

    record HistorialClinicoReciente(
        String paciente,
        String numDocumento,
        java.time.LocalDateTime fechaConsulta,
        String motivo,
        String graduacionOd,
        String graduacionOi,
        String observaciones,
        int edad,
        String tienda
    ) {}

    record OrdenPendienteView(
        int ordenId,
        String estadoFisico,
        String tipoTrabajo,
        java.time.LocalDateTime fechaCreacion,
        java.time.LocalDateTime fechaPrometida,
        String alerta,
        String paciente,
        String tienda
    ) {}

    record ProductoDisponibleView(
        int productoId,
        String tienda,
        String codigoProducto,
        String nombre,
        String categoria,
        String generoObjetivo,
        BigDecimal precioVenta,
        int stockActual,
        int stockMinimo
    ) {}

    BigDecimal getVentasDelDia(Integer tiendaId);

    BigDecimal getComprasDelDia(Integer tiendaId);

    long getVentasCountDelDia(Integer tiendaId);

    long getPacientesCount(Integer tiendaId);

    List<Producto> getProductosBajoStock(Integer tiendaId);

    List<Insumo> getInsumosBajoStock(Integer tiendaId);

    List<OrdenTrabajo> getOrdenesPendientes();

    List<OrdenTrabajo> getOrdenesVencidas();

    BigDecimal getTotalVentasMes(Integer tiendaId);

    BigDecimal getTotalComprasMes(Integer tiendaId);

    List<Paciente> getPacientesRecientes(Integer tiendaId, int limit);

    List<EmployeeSales> getTopVendedores(Integer tiendaId, int limit);

    List<DailySales> getVentasUltimos7Dias(Integer tiendaId);

    // View-based querying methods
    List<ResumenVentasTienda> getResumenVentasTienda(Integer tiendaId);

    List<InsumoLiniado> getInsumosBajoMinimoView(Integer tiendaId);

    List<HistorialClinicoReciente> getHistorialClinicoPaciente(Integer tiendaId, int limit);

    List<OrdenPendienteView> getOrdenesPendientesView(Integer tiendaId);

    List<ProductoDisponibleView> getProductosDisponiblesView(Integer tiendaId);
}
