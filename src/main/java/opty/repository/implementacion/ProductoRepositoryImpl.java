package opty.repository.implementacion;

import opty.repository.*;

import opty.model.entity.Producto;
import opty.model.enums.CategoriaProducto;
import opty.model.enums.GeneroObjetivo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoRepositoryImpl extends BaseJdbcRepository implements ProductoRepository {

    private static final String SELECT_COLUMNS = "SELECT id, tienda_id, codigo, nombre, categoria, genero_objetivo, precio_venta, stock_actual, stock_minimo, activo FROM productos";

    @Override
    public Optional<Producto> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Producto> findAll() {
        var list = new ArrayList<Producto>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY nombre"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos", e);
        }
        return list;
    }

    @Override
    public Producto save(Producto entity) {
        var sql = "INSERT INTO productos (tienda_id, codigo, nombre, categoria, genero_objetivo, precio_venta, stock_actual, stock_minimo, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getTiendaId());
            ps.setString(2, entity.getCodigo());
            ps.setString(3, entity.getNombre());
            ps.setString(4, entity.getCategoria().name());
            ps.setString(5, entity.getGeneroObjetivo() != null ? entity.getGeneroObjetivo().name() : null);
            ps.setBigDecimal(6, entity.getPrecioVenta());
            ps.setInt(7, entity.getStockActual());
            ps.setInt(8, entity.getStockMinimo());
            ps.setBoolean(9, entity.getActivo() != null && entity.getActivo());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar producto", e);
        }
        return entity;
    }

    @Override
    public void update(Producto entity) {
        var sql = "UPDATE productos SET tienda_id = ?, codigo = ?, nombre = ?, categoria = ?, genero_objetivo = ?, precio_venta = ?, stock_actual = ?, stock_minimo = ?, activo = ? WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getTiendaId());
            ps.setString(2, entity.getCodigo());
            ps.setString(3, entity.getNombre());
            ps.setString(4, entity.getCategoria().name());
            ps.setString(5, entity.getGeneroObjetivo() != null ? entity.getGeneroObjetivo().name() : null);
            ps.setBigDecimal(6, entity.getPrecioVenta());
            ps.setInt(7, entity.getStockActual());
            ps.setInt(8, entity.getStockMinimo());
            ps.setBoolean(9, entity.getActivo() != null && entity.getActivo());
            ps.setInt(10, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar producto", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("DELETE FROM productos WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar producto", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM productos WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de producto", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM productos"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar productos", e);
        }
    }

    @Override
    public Optional<Producto> findByCodigoAndTienda(String codigo, Integer tiendaId) {
        var sql = SELECT_COLUMNS + " WHERE codigo = ? AND tienda_id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setInt(2, tiendaId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto por cÃ³digo y tienda", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Producto> findByCategoria(CategoriaProducto categoria) {
        var list = new ArrayList<Producto>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE categoria = ?")) {
            ps.setString(1, categoria.name());
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar productos por categorÃ­a", e);
        }
        return list;
    }

    @Override
    public List<Producto> findByTiendaId(Integer tiendaId) {
        var list = new ArrayList<Producto>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tienda_id = ?")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar productos por tienda", e);
        }
        return list;
    }

    @Override
    public List<Producto> findDisponibles(Integer tiendaId) {
        var list = new ArrayList<Producto>();
        var sql = "SELECT p.id, p.tienda_id, p.codigo, p.nombre, p.categoria, p.genero_objetivo, p.precio_venta, p.stock_actual, p.stock_minimo, p.activo FROM productos p WHERE p.activo = TRUE AND p.stock_actual > 0";
        if (tiendaId != null) sql += " AND p.tienda_id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            if (tiendaId != null) ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar productos disponibles", e);
        }
        return list;
    }

    @Override
    public List<Producto> findActivos() {
        var list = new ArrayList<Producto>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE activo = TRUE"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar productos activos", e);
        }
        return list;
    }

    @Override
    public Optional<Producto> findByNombreAndTienda(String nombre, Integer tiendaId) {
        var sql = SELECT_COLUMNS + " WHERE nombre = ? AND tienda_id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, tiendaId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto por nombre y tienda", e);
        }
        return Optional.empty();
    }

    private Producto map(ResultSet rs) throws SQLException {
        var p = new Producto();
        p.setId(rs.getInt("id"));
        p.setTiendaId(rs.getInt("tienda_id"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setCategoria(CategoriaProducto.valueOf(rs.getString("categoria")));
        var gen = rs.getString("genero_objetivo");
        if (gen != null) p.setGeneroObjetivo(GeneroObjetivo.valueOf(gen));
        p.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        p.setStockActual(rs.getInt("stock_actual"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }
}

