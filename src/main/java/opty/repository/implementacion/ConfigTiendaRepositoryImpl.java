package opty.repository.implementacion;

import opty.repository.*;

import opty.model.entity.ConfigTienda;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigTiendaRepositoryImpl extends BaseJdbcRepository implements ConfigTiendaRepository {

    @Override
    public Optional<ConfigTienda> findById(Integer id) {
        var sql = "SELECT id, codigo, nombre_optica, ruc, direccion, telefono FROM config_tienda WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tienda por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ConfigTienda> findAll() {
        var sql = "SELECT id, codigo, nombre_optica, ruc, direccion, telefono FROM config_tienda ORDER BY codigo";
        var list = new ArrayList<ConfigTienda>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar tiendas", e);
        }
        return list;
    }

    @Override
    public ConfigTienda save(ConfigTienda entity) {
        var sql = "INSERT INTO config_tienda (codigo, nombre_optica, ruc, direccion, telefono) VALUES (?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getCodigo());
            ps.setString(2, entity.getNombreOptica());
            ps.setString(3, entity.getRuc());
            ps.setString(4, entity.getDireccion());
            ps.setString(5, entity.getTelefono());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar tienda", e);
        }
        return entity;
    }

    @Override
    public void update(ConfigTienda entity) {
        var sql = "UPDATE config_tienda SET codigo = ?, nombre_optica = ?, ruc = ?, direccion = ?, telefono = ? WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getCodigo());
            ps.setString(2, entity.getNombreOptica());
            ps.setString(3, entity.getRuc());
            ps.setString(4, entity.getDireccion());
            ps.setString(5, entity.getTelefono());
            ps.setInt(6, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar tienda", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("DELETE FROM config_tienda WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar tienda", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM config_tienda WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de tienda", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM config_tienda"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar tiendas", e);
        }
    }

    @Override
    public Optional<ConfigTienda> findByCodigo(String codigo) {
        var sql = "SELECT id, codigo, nombre_optica, ruc, direccion, telefono FROM config_tienda WHERE codigo = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tienda por cÃ³digo", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ConfigTienda> findByRuc(String ruc) {
        var list = new ArrayList<ConfigTienda>();
        var sql = "SELECT id, codigo, nombre_optica, ruc, direccion, telefono FROM config_tienda WHERE ruc = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, ruc);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tiendas por RUC", e);
        }
        return list;
    }

    private ConfigTienda map(ResultSet rs) throws SQLException {
        return new ConfigTienda(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre_optica"),
                rs.getString("ruc"),
                rs.getString("direccion"),
                rs.getString("telefono")
        );
    }
}

