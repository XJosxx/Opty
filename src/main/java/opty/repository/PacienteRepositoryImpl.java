package opty.repository;

import opty.model.entity.Paciente;
import opty.model.enums.TipoDestacado;
import opty.model.enums.TipoDocumento;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacienteRepositoryImpl extends BaseJdbcRepository implements PacienteRepository {

    private static final String SELECT_COLUMNS = "SELECT id, nombre, apellido_p, apellido_m, tipo_documento, num_documento, telefono, fecha_nacimiento, es_destacado, tipo_destacado, fecha_registro, tienda_id FROM pacientes";

    @Override
    public Optional<Paciente> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar paciente por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Paciente> findAll() {
        var list = new ArrayList<Paciente>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY apellido_p, apellido_m"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar pacientes", e);
        }
        return list;
    }

    @Override
    public Paciente save(Paciente entity) {
        var sql = "INSERT INTO pacientes (nombre, apellido_p, apellido_m, tipo_documento, num_documento, telefono, fecha_nacimiento, es_destacado, tipo_destacado, tienda_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getApellidoP());
            ps.setString(3, entity.getApellidoM());
            ps.setString(4, entity.getTipoDocumento().name());
            ps.setString(5, entity.getNumDocumento());
            ps.setString(6, entity.getTelefono());
            ps.setObject(7, entity.getFechaNacimiento());
            ps.setBoolean(8, entity.getEsDestacado() != null && entity.getEsDestacado());
            ps.setString(9, entity.getTipoDestacado() != null ? entity.getTipoDestacado().name() : null);
            ps.setInt(10, entity.getTiendaId());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar paciente", e);
        }
        return entity;
    }

    @Override
    public void update(Paciente entity) {
        var sql = "UPDATE pacientes SET nombre = ?, apellido_p = ?, apellido_m = ?, tipo_documento = ?, num_documento = ?, telefono = ?, fecha_nacimiento = ?, es_destacado = ?, tipo_destacado = ?, tienda_id = ? WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getApellidoP());
            ps.setString(3, entity.getApellidoM());
            ps.setString(4, entity.getTipoDocumento().name());
            ps.setString(5, entity.getNumDocumento());
            ps.setString(6, entity.getTelefono());
            ps.setObject(7, entity.getFechaNacimiento());
            ps.setBoolean(8, entity.getEsDestacado() != null && entity.getEsDestacado());
            ps.setString(9, entity.getTipoDestacado() != null ? entity.getTipoDestacado().name() : null);
            ps.setInt(10, entity.getTiendaId());
            ps.setInt(11, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar paciente", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("DELETE FROM pacientes WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar paciente", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM pacientes WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de paciente", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM pacientes"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar pacientes", e);
        }
    }

    @Override
    public Optional<Paciente> findByNumDocumento(String numDocumento) {
        var sql = SELECT_COLUMNS + " WHERE num_documento = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, numDocumento);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar paciente por documento", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Paciente> searchByNombre(String query) {
        var list = new ArrayList<Paciente>();
        var sql = SELECT_COLUMNS + " WHERE CONCAT(nombre, ' ', apellido_p, ' ', apellido_m) LIKE ? OR num_documento LIKE ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            var like = "%" + query.trim() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar pacientes", e);
        }
        return list;
    }

    @Override
    public List<Paciente> findByTiendaId(Integer tiendaId) {
        var list = new ArrayList<Paciente>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tienda_id = ?")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar pacientes por tienda", e);
        }
        return list;
    }

    @Override
    public List<Paciente> findDestacados() {
        var list = new ArrayList<Paciente>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE es_destacado = TRUE"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar pacientes destacados", e);
        }
        return list;
    }

    @Override
    public long countByTiendaId(Integer tiendaId) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM pacientes WHERE tienda_id = ?")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar pacientes por tienda", e);
        }
    }

    private Paciente map(ResultSet rs) throws SQLException {
        var p = new Paciente();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setApellidoP(rs.getString("apellido_p"));
        p.setApellidoM(rs.getString("apellido_m"));
        p.setTipoDocumento(TipoDocumento.valueOf(rs.getString("tipo_documento")));
        p.setNumDocumento(rs.getString("num_documento"));
        p.setTelefono(rs.getString("telefono"));
        var sqlDate = rs.getDate("fecha_nacimiento");
        if (sqlDate != null) p.setFechaNacimiento(sqlDate.toLocalDate());
        p.setEsDestacado(rs.getBoolean("es_destacado"));
        var td = rs.getString("tipo_destacado");
        if (td != null) p.setTipoDestacado(TipoDestacado.valueOf(td));
        var ts = rs.getTimestamp("fecha_registro");
        if (ts != null) p.setFechaRegistro(ts.toLocalDateTime());
        p.setTiendaId(rs.getInt("tienda_id"));
        return p;
    }
}
