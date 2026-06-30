package opty.repository;

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
                                      Integer productoId, Integer cantidad, String metodoPago) {
        var sql = "{CALL sp_procesar_venta(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (var conn = getConnection(); var cs = conn.prepareCall(sql)) {
            cs.setInt(1, pacienteId);
            cs.setInt(2, usuarioId);
            cs.setInt(3, tiendaId);
            cs.setString(4, "BOLETA");
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
}
