package opty.service.impl;

import opty.model.entity.Insumo;
import opty.model.entity.OrdenTrabajo;
import opty.model.entity.Paciente;
import opty.model.entity.Producto;
import opty.repository.InsumoRepository;
import opty.repository.implementacion.InsumoRepositoryImpl;
import opty.repository.MovimientoCajaRepository;
import opty.repository.implementacion.MovimientoCajaRepositoryImpl;
import opty.repository.OrdenTrabajoRepository;
import opty.repository.implementacion.OrdenTrabajoRepositoryImpl;
import opty.repository.PacienteRepository;
import opty.repository.implementacion.PacienteRepositoryImpl;
import opty.repository.ProductoRepository;
import opty.repository.implementacion.ProductoRepositoryImpl;
import opty.repository.VentaRepository;
import opty.repository.implementacion.VentaRepositoryImpl;
import opty.repository.ConfigTiendaRepository;
import opty.repository.implementacion.ConfigTiendaRepositoryImpl;
import opty.service.DashboardService;

import opty.config.DatabaseConfig;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @Override
    public List<EmployeeSales> getTopVendedores(Integer tiendaId, int limit) {
        var list = new ArrayList<EmployeeSales>();
        var sql = tiendaId != null ?
                "SELECT u.nombre, u.apellido_p, SUM(v.monto_total) AS total_venta " +
                "FROM ventas_cabecera v " +
                "INNER JOIN usuarios u ON v.usuario_id = u.id " +
                "WHERE v.tienda_id = ? " +
                "GROUP BY u.id, u.nombre, u.apellido_p " +
                "ORDER BY total_venta DESC " +
                "LIMIT ?" :
                "SELECT u.nombre, u.apellido_p, SUM(v.monto_total) AS total_venta " +
                "FROM ventas_cabecera v " +
                "INNER JOIN usuarios u ON v.usuario_id = u.id " +
                "GROUP BY u.id, u.nombre, u.apellido_p " +
                "ORDER BY total_venta DESC " +
                "LIMIT ?";

        try (var conn = DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            if (tiendaId != null) {
                ps.setInt(1, tiendaId);
                ps.setInt(2, limit);
            } else {
                ps.setInt(1, limit);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    var name = rs.getString("nombre") + " " + rs.getString("apellido_p");
                    var total = rs.getBigDecimal("total_venta");
                    list.add(new EmployeeSales(name, total != null ? total : BigDecimal.ZERO));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener top vendedores", e);
        }
        return list;
    }

    @Override
    public List<DailySales> getVentasUltimos7Dias(Integer tiendaId) {
        var list = new ArrayList<DailySales>();
        var sql = tiendaId != null ?
                "SELECT DATE(v.fecha_emision) as dia, SUM(v.monto_total) as total " +
                "FROM ventas_cabecera v " +
                "WHERE v.tienda_id = ? AND v.fecha_emision >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                "GROUP BY DATE(v.fecha_emision) " +
                "ORDER BY dia ASC" :
                "SELECT DATE(v.fecha_emision) as dia, SUM(v.monto_total) as total " +
                "FROM ventas_cabecera v " +
                "WHERE v.fecha_emision >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                "GROUP BY DATE(v.fecha_emision) " +
                "ORDER BY dia ASC";

        try (var conn = DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            if (tiendaId != null) {
                ps.setInt(1, tiendaId);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    var date = rs.getDate("dia").toLocalDate();
                    var total = rs.getBigDecimal("total");
                    list.add(new DailySales(date, total != null ? total : BigDecimal.ZERO));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener ventas de últimos 7 días", e);
        }
        return list;
    }

    @Override
    public List<DailySales> getComprasUltimos7Dias(Integer tiendaId) {
        var list = new ArrayList<DailySales>();
        var sql = tiendaId != null ?
                "SELECT DATE(c.fecha_emision) as dia, SUM(c.monto_total) as total " +
                "FROM compras_cabecera c " +
                "WHERE c.tienda_id = ? AND c.fecha_emision >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                "GROUP BY DATE(c.fecha_emision) " +
                "ORDER BY dia ASC" :
                "SELECT DATE(c.fecha_emision) as dia, SUM(c.monto_total) as total " +
                "FROM compras_cabecera c " +
                "WHERE c.fecha_emision >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                "GROUP BY DATE(c.fecha_emision) " +
                "ORDER BY dia ASC";

        try (var conn = DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            if (tiendaId != null) {
                ps.setInt(1, tiendaId);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    var date = rs.getDate("dia").toLocalDate();
                    var total = rs.getBigDecimal("total");
                    list.add(new DailySales(date, total != null ? total : BigDecimal.ZERO));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener compras de últimos 7 días", e);
        }
        return list;
    }

    private String getTiendaCodigo(Integer tiendaId) {
        if (tiendaId == null) return null;
        var repo = new ConfigTiendaRepositoryImpl();
        return repo.findById(tiendaId)
                .map(opty.model.entity.ConfigTienda::getCodigo)
                .orElse(null);
    }

    @Override
    public List<ResumenVentasTienda> getResumenVentasTienda(Integer tiendaId) {
        var list = new ArrayList<ResumenVentasTienda>();
        var tiendaCodigo = getTiendaCodigo(tiendaId);
        var sql = "SELECT tienda, nombre_optica, total_ventas, total_subtotal, total_igv, total_con_igv, cobrado_efectivo, cobrado_yape, cobrado_tarjeta, cobrado_transferencia FROM v_resumen_ventas_tienda";
        if (tiendaCodigo != null) {
            sql += " WHERE tienda = ?";
        }
        try (var conn = DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            if (tiendaCodigo != null) {
                ps.setString(1, tiendaCodigo);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ResumenVentasTienda(
                        rs.getString("tienda"),
                        rs.getString("nombre_optica"),
                        rs.getInt("total_ventas"),
                        rs.getBigDecimal("total_subtotal"),
                        rs.getBigDecimal("total_igv"),
                        rs.getBigDecimal("total_con_igv"),
                        rs.getBigDecimal("cobrado_efectivo"),
                        rs.getBigDecimal("cobrado_yape"),
                        rs.getBigDecimal("cobrado_tarjeta"),
                        rs.getBigDecimal("cobrado_transferencia")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar v_resumen_ventas_tienda", e);
        }
        return list;
    }

    @Override
    public List<InsumoLiniado> getInsumosBajoMinimoView(Integer tiendaId) {
        var list = new ArrayList<InsumoLiniado>();
        var tiendaCodigo = getTiendaCodigo(tiendaId);
        var sql = "SELECT tienda, codigo_insumo, nombre, categoria, stock_actual, stock_minimo, unidades_faltantes FROM v_insumos_bajo_minimo";
        if (tiendaCodigo != null) {
            sql += " WHERE tienda = ?";
        }
        try (var conn = DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            if (tiendaCodigo != null) {
                ps.setString(1, tiendaCodigo);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new InsumoLiniado(
                        rs.getString("tienda"),
                        rs.getString("codigo_insumo"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("stock_actual"),
                        rs.getInt("stock_minimo"),
                        rs.getInt("unidades_faltantes")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar v_insumos_bajo_minimo", e);
        }
        return list;
    }

    @Override
    public List<HistorialClinicoReciente> getHistorialClinicoPaciente(Integer tiendaId, int limit) {
        var list = new ArrayList<HistorialClinicoReciente>();
        var tiendaCodigo = getTiendaCodigo(tiendaId);
        var sql = "SELECT paciente, num_documento, fecha_consulta, motivo, graduacion_od, graduacion_oi, observaciones, edad, tienda FROM v_historial_clinico_paciente";
        if (tiendaCodigo != null) {
            sql += " WHERE tienda = ?";
        }
        sql += " LIMIT ?";
        try (var conn = DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            if (tiendaCodigo != null) {
                ps.setString(1, tiendaCodigo);
                ps.setInt(2, limit);
            } else {
                ps.setInt(1, limit);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    var ts = rs.getTimestamp("fecha_consulta");
                    var fecha = ts != null ? ts.toLocalDateTime() : null;
                    list.add(new HistorialClinicoReciente(
                        rs.getString("paciente"),
                        rs.getString("num_documento"),
                        fecha,
                        rs.getString("motivo"),
                        rs.getString("graduacion_od"),
                        rs.getString("graduacion_oi"),
                        rs.getString("observaciones"),
                        rs.getInt("edad"),
                        rs.getString("tienda")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar v_historial_clinico_paciente", e);
        }
        return list;
    }

    @Override
    public List<OrdenPendienteView> getOrdenesPendientesView(Integer tiendaId) {
        var list = new ArrayList<OrdenPendienteView>();
        var tiendaCodigo = getTiendaCodigo(tiendaId);
        var sql = "SELECT orden_id, estado_fisico, tipo_trabajo, fecha_creacion, fecha_prometida, alerta, paciente, tienda FROM v_ordenes_pendientes";
        if (tiendaCodigo != null) {
            sql += " WHERE tienda = ?";
        }
        try (var conn = DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            if (tiendaCodigo != null) {
                ps.setString(1, tiendaCodigo);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    var tsCreacion = rs.getTimestamp("fecha_creacion");
                    var tsPrometida = rs.getTimestamp("fecha_prometida");
                    list.add(new OrdenPendienteView(
                        rs.getInt("orden_id"),
                        rs.getString("estado_fisico"),
                        rs.getString("tipo_trabajo"),
                        tsCreacion != null ? tsCreacion.toLocalDateTime() : null,
                        tsPrometida != null ? tsPrometida.toLocalDateTime() : null,
                        rs.getString("alerta"),
                        rs.getString("paciente"),
                        rs.getString("tienda")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar v_ordenes_pendientes", e);
        }
        return list;
    }

    @Override
    public List<ProductoDisponibleView> getProductosDisponiblesView(Integer tiendaId) {
        var list = new ArrayList<ProductoDisponibleView>();
        var tiendaCodigo = getTiendaCodigo(tiendaId);
        var sql = "SELECT producto_id, tienda, codigo_producto, nombre, categoria, genero_objetivo, precio_venta, stock_actual, stock_minimo FROM v_productos_disponibles";
        if (tiendaCodigo != null) {
            sql += " WHERE tienda = ?";
        }
        try (var conn = DatabaseConfig.getConnection();
             var ps = conn.prepareStatement(sql)) {
            if (tiendaCodigo != null) {
                ps.setString(1, tiendaCodigo);
            }
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ProductoDisponibleView(
                        rs.getInt("producto_id"),
                        rs.getString("tienda"),
                        rs.getString("codigo_producto"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getString("genero_objetivo"),
                        rs.getBigDecimal("precio_venta"),
                        rs.getInt("stock_actual"),
                        rs.getInt("stock_minimo")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar v_productos_disponibles", e);
        }
        return list;
    }
}

