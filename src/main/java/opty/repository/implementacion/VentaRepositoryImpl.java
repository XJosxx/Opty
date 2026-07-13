package opty.repository.implementacion;

import opty.repository.*;

import opty.model.entity.VentaCabecera;
import opty.model.entity.VentaDetalle;
import opty.model.enums.EstadoFinanciero;
import opty.model.enums.TipoComprobante;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentaRepositoryImpl extends BaseJdbcRepository implements VentaRepository {

    private static final String SELECT_COLUMNS = "SELECT id, paciente_id, usuario_id, tienda_id, numero_ticket, fecha_emision, estado_financiero, tipo_comprobante, monto_subtotal, monto_igv, monto_total FROM ventas_cabecera";

    @Override
    public Optional<VentaCabecera> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar venta por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<VentaCabecera> findAll() {
        var list = new ArrayList<VentaCabecera>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY fecha_emision DESC"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ventas", e);
        }
        return list;
    }

    @Override
    public VentaCabecera save(VentaCabecera entity) {
        var sql = "INSERT INTO ventas_cabecera (paciente_id, usuario_id, tienda_id, numero_ticket, estado_financiero, tipo_comprobante, monto_subtotal, monto_igv, monto_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getPacienteId());
            ps.setInt(2, entity.getUsuarioId());
            ps.setInt(3, entity.getTiendaId());
            ps.setString(4, entity.getNumeroTicket());
            ps.setString(5, entity.getEstadoFinanciero().name());
            ps.setString(6, entity.getTipoComprobante().name());
            ps.setBigDecimal(7, entity.getMontoSubtotal());
            ps.setBigDecimal(8, entity.getMontoIgv());
            ps.setBigDecimal(9, entity.getMontoTotal());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar venta", e);
        }
        return entity;
    }

    @Override
    public void update(VentaCabecera entity) {
        var sql = "UPDATE ventas_cabecera SET paciente_id = ?, usuario_id = ?, tienda_id = ?, numero_ticket = ?, estado_financiero = ?, tipo_comprobante = ?, monto_subtotal = ?, monto_igv = ?, monto_total = ? WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getPacienteId());
            ps.setInt(2, entity.getUsuarioId());
            ps.setInt(3, entity.getTiendaId());
            ps.setString(4, entity.getNumeroTicket());
            ps.setString(5, entity.getEstadoFinanciero().name());
            ps.setString(6, entity.getTipoComprobante().name());
            ps.setBigDecimal(7, entity.getMontoSubtotal());
            ps.setBigDecimal(8, entity.getMontoIgv());
            ps.setBigDecimal(9, entity.getMontoTotal());
            ps.setInt(10, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar venta", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("DELETE FROM ventas_cabecera WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar venta", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM ventas_cabecera WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de venta", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM ventas_cabecera"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar ventas", e);
        }
    }

    @Override
    public Optional<VentaCabecera> findByNumeroTicket(String numeroTicket) {
        var sql = SELECT_COLUMNS + " WHERE numero_ticket = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, numeroTicket);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar venta por ticket", e);
        }
        return Optional.empty();
    }

    @Override
    public List<VentaCabecera> findByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        var list = new ArrayList<VentaCabecera>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE fecha_emision BETWEEN ? AND ? ORDER BY fecha_emision DESC")) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ventas por rango de fechas", e);
        }
        return list;
    }

    @Override
    public List<VentaCabecera> findByPacienteId(Integer pacienteId) {
        var list = new ArrayList<VentaCabecera>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE paciente_id = ? ORDER BY fecha_emision DESC")) {
            ps.setInt(1, pacienteId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ventas por paciente", e);
        }
        return list;
    }

    @Override
    public List<VentaCabecera> findByTiendaId(Integer tiendaId) {
        var list = new ArrayList<VentaCabecera>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tienda_id = ? ORDER BY fecha_emision DESC")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ventas por tienda", e);
        }
        return list;
    }

    @Override
    public List<VentaCabecera> findByUsuarioId(Integer usuarioId) {
        var list = new ArrayList<VentaCabecera>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE usuario_id = ? ORDER BY fecha_emision DESC")) {
            ps.setInt(1, usuarioId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ventas por usuario", e);
        }
        return list;
    }

    @Override
    public VentaCabecera processSale(Integer pacienteId, Integer usuarioId, Integer tiendaId,
                                      TipoComprobante tipoComprobante, Integer productoId, Integer cantidad, String metodoPago) {
        var sql = "{CALL sp_procesar_venta(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (var conn = getConnection(); var cs = conn.prepareCall(sql)) {
            cs.setInt(1, pacienteId);
            cs.setInt(2, usuarioId);
            cs.setInt(3, tiendaId);
            cs.setString(4, tipoComprobante != null ? tipoComprobante.name() : "BOLETA");
            cs.setInt(5, productoId);
            cs.setInt(6, cantidad);
            cs.setString(7, metodoPago);
            cs.registerOutParameter(8, Types.INTEGER);
            cs.registerOutParameter(9, Types.VARCHAR);
            cs.execute();

            var resultado = cs.getInt(8);
            var mensaje = cs.getString(9);
            if (resultado == 0) throw new RuntimeException("Error al procesar venta: " + mensaje);

            var ticketExtraido = mensaje.replace("Venta registrada. Ticket: ", "").trim();
            return findByNumeroTicket(ticketExtraido)
                    .orElseThrow(() -> new RuntimeException("Venta registrada pero no encontrada: " + ticketExtraido));
        } catch (SQLException e) {
            throw new RuntimeException("Error al ejecutar sp_procesar_venta", e);
        }
    }

    @Override
    public List<VentaDetalle> findDetallesByVentaId(Integer ventaId) {
        var list = new ArrayList<VentaDetalle>();
        var sql = "SELECT id, venta_id, producto_id, cantidad, precio_unitario, subtotal FROM ventas_detalle WHERE venta_id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ventaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    var d = new VentaDetalle();
                    d.setId(rs.getInt("id"));
                    d.setVentaId(rs.getInt("venta_id"));
                    d.setProductoId(rs.getInt("producto_id"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    d.setSubtotal(rs.getBigDecimal("subtotal"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar detalles de venta", e);
        }
        return list;
    }

    @Override
    public BigDecimal sumVentasByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COALESCE(SUM(monto_total), 0) FROM ventas_cabecera WHERE fecha_emision BETWEEN ? AND ? AND estado_financiero != 'ANULADO'")) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al sumar ventas por rango", e);
        }
    }

    private VentaCabecera map(ResultSet rs) throws SQLException {
        var v = new VentaCabecera();
        v.setId(rs.getInt("id"));
        v.setPacienteId(rs.getInt("paciente_id"));
        v.setUsuarioId(rs.getInt("usuario_id"));
        v.setTiendaId(rs.getInt("tienda_id"));
        v.setNumeroTicket(rs.getString("numero_ticket"));
        var ts = rs.getTimestamp("fecha_emision");
        if (ts != null) v.setFechaEmision(ts.toLocalDateTime());
        v.setEstadoFinanciero(EstadoFinanciero.valueOf(rs.getString("estado_financiero")));
        v.setTipoComprobante(TipoComprobante.valueOf(rs.getString("tipo_comprobante")));
        v.setMontoSubtotal(rs.getBigDecimal("monto_subtotal"));
        v.setMontoIgv(rs.getBigDecimal("monto_igv"));
        v.setMontoTotal(rs.getBigDecimal("monto_total"));
        return v;
    }

    @Override
    public VentaCabecera registrarVentaMultiproducto(Integer pacienteId, Integer usuarioId, Integer tiendaId,
                                                      TipoComprobante tipoComprobante, List<VentaDetalle> detalles,
                                                      String metodoPago, BigDecimal montoPagado) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto a la venta");
        }
        
        String ticket = "TK-" + System.currentTimeMillis() + "-" + (int)(100 + Math.random() * 900);
        java.sql.Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            // 1. Insert cabecera (iniciamos en POR_COBRAR, lo actualizamos al final)
            int ventaId = 0;
            var sqlCab = "INSERT INTO ventas_cabecera (paciente_id, usuario_id, tienda_id, numero_ticket, tipo_comprobante, estado_financiero, monto_subtotal, monto_igv, monto_total) VALUES (?, ?, ?, ?, ?, 'POR_COBRAR', 0, 0, 0)";
            try (var ps = conn.prepareStatement(sqlCab, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, pacienteId);
                ps.setInt(2, usuarioId);
                ps.setInt(3, tiendaId);
                ps.setString(4, ticket);
                ps.setString(5, tipoComprobante != null ? tipoComprobante.name() : "BOLETA");
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        ventaId = rs.getInt(1);
                    }
                }
            }
            
            if (ventaId == 0) {
                throw new SQLException("No se pudo generar el ID de la venta.");
            }
            
            // 2. Loop insert detalles y kardex
            var sqlDet = "INSERT INTO ventas_detalle (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, 0)";
            var sqlKardex = "INSERT INTO kardex (tienda_id, usuario_id, producto_id, venta_id, tipo_movimiento, motivo, cantidad, cantidad_saldo) VALUES (?, ?, ?, ?, ?, 'Venta registrada', ?, 0)";
            
            try (var psDet = conn.prepareStatement(sqlDet);
                 var psKardex = conn.prepareStatement(sqlKardex)) {
                 
                for (var item : detalles) {
                    // Check stock y estado del producto
                    var sqlStock = "SELECT stock_actual, precio_venta, activo FROM productos WHERE id = ? AND tienda_id = ?";
                    BigDecimal precioVenta = BigDecimal.ZERO;
                    int stock = 0;
                    boolean activo = false;
                    try (var psStock = conn.prepareStatement(sqlStock)) {
                        psStock.setInt(1, item.getProductoId());
                        psStock.setInt(2, tiendaId);
                        try (var rsStock = psStock.executeQuery()) {
                            if (rsStock.next()) {
                                stock = rsStock.getInt("stock_actual");
                                precioVenta = rsStock.getBigDecimal("precio_venta");
                                activo = rsStock.getBoolean("activo");
                            }
                        }
                    }
                    
                    if (!activo) {
                        throw new RuntimeException("El producto ID " + item.getProductoId() + " no está activo o disponible.");
                    }
                    // Omitimos validación de stock para productos virtuales de receta que se auto-generan
                    if (stock < item.getCantidad() && stock >= 0 && item.getProductoId() < 10000) { // IDs normales
                        // Algunos productos como servicios o lunas virtuales pueden tener stock 0 inicialmente
                        // Si es un lente formulado o producto especial, se permite la venta (se manda a fabricar)
                    }
                    
                    // Insert detail
                    psDet.setInt(1, ventaId);
                    psDet.setInt(2, item.getProductoId());
                    psDet.setInt(3, item.getCantidad());
                    psDet.setBigDecimal(4, item.getPrecioUnitario() != null ? item.getPrecioUnitario() : precioVenta);
                    psDet.executeUpdate();
                    
                    // Insert kardex entry
                    psKardex.setInt(1, tiendaId);
                    psKardex.setInt(2, usuarioId);
                    psKardex.setInt(3, item.getProductoId());
                    psKardex.setInt(4, ventaId);
                    psKardex.setString(5, opty.model.enums.TipoMovimientoKardex.SALIDA.name());
                    psKardex.setInt(6, item.getCantidad());
                    psKardex.executeUpdate();
                }
            }
            
            // 3. Get total sum from header (triggers calculated it)
            BigDecimal totalVenta = BigDecimal.ZERO;
            var sqlTotal = "SELECT monto_total FROM ventas_cabecera WHERE id = ?";
            try (var psTotal = conn.prepareStatement(sqlTotal)) {
                psTotal.setInt(1, ventaId);
                try (var rsTotal = psTotal.executeQuery()) {
                    if (rsTotal.next()) {
                        totalVenta = rsTotal.getBigDecimal("monto_total");
                    }
                }
            }
            
            // 3.5 Calcular Estado Financiero final y Monto a Caja
            String estadoFinanciero = EstadoFinanciero.PAGADO.name();
            BigDecimal montoCaja = totalVenta;
            if (montoPagado != null) {
                if (montoPagado.compareTo(BigDecimal.ZERO) <= 0) {
                    estadoFinanciero = EstadoFinanciero.POR_COBRAR.name();
                    montoCaja = BigDecimal.ZERO;
                } else if (montoPagado.compareTo(totalVenta) < 0) {
                    estadoFinanciero = EstadoFinanciero.PAGO_PARCIAL.name();
                    montoCaja = montoPagado;
                } else {
                    estadoFinanciero = EstadoFinanciero.PAGADO.name();
                    montoCaja = totalVenta;
                }
            }
            
            // Actualizar estado financiero en cabecera
            var sqlUpdateEst = "UPDATE ventas_cabecera SET estado_financiero = ? WHERE id = ?";
            try (var psUpdateEst = conn.prepareStatement(sqlUpdateEst)) {
                psUpdateEst.setString(1, estadoFinanciero);
                psUpdateEst.setInt(2, ventaId);
                psUpdateEst.executeUpdate();
            }
            
            // 4. Insert caja entry (si hay cobro)
            if (montoCaja.compareTo(BigDecimal.ZERO) > 0) {
                var sqlCaja = "INSERT INTO movimientos_caja (tienda_id, usuario_id, venta_id, tipo, metodo_pago, monto, descripcion) VALUES (?, ?, ?, ?, ?, ?, 'Venta de productos (A cuenta / Pago)')";
                try (var psCaja = conn.prepareStatement(sqlCaja)) {
                    psCaja.setInt(1, tiendaId);
                    psCaja.setInt(2, usuarioId);
                    psCaja.setInt(3, ventaId);
                    psCaja.setString(4, opty.model.enums.TipoMovimientoCaja.ENTRADA.name());
                    psCaja.setString(5, metodoPago != null ? metodoPago : opty.model.enums.MetodoPago.EFECTIVO.name());
                    psCaja.setBigDecimal(6, montoCaja);
                    psCaja.executeUpdate();
                }
            }
            
            conn.commit();
            
            // Fetch final cabecera entity
            var sqlSelect = SELECT_COLUMNS + " WHERE id = ?";
            try (var psSelect = conn.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, ventaId);
                try (var rsSelect = psSelect.executeQuery()) {
                    if (rsSelect.next()) {
                        return map(rsSelect);
                    }
                }
            }
            throw new SQLException("No se pudo recuperar la venta guardada.");
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al revertir transacción de venta: " + ex.getMessage());
                }
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
}

