package opty.repository.implementacion;

import opty.repository.*;

import opty.model.entity.MovimientoCaja;
import opty.model.enums.MetodoPago;
import opty.model.enums.TipoMovimientoCaja;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovimientoCajaRepositoryImpl extends BaseJdbcRepository implements MovimientoCajaRepository {

    private static final String SELECT_COLUMNS = "SELECT id, tienda_id, usuario_id, venta_id, compra_id, tipo, metodo_pago, monto, descripcion, fecha FROM movimientos_caja";

    @Override
    public Optional<MovimientoCaja> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar movimiento de caja por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<MovimientoCaja> findAll() {
        var list = new ArrayList<MovimientoCaja>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY fecha DESC"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar movimientos de caja", e);
        }
        return list;
    }

    @Override
    public MovimientoCaja save(MovimientoCaja entity) {
        var sql = "INSERT INTO movimientos_caja (tienda_id, usuario_id, venta_id, compra_id, tipo, metodo_pago, monto, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getTiendaId());
            ps.setInt(2, entity.getUsuarioId());
            if (entity.getVentaId() != null) ps.setInt(3, entity.getVentaId());
            else ps.setNull(3, java.sql.Types.INTEGER);
            if (entity.getCompraId() != null) ps.setInt(4, entity.getCompraId());
            else ps.setNull(4, java.sql.Types.INTEGER);
            ps.setString(5, entity.getTipo().name());
            ps.setString(6, entity.getMetodoPago().name());
            ps.setBigDecimal(7, entity.getMonto());
            ps.setString(8, entity.getDescripcion());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar movimiento de caja", e);
        }
        return entity;
    }

    @Override
    public void update(MovimientoCaja entity) {
        throw new UnsupportedOperationException("No se puede modificar un movimiento de caja existente");
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("No se puede eliminar un movimiento de caja");
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM movimientos_caja WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de movimiento de caja", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM movimientos_caja"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar movimientos de caja", e);
        }
    }

    @Override
    public List<MovimientoCaja> findByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        var list = new ArrayList<MovimientoCaja>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC")) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar movimientos por rango de fechas", e);
        }
        return list;
    }

    @Override
    public List<MovimientoCaja> findByTiendaId(Integer tiendaId) {
        var list = new ArrayList<MovimientoCaja>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tienda_id = ? ORDER BY fecha DESC")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar movimientos por tienda", e);
        }
        return list;
    }

    @Override
    public List<MovimientoCaja> findByVentaId(Integer ventaId) {
        var list = new ArrayList<MovimientoCaja>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE venta_id = ?")) {
            ps.setInt(1, ventaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar movimientos por venta", e);
        }
        return list;
    }

    @Override
    public List<MovimientoCaja> findByCompraId(Integer compraId) {
        var list = new ArrayList<MovimientoCaja>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE compra_id = ?")) {
            ps.setInt(1, compraId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar movimientos por compra", e);
        }
        return list;
    }

    @Override
    public BigDecimal sumIngresosByDateRange(LocalDateTime desde, LocalDateTime hasta, Integer tiendaId) {
        var sql = "SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE tipo = 'ENTRADA' AND fecha BETWEEN ? AND ?";
        if (tiendaId != null) sql += " AND tienda_id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            if (tiendaId != null) ps.setInt(3, tiendaId);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al sumar ingresos", e);
        }
    }

    @Override
    public BigDecimal sumEgresosByDateRange(LocalDateTime desde, LocalDateTime hasta, Integer tiendaId) {
        var sql = "SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE tipo = 'SALIDA' AND fecha BETWEEN ? AND ?";
        if (tiendaId != null) sql += " AND tienda_id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            if (tiendaId != null) ps.setInt(3, tiendaId);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al sumar egresos", e);
        }
    }

    private MovimientoCaja map(ResultSet rs) throws SQLException {
        var m = new MovimientoCaja();
        m.setId(rs.getInt("id"));
        m.setTiendaId(rs.getInt("tienda_id"));
        m.setUsuarioId(rs.getInt("usuario_id"));
        var ventId = rs.getObject("venta_id");
        if (ventId != null) m.setVentaId(rs.getInt("venta_id"));
        var compId = rs.getObject("compra_id");
        if (compId != null) m.setCompraId(rs.getInt("compra_id"));
        m.setTipo(TipoMovimientoCaja.valueOf(rs.getString("tipo")));
        m.setMetodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")));
        m.setMonto(rs.getBigDecimal("monto"));
        m.setDescripcion(rs.getString("descripcion"));
        var ts = rs.getTimestamp("fecha");
        if (ts != null) m.setFecha(ts.toLocalDateTime());
        return m;
    }
}

