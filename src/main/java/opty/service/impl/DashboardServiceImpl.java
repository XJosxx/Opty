package opty.service.impl;

import opty.model.entity.Insumo;
import opty.model.entity.OrdenTrabajo;
import opty.model.entity.Paciente;
import opty.model.entity.Producto;
import opty.repository.InsumoRepository;
import opty.repository.InsumoRepositoryImpl;
import opty.repository.MovimientoCajaRepository;
import opty.repository.MovimientoCajaRepositoryImpl;
import opty.repository.OrdenTrabajoRepository;
import opty.repository.OrdenTrabajoRepositoryImpl;
import opty.repository.PacienteRepository;
import opty.repository.PacienteRepositoryImpl;
import opty.repository.ProductoRepository;
import opty.repository.ProductoRepositoryImpl;
import opty.repository.VentaRepository;
import opty.repository.VentaRepositoryImpl;
import opty.service.DashboardService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DashboardServiceImpl implements DashboardService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final InsumoRepository insumoRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final PacienteRepository pacienteRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;

    public DashboardServiceImpl() {
        this.ventaRepository = new VentaRepositoryImpl();
        this.productoRepository = new ProductoRepositoryImpl();
        this.insumoRepository = new InsumoRepositoryImpl();
        this.ordenTrabajoRepository = new OrdenTrabajoRepositoryImpl();
        this.pacienteRepository = new PacienteRepositoryImpl();
        this.movimientoCajaRepository = new MovimientoCajaRepositoryImpl();
    }

    public DashboardServiceImpl(VentaRepository ventaRepository, ProductoRepository productoRepository,
                                 InsumoRepository insumoRepository, OrdenTrabajoRepository ordenTrabajoRepository,
                                 PacienteRepository pacienteRepository, MovimientoCajaRepository movimientoCajaRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.insumoRepository = insumoRepository;
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.pacienteRepository = pacienteRepository;
        this.movimientoCajaRepository = movimientoCajaRepository;
    }

    @Override
    public BigDecimal getVentasDelDia(Integer tiendaId) {
        var inicio = LocalDate.now().atStartOfDay();
        var fin = LocalDate.now().atTime(LocalTime.MAX);
        return ventaRepository.sumVentasByDateRange(inicio, fin);
    }

    @Override
    public BigDecimal getComprasDelDia(Integer tiendaId) {
        var inicio = LocalDate.now().atStartOfDay();
        var fin = LocalDate.now().atTime(LocalTime.MAX);
        return movimientoCajaRepository.sumIngresosByDateRange(inicio, fin, tiendaId);
    }

    @Override
    public long getVentasCountDelDia(Integer tiendaId) {
        var inicio = LocalDate.now().atStartOfDay();
        var fin = LocalDate.now().atTime(LocalTime.MAX);
        return ventaRepository.findByDateRange(inicio, fin).size();
    }

    @Override
    public long getPacientesCount(Integer tiendaId) {
        return pacienteRepository.countByTiendaId(tiendaId);
    }

    @Override
    public List<Producto> getProductosBajoStock(Integer tiendaId) {
        if (tiendaId == null) {
            return productoRepository.findAll().stream()
                    .filter(p -> p.getStockActual() <= p.getStockMinimo())
                    .toList();
        }
        return productoRepository.findDisponibles(tiendaId).stream()
                .filter(p -> p.getStockActual() <= p.getStockMinimo())
                .toList();
    }

    @Override
    public List<Insumo> getInsumosBajoStock(Integer tiendaId) {
        if (tiendaId == null) {
            return insumoRepository.findAll().stream()
                    .filter(i -> i.getStockActual() <= i.getStockMinimo())
                    .toList();
        }
        return insumoRepository.findBajoStock(tiendaId);
    }

    @Override
    public List<OrdenTrabajo> getOrdenesPendientes() {
        return ordenTrabajoRepository.findPendientes();
    }

    @Override
    public List<OrdenTrabajo> getOrdenesVencidas() {
        return ordenTrabajoRepository.findVencidas();
    }

    @Override
    public BigDecimal getTotalVentasMes(Integer tiendaId) {
        var now = LocalDate.now();
        var inicio = now.withDayOfMonth(1).atStartOfDay();
        var fin = now.atTime(LocalTime.MAX);
        return ventaRepository.sumVentasByDateRange(inicio, fin);
    }

    @Override
    public BigDecimal getTotalComprasMes(Integer tiendaId) {
        var now = LocalDate.now();
        var inicio = now.withDayOfMonth(1).atStartOfDay();
        var fin = now.atTime(LocalTime.MAX);
        return movimientoCajaRepository.sumEgresosByDateRange(inicio, fin, tiendaId);
    }

    @Override
    public List<Paciente> getPacientesRecientes(Integer tiendaId, int limit) {
        return pacienteRepository.findByTiendaId(tiendaId).stream()
                .sorted((a, b) -> b.getFechaRegistro().compareTo(a.getFechaRegistro()))
                .limit(limit)
                .toList();
    }
}
