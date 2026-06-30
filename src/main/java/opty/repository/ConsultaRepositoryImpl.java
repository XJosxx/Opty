package opty.repository;

import opty.model.entity.Consulta;
import opty.model.entity.HistorialClinico;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultaRepositoryImpl extends BaseJdbcRepository implements ConsultaRepository {

    private static final String SELECT_COLUMNS = "SELECT id, paciente_id, usuario_id, tienda_id, venta_detalle_id, fecha, motivo FROM consultas";

    @Override
    public Optional<Consulta> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar consulta por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Consulta> findAll() {
        var list = new ArrayList<Consulta>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY fecha DESC"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar consultas", e);
        }
        return list;
    }

    @Override
    public Consulta save(Consulta entity) {
        var sql = "INSERT INTO consultas (paciente_id, usuario_id, tienda_id, venta_detalle_id, motivo) VALUES (?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getPacienteId());
            ps.setInt(2, entity.getUsuarioId());
            ps.setInt(3, entity.getTiendaId());
            if (entity.getVentaDetalleId() != null) ps.setInt(4, entity.getVentaDetalleId());
            else ps.setNull(4, Types.INTEGER);
            ps.setString(5, entity.getMotivo());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar consulta", e);
        }
        return entity;
    }

    @Override
    public void update(Consulta entity) {
        var sql = "UPDATE consultas SET paciente_id = ?, usuario_id = ?, tienda_id = ?, venta_detalle_id = ?, motivo = ? WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getPacienteId());
            ps.setInt(2, entity.getUsuarioId());
            ps.setInt(3, entity.getTiendaId());
            if (entity.getVentaDetalleId() != null) ps.setInt(4, entity.getVentaDetalleId());
            else ps.setNull(4, Types.INTEGER);
            ps.setString(5, entity.getMotivo());
            ps.setInt(6, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar consulta", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("DELETE FROM consultas WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar consulta", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM consultas WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de consulta", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM consultas"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar consultas", e);
        }
    }

    @Override
    public List<Consulta> findByPacienteId(Integer pacienteId) {
        var list = new ArrayList<Consulta>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE paciente_id = ? ORDER BY fecha DESC")) {
            ps.setInt(1, pacienteId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar consultas por paciente", e);
        }
        return list;
    }

    @Override
    public List<Consulta> findByUsuarioId(Integer usuarioId) {
        var list = new ArrayList<Consulta>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE usuario_id = ? ORDER BY fecha DESC")) {
            ps.setInt(1, usuarioId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar consultas por usuario", e);
        }
        return list;
    }

    @Override
    public List<Consulta> findByTiendaId(Integer tiendaId) {
        var list = new ArrayList<Consulta>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE tienda_id = ? ORDER BY fecha DESC")) {
            ps.setInt(1, tiendaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar consultas por tienda", e);
        }
        return list;
    }

    @Override
    public Consulta registrarConsultaConVenta(Integer pacienteId, Integer usuarioId, Integer tiendaId,
                                                String motivo, Integer productoId, BigDecimal precioServicio,
                                                String tipoComprobante) {
        var sql = "{CALL sp_registrar_paciente_y_consulta(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (var conn = getConnection(); var cs = conn.prepareCall(sql)) {
            cs.setString(1, "DNI");
            cs.setString(2, "TEMP-" + System.currentTimeMillis());
            cs.setString(3, "Paciente");
            cs.setString(4, "Temp");
            cs.setString(5, "Temp");
            cs.setNull(6, Types.VARCHAR);
            cs.setNull(7, Types.DATE);
            cs.setInt(8, usuarioId);
            cs.setInt(9, tiendaId);
            cs.setString(10, motivo);
            cs.setInt(11, productoId);
            cs.setBigDecimal(12, precioServicio);
            cs.setString(13, tipoComprobante);
            cs.registerOutParameter(14, Types.INTEGER);
            cs.registerOutParameter(15, Types.VARCHAR);
            cs.execute();

            var resultado = cs.getInt(14);
            var mensaje = cs.getString(15);
            if (resultado == 0) throw new RuntimeException("Error al registrar consulta: " + mensaje);

            var ticketExtraido = mensaje.replace("Consulta registrada. Ticket: ", "").trim();
            var ventaRepo = new VentaRepositoryImpl();
            var venta = ventaRepo.findByNumeroTicket(ticketExtraido)
                    .orElseThrow(() -> new RuntimeException("Consulta registrada pero ticket no encontrado"));

            var detalles = ventaRepo.findDetallesByVentaId(venta.getId());
            if (!detalles.isEmpty()) {
                return findByVentaDetalleId(detalles.getFirst().getId());
            }
            throw new RuntimeException("No se encontró el detalle de venta asociado a la consulta");
        } catch (SQLException e) {
            throw new RuntimeException("Error al ejecutar sp_registrar_paciente_y_consulta", e);
        }
    }

    private Consulta findByVentaDetalleId(Integer ventaDetalleId) {
        var sql = SELECT_COLUMNS + " WHERE venta_detalle_id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ventaDetalleId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar consulta por detalle de venta", e);
        }
        return null;
    }

    @Override
    public HistorialClinico findHistorialByConsultaId(Integer consultaId) {
        var sql = "SELECT id, consulta_id, graduacion_od, graduacion_oi, observaciones FROM historial_clinico WHERE consulta_id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, consultaId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    var h = new HistorialClinico();
                    h.setId(rs.getInt("id"));
                    h.setConsultaId(rs.getInt("consulta_id"));
                    h.setGraduacionOd(rs.getString("graduacion_od"));
                    h.setGraduacionOi(rs.getString("graduacion_oi"));
                    h.setObservaciones(rs.getString("observaciones"));
                    return h;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar historial clínico", e);
        }
        return null;
    }

    @Override
    public void saveHistorialClinico(HistorialClinico historial) {
        if (historial.getId() != null && historial.getId() > 0) {
            var sql = "UPDATE historial_clinico SET graduacion_od = ?, graduacion_oi = ?, observaciones = ? WHERE id = ?";
            try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
                ps.setString(1, historial.getGraduacionOd());
                ps.setString(2, historial.getGraduacionOi());
                ps.setString(3, historial.getObservaciones());
                ps.setInt(4, historial.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error al actualizar historial clínico", e);
            }
        } else {
            var sql = "INSERT INTO historial_clinico (consulta_id, graduacion_od, graduacion_oi, observaciones) VALUES (?, ?, ?, ?)";
            try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, historial.getConsultaId());
                ps.setString(2, historial.getGraduacionOd());
                ps.setString(3, historial.getGraduacionOi());
                ps.setString(4, historial.getObservaciones());
                ps.executeUpdate();
                try (var keys = ps.getGeneratedKeys()) {
                    if (keys.next()) historial.setId(keys.getInt(1));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al guardar historial clínico", e);
            }
        }
    }

    private Consulta map(ResultSet rs) throws SQLException {
        var c = new Consulta();
        c.setId(rs.getInt("id"));
        c.setPacienteId(rs.getInt("paciente_id"));
        c.setUsuarioId(rs.getInt("usuario_id"));
        c.setTiendaId(rs.getInt("tienda_id"));
        var vdId = rs.getObject("venta_detalle_id");
        if (vdId != null) c.setVentaDetalleId(rs.getInt("venta_detalle_id"));
        var ts = rs.getTimestamp("fecha");
        if (ts != null) c.setFecha(ts.toLocalDateTime());
        c.setMotivo(rs.getString("motivo"));
        return c;
    }
}
