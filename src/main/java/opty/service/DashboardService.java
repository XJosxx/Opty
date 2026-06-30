package opty.service;

import opty.model.entity.Insumo;
import opty.model.entity.OrdenTrabajo;
import opty.model.entity.Paciente;
import opty.model.entity.Producto;

import java.math.BigDecimal;
import java.util.List;

public interface DashboardService {

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
}
