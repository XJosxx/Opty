package opty.repository;

import opty.model.entity.Proveedor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProveedorRepositoryImpl extends BaseJdbcRepository implements ProveedorRepository {

    private static final String SELECT_COLUMNS = "SELECT id, nombre_empresa, nombre_contacto, telefono, email, ruc, activo, fecha_registro, tienda_id FROM proveedores";

    @Override
    public Optional<Proveedor> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar proveedor por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Proveedor> findAll() {
        var list = new ArrayList<Proveedor>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY nombre_empresa"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar proveedores", e);
        }
        return list;
    }

    @Override
    public Proveedor save(Proveedor entity) {
        var sql = "INSERT INTO proveedores (nombre_empresa, nombre_contacto, telefono, email, ruc, activo, tienda_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNombreEmpresa());
            ps.setString(2, entity.getNombreContacto());
            ps.setString(3, entity.getTelefono());
            ps.setString(4, entity.getEmail());
            ps.setString(5, entity.getRuc());
            ps.setBoolean(6, entity.getActivo() != null && entity.getActivo());
            ps.setInt(7, entity.getTiendaId());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar proveedor", e);
        }
        return entity;
    }

    @Override
    public void update(Proveedor entity) {
        var sql = "UPDATE proveedores SET nombre_empresa = ?, nombre_contacto = ?, telefono = ?, email = ?, ruc = ?, activo = ?, tienda_id = ? WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getNombreEmpresa());
            ps.setString(2, entity.getNombreContacto());
            ps.setString(3, entity.getTelefono());
            ps.setString(4, entity.getEmail());
            ps.setString(5, entity.getRuc());
            ps.setBoolean(6, entity.getActivo() != null && entity.getActivo());
            ps.setInt(7, entity.getTiendaId());
            ps.setInt(8, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar proveedor", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("DELETE FROM proveedores WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar proveedor", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM proveedores WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de proveedor", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM proveedores"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar proveedores", e);
        }
    }

    @Override
    public Optional<Proveedor> findByRuc(String ruc) {
        var sql = SELECT_COLUMNS + " WHERE ruc = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, ruc);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar proveedor por RUC", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Proveedor> findActivos() {
        var list = new ArrayList<Proveedor>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE activo = TRUE"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar proveedores activos", e);
        }
        return list;
    }

    @Override
    public List<Proveedor> findByTiendaId(Integer tiendaId) {
        var list = new ArrayList<Proveedor>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tienda_id = ?")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar proveedores por tienda", e);
        }
        return list;
    }

    private Proveedor map(ResultSet rs) throws SQLException {
        var p = new Proveedor();
        p.setId(rs.getInt("id"));
        p.setNombreEmpresa(rs.getString("nombre_empresa"));
        p.setNombreContacto(rs.getString("nombre_contacto"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setRuc(rs.getString("ruc"));
        p.setActivo(rs.getBoolean("activo"));
        var ts = rs.getTimestamp("fecha_registro");
        if (ts != null) p.setFechaRegistro(ts.toLocalDateTime());
        p.setTiendaId(rs.getInt("tienda_id"));
        return p;
    }
}
