package opty.repository.implementacion;

import opty.repository.*;

import opty.model.entity.Usuario;
import opty.model.enums.Rol;
import opty.model.enums.TipoDocumento;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepositoryImpl extends BaseJdbcRepository implements UsuarioRepository {

    private static final String SELECT_COLUMNS = "SELECT id, username, password, nombre, apellido_p, apellido_m, tipo_documento, num_documento, rol, activo, ultimo_acceso, tienda_id FROM usuarios";

    @Override
    public Optional<Usuario> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> findAll() {
        var list = new ArrayList<Usuario>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY apellido_p, apellido_m"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios", e);
        }
        return list;
    }

    @Override
    public Usuario save(Usuario entity) {
        var sql = "INSERT INTO usuarios (username, password, nombre, apellido_p, apellido_m, tipo_documento, num_documento, rol, activo, tienda_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getPassword());
            ps.setString(3, entity.getNombre());
            ps.setString(4, entity.getApellidoP());
            ps.setString(5, entity.getApellidoM());
            ps.setString(6, entity.getTipoDocumento().name());
            ps.setString(7, entity.getNumDocumento());
            ps.setString(8, entity.getRol().name());
            ps.setBoolean(9, entity.getActivo() != null && entity.getActivo());
            ps.setInt(10, entity.getTiendaId());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar usuario", e);
        }
        return entity;
    }

    @Override
    public void update(Usuario entity) {
        var sql = "UPDATE usuarios SET username = ?, password = ?, nombre = ?, apellido_p = ?, apellido_m = ?, tipo_documento = ?, num_documento = ?, rol = ?, activo = ?, tienda_id = ? WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getPassword());
            ps.setString(3, entity.getNombre());
            ps.setString(4, entity.getApellidoP());
            ps.setString(5, entity.getApellidoM());
            ps.setString(6, entity.getTipoDocumento().name());
            ps.setString(7, entity.getNumDocumento());
            ps.setString(8, entity.getRol().name());
            ps.setBoolean(9, entity.getActivo() != null && entity.getActivo());
            ps.setInt(10, entity.getTiendaId());
            ps.setInt(11, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("DELETE FROM usuarios WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM usuarios WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de usuario", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM usuarios"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar usuarios", e);
        }
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        var sql = SELECT_COLUMNS + " WHERE username = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por username", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> findByRol(Rol rol) {
        var list = new ArrayList<Usuario>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE rol = ?")) {
            ps.setString(1, rol.name());
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios por rol", e);
        }
        return list;
    }

    @Override
    public List<Usuario> findByTiendaId(Integer tiendaId) {
        var list = new ArrayList<Usuario>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tienda_id = ?")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios por tienda", e);
        }
        return list;
    }

    @Override
    public Optional<Usuario> findByNumDocumento(String numDocumento) {
        var sql = SELECT_COLUMNS + " WHERE num_documento = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDocumento);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por documento", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> findActivos() {
        var list = new ArrayList<Usuario>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE activo = TRUE"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios activos", e);
        }
        return list;
    }

    @Override
    public void actualizarUltimoAcceso(Integer usuarioId) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("UPDATE usuarios SET ultimo_acceso = ? WHERE id = ?")) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar Ãºltimo acceso", e);
        }
    }

    private Usuario map(ResultSet rs) throws SQLException {
        var u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNombre(rs.getString("nombre"));
        u.setApellidoP(rs.getString("apellido_p"));
        u.setApellidoM(rs.getString("apellido_m"));
        u.setTipoDocumento(TipoDocumento.valueOf(rs.getString("tipo_documento")));
        u.setNumDocumento(rs.getString("num_documento"));
        u.setRol(Rol.valueOf(rs.getString("rol")));
        u.setActivo(rs.getBoolean("activo"));
        var ts = rs.getTimestamp("ultimo_acceso");
        if (ts != null) u.setUltimoAcceso(ts.toLocalDateTime());
        u.setTiendaId(rs.getInt("tienda_id"));
        return u;
    }
}

