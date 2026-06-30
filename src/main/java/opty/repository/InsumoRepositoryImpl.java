package opty.repository;

import opty.model.entity.Insumo;
import opty.model.enums.CategoriaInsumo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InsumoRepositoryImpl extends BaseJdbcRepository implements InsumoRepository {

    private static final String SELECT_COLUMNS = "SELECT id, tienda_id, codigo, nombre, categoria, material, precio_costo, stock_actual, stock_minimo, activo FROM insumos";

    @Override
    public Optional<Insumo> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar insumo por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Insumo> findAll() {
        var list = new ArrayList<Insumo>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY nombre"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar insumos", e);
        }
        return list;
    }

    @Override
    public Insumo save(Insumo entity) {
        var sql = "INSERT INTO insumos (tienda_id, codigo, nombre, categoria, material, precio_costo, stock_actual, stock_minimo, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getTiendaId());
            ps.setString(2, entity.getCodigo());
            ps.setString(3, entity.getNombre());
            ps.setString(4, entity.getCategoria().name());
            ps.setString(5, entity.getMaterial());
            ps.setBigDecimal(6, entity.getPrecioCosto());
            ps.setInt(7, entity.getStockActual());
            ps.setInt(8, entity.getStockMinimo());
            ps.setBoolean(9, entity.getActivo() != null && entity.getActivo());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar insumo", e);
        }
        return entity;
    }

    @Override
    public void update(Insumo entity) {
        var sql = "UPDATE insumos SET tienda_id = ?, codigo = ?, nombre = ?, categoria = ?, material = ?, precio_costo = ?, stock_actual = ?, stock_minimo = ?, activo = ? WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getTiendaId());
            ps.setString(2, entity.getCodigo());
            ps.setString(3, entity.getNombre());
            ps.setString(4, entity.getCategoria().name());
            ps.setString(5, entity.getMaterial());
            ps.setBigDecimal(6, entity.getPrecioCosto());
            ps.setInt(7, entity.getStockActual());
            ps.setInt(8, entity.getStockMinimo());
            ps.setBoolean(9, entity.getActivo() != null && entity.getActivo());
            ps.setInt(10, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar insumo", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("DELETE FROM insumos WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar insumo", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM insumos WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de insumo", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM insumos"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar insumos", e);
        }
    }

    @Override
    public Optional<Insumo> findByCodigoAndTienda(String codigo, Integer tiendaId) {
        var sql = SELECT_COLUMNS + " WHERE codigo = ? AND tienda_id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setInt(2, tiendaId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar insumo por código y tienda", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Insumo> findByCategoria(CategoriaInsumo categoria) {
        var list = new ArrayList<Insumo>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE categoria = ?")) {
            ps.setString(1, categoria.name());
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar insumos por categoría", e);
        }
        return list;
    }

    @Override
    public List<Insumo> findByTiendaId(Integer tiendaId) {
        var list = new ArrayList<Insumo>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tienda_id = ?")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar insumos por tienda", e);
        }
        return list;
    }

    @Override
    public List<Insumo> findBajoStock(Integer tiendaId) {
        var list = new ArrayList<Insumo>();
        var sql = "SELECT i.id, i.tienda_id, i.codigo, i.nombre, i.categoria, i.material, i.precio_costo, i.stock_actual, i.stock_minimo, i.activo FROM insumos i WHERE i.activo = TRUE AND i.stock_actual < i.stock_minimo";
        if (tiendaId != null) sql += " AND i.tienda_id = ?";
        sql += " ORDER BY (i.stock_minimo - i.stock_actual) DESC";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            if (tiendaId != null) ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar insumos bajo stock", e);
        }
        return list;
    }

    @Override
    public List<Insumo> findActivos() {
        var list = new ArrayList<Insumo>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE activo = TRUE"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar insumos activos", e);
        }
        return list;
    }

    private Insumo map(ResultSet rs) throws SQLException {
        var i = new Insumo();
        i.setId(rs.getInt("id"));
        i.setTiendaId(rs.getInt("tienda_id"));
        i.setCodigo(rs.getString("codigo"));
        i.setNombre(rs.getString("nombre"));
        i.setCategoria(CategoriaInsumo.valueOf(rs.getString("categoria")));
        i.setMaterial(rs.getString("material"));
        i.setPrecioCosto(rs.getBigDecimal("precio_costo"));
        i.setStockActual(rs.getInt("stock_actual"));
        i.setStockMinimo(rs.getInt("stock_minimo"));
        i.setActivo(rs.getBoolean("activo"));
        return i;
    }
}
