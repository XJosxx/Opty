package opty.repository;

import opty.model.entity.CompraCabecera;
import opty.model.entity.CompraDetalle;
import opty.model.enums.EstadoFisicoCompra;
import opty.model.enums.EstadoFinanciero;

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

public class CompraRepositoryImpl extends BaseJdbcRepository implements CompraRepository {

    private static final String SELECT_COLUMNS = "SELECT id, proveedor_id, usuario_id, tienda_id, numero_orden, fecha_emision, estado_fisico, estado_financiero, monto_total FROM compras_cabecera";

    @Override
    public Optional<CompraCabecera> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar compra por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<CompraCabecera> findAll() {
        var list = new ArrayList<CompraCabecera>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY fecha_emision DESC"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar compras", e);
        }
        return list;
    }

    @Override
    public CompraCabecera save(CompraCabecera entity) {
        var sql = "INSERT INTO compras_cabecera (proveedor_id, usuario_id, tienda_id, numero_orden, estado_fisico, estado_financiero, monto_total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getProveedorId());
            ps.setInt(2, entity.getUsuarioId());
            ps.setInt(3, entity.getTiendaId());
            ps.setString(4, entity.getNumeroOrden());
            ps.setString(5, entity.getEstadoFisico().name());
            ps.setString(6, entity.getEstadoFinanciero().name());
            ps.setBigDecimal(7, entity.getMontoTotal());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar compra", e);
        }
        return entity;
    }

    @Override
    public void update(CompraCabecera entity) {
        var sql = "UPDATE compras_cabecera SET proveedor_id = ?, usuario_id = ?, tienda_id = ?, numero_orden = ?, estado_fisico = ?, estado_financiero = ?, monto_total = ? WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getProveedorId());
            ps.setInt(2, entity.getUsuarioId());
            ps.setInt(3, entity.getTiendaId());
            ps.setString(4, entity.getNumeroOrden());
            ps.setString(5, entity.getEstadoFisico().name());
            ps.setString(6, entity.getEstadoFinanciero().name());
            ps.setBigDecimal(7, entity.getMontoTotal());
            ps.setInt(8, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar compra", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("DELETE FROM compras_cabecera WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar compra", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM compras_cabecera WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de compra", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM compras_cabecera"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar compras", e);
        }
    }

    @Override
    public Optional<CompraCabecera> findByNumeroOrden(String numeroOrden) {
        var sql = SELECT_COLUMNS + " WHERE numero_orden = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, numeroOrden);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar compra por orden", e);
        }
        return Optional.empty();
    }

    @Override
    public List<CompraCabecera> findByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        var list = new ArrayList<CompraCabecera>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE fecha_emision BETWEEN ? AND ? ORDER BY fecha_emision DESC")) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar compras por rango de fechas", e);
        }
        return list;
    }

    @Override
    public List<CompraCabecera> findByProveedorId(Integer proveedorId) {
        var list = new ArrayList<CompraCabecera>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE proveedor_id = ? ORDER BY fecha_emision DESC")) {
            ps.setInt(1, proveedorId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar compras por proveedor", e);
        }
        return list;
    }

    @Override
    public List<CompraCabecera> findByTiendaId(Integer tiendaId) {
        var list = new ArrayList<CompraCabecera>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tienda_id = ? ORDER BY fecha_emision DESC")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar compras por tienda", e);
        }
        return list;
    }

    @Override
    public CompraCabecera processPurchase(Integer proveedorId, Integer usuarioId, Integer tiendaId,
                                            Integer insumoId, Integer cantidad, BigDecimal precioUnit,
                                            String metodoPago) {
        var sql = "{CALL sp_registrar_compra(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (var conn = getConnection(); var cs = conn.prepareCall(sql)) {
            cs.setInt(1, proveedorId);
            cs.setInt(2, usuarioId);
            cs.setInt(3, tiendaId);
            cs.setInt(4, insumoId);
            cs.setInt(5, cantidad);
            cs.setBigDecimal(6, precioUnit);
            cs.setString(7, metodoPago);
            cs.registerOutParameter(8, Types.INTEGER);
            cs.registerOutParameter(9, Types.VARCHAR);
            cs.execute();

            var resultado = cs.getInt(8);
            var mensaje = cs.getString(9);
            if (resultado == 0) throw new RuntimeException("Error al registrar compra: " + mensaje);

            var ordenExtraida = mensaje.replace("Compra registrada. Orden: ", "").trim();
            return findByNumeroOrden(ordenExtraida)
                    .orElseThrow(() -> new RuntimeException("Compra registrada pero no encontrada: " + ordenExtraida));
        } catch (SQLException e) {
            throw new RuntimeException("Error al ejecutar sp_registrar_compra", e);
        }
    }

    @Override
    public List<CompraDetalle> findDetallesByCompraId(Integer compraId) {
        var list = new ArrayList<CompraDetalle>();
        var sql = "SELECT id, compra_id, insumo_id, cantidad, precio_unitario, subtotal FROM compras_detalle WHERE compra_id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, compraId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    var d = new CompraDetalle();
                    d.setId(rs.getInt("id"));
                    d.setCompraId(rs.getInt("compra_id"));
                    d.setInsumoId(rs.getInt("insumo_id"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    d.setSubtotal(rs.getBigDecimal("subtotal"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar detalles de compra", e);
        }
        return list;
    }

    @Override
    public BigDecimal sumComprasByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COALESCE(SUM(monto_total), 0) FROM compras_cabecera WHERE fecha_emision BETWEEN ? AND ? AND estado_financiero != 'ANULADO'")) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al sumar compras por rango", e);
        }
    }

    private CompraCabecera map(ResultSet rs) throws SQLException {
        var c = new CompraCabecera();
        c.setId(rs.getInt("id"));
        c.setProveedorId(rs.getInt("proveedor_id"));
        c.setUsuarioId(rs.getInt("usuario_id"));
        c.setTiendaId(rs.getInt("tienda_id"));
        c.setNumeroOrden(rs.getString("numero_orden"));
        var ts = rs.getTimestamp("fecha_emision");
        if (ts != null) c.setFechaEmision(ts.toLocalDateTime());
        c.setEstadoFisico(EstadoFisicoCompra.valueOf(rs.getString("estado_fisico")));
        c.setEstadoFinanciero(EstadoFinanciero.valueOf(rs.getString("estado_financiero")));
        c.setMontoTotal(rs.getBigDecimal("monto_total"));
        return c;
    }
}
