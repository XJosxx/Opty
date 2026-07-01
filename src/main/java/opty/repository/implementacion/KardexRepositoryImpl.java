package opty.repository.implementacion;

import opty.repository.*;

import opty.model.entity.Kardex;
import opty.model.enums.TipoMovimientoKardex;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KardexRepositoryImpl extends BaseJdbcRepository implements KardexRepository {

    private static final String SELECT_COLUMNS = "SELECT id, tienda_id, usuario_id, insumo_id, producto_id, compra_id, venta_id, tipo_movimiento, motivo, cantidad, cantidad_saldo, fecha FROM kardex";

    @Override
    public Optional<Kardex> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar kardex por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Kardex> findAll() {
        var list = new ArrayList<Kardex>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY fecha DESC"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar kardex", e);
        }
        return list;
    }

    @Override
    public Kardex save(Kardex entity) {
        var sql = "INSERT INTO kardex (tienda_id, usuario_id, insumo_id, producto_id, compra_id, venta_id, tipo_movimiento, motivo, cantidad, cantidad_saldo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getTiendaId());
            ps.setInt(2, entity.getUsuarioId());
            if (entity.getInsumoId() != null) ps.setInt(3, entity.getInsumoId());
            else ps.setNull(3, java.sql.Types.INTEGER);
            if (entity.getProductoId() != null) ps.setInt(4, entity.getProductoId());
            else ps.setNull(4, java.sql.Types.INTEGER);
            if (entity.getCompraId() != null) ps.setInt(5, entity.getCompraId());
            else ps.setNull(5, java.sql.Types.INTEGER);
            if (entity.getVentaId() != null) ps.setInt(6, entity.getVentaId());
            else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setString(7, entity.getTipoMovimiento().name());
            ps.setString(8, entity.getMotivo());
            ps.setInt(9, entity.getCantidad());
            ps.setInt(10, entity.getCantidadSaldo());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar kardex", e);
        }
        return entity;
    }

    @Override
    public void update(Kardex entity) {
        throw new UnsupportedOperationException("No se puede actualizar un registro de kardex");
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("No se puede eliminar un registro de kardex");
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM kardex WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de kardex", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM kardex"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar kardex", e);
        }
    }

    @Override
    public List<Kardex> findByProductoId(Integer productoId) {
        var list = new ArrayList<Kardex>();
        var sql = SELECT_COLUMNS + " WHERE producto_id = ? ORDER BY fecha ASC";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar kardex por producto", e);
        }
        return list;
    }

    @Override
    public List<Kardex> findByInsumoId(Integer insumoId) {
        var list = new ArrayList<Kardex>();
        var sql = SELECT_COLUMNS + " WHERE insumo_id = ? ORDER BY fecha ASC";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, insumoId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar kardex por insumo", e);
        }
        return list;
    }

    @Override
    public List<Kardex> findByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        var list = new ArrayList<Kardex>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE fecha BETWEEN ? AND ? ORDER BY fecha ASC")) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar kardex por rango de fechas", e);
        }
        return list;
    }

    @Override
    public List<Kardex> findByTiendaId(Integer tiendaId) {
        var list = new ArrayList<Kardex>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tienda_id = ? ORDER BY fecha DESC")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar kardex por tienda", e);
        }
        return list;
    }

    @Override
    public List<Kardex> findByTipoMovimiento(String tipoMovimiento) {
        var list = new ArrayList<Kardex>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tipo_movimiento = ? ORDER BY fecha DESC")) {
            ps.setString(1, tipoMovimiento);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar kardex por tipo de movimiento", e);
        }
        return list;
    }

    private Kardex map(ResultSet rs) throws SQLException {
        var k = new Kardex();
        k.setId(rs.getInt("id"));
        k.setTiendaId(rs.getInt("tienda_id"));
        k.setUsuarioId(rs.getInt("usuario_id"));
        var insId = rs.getObject("insumo_id");
        if (insId != null) k.setInsumoId(rs.getInt("insumo_id"));
        var prodId = rs.getObject("producto_id");
        if (prodId != null) k.setProductoId(rs.getInt("producto_id"));
        var compId = rs.getObject("compra_id");
        if (compId != null) k.setCompraId(rs.getInt("compra_id"));
        var ventId = rs.getObject("venta_id");
        if (ventId != null) k.setVentaId(rs.getInt("venta_id"));
        k.setTipoMovimiento(TipoMovimientoKardex.valueOf(rs.getString("tipo_movimiento")));
        k.setMotivo(rs.getString("motivo"));
        k.setCantidad(rs.getInt("cantidad"));
        k.setCantidadSaldo(rs.getInt("cantidad_saldo"));
        var ts = rs.getTimestamp("fecha");
        if (ts != null) k.setFecha(ts.toLocalDateTime());
        return k;
    }
}

