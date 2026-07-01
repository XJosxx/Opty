package opty.repository.implementacion;

import opty.repository.*;

import opty.model.entity.OrdenTrabajo;
import opty.model.enums.EstadoFisicoOT;
import opty.model.enums.TipoTrabajo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdenTrabajoRepositoryImpl extends BaseJdbcRepository implements OrdenTrabajoRepository {

    private static final String SELECT_COLUMNS = "SELECT id, venta_id, historial_clinico_id, estado_fisico, tipo_trabajo, usa_montura_cliente, detalles_montura_cliente, usa_luna_cliente, detalles_luna_cliente, fecha_creacion, fecha_prometida FROM ordenes_trabajo";

    @Override
    public Optional<OrdenTrabajo> findById(Integer id) {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar orden de trabajo por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<OrdenTrabajo> findAll() {
        var list = new ArrayList<OrdenTrabajo>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " ORDER BY fecha_creacion DESC"); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar Ã³rdenes de trabajo", e);
        }
        return list;
    }

    @Override
    public OrdenTrabajo save(OrdenTrabajo entity) {
        var sql = "INSERT INTO ordenes_trabajo (venta_id, historial_clinico_id, estado_fisico, tipo_trabajo, usa_montura_cliente, detalles_montura_cliente, usa_luna_cliente, detalles_luna_cliente, fecha_prometida) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getVentaId());
            if (entity.getHistorialClinicoId() != null) ps.setInt(2, entity.getHistorialClinicoId());
            else ps.setNull(2, java.sql.Types.INTEGER);
            ps.setString(3, entity.getEstadoFisico().name());
            ps.setString(4, entity.getTipoTrabajo().name());
            ps.setBoolean(5, entity.getUsaMonturaCliente() != null && entity.getUsaMonturaCliente());
            ps.setString(6, entity.getDetallesMonturaCliente());
            ps.setBoolean(7, entity.getUsaLunaCliente() != null && entity.getUsaLunaCliente());
            ps.setString(8, entity.getDetallesLunaCliente());
            if (entity.getFechaPrometida() != null) ps.setTimestamp(9, Timestamp.valueOf(entity.getFechaPrometida()));
            else ps.setNull(9, java.sql.Types.TIMESTAMP);
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) entity.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar orden de trabajo", e);
        }
        return entity;
    }

    @Override
    public void update(OrdenTrabajo entity) {
        var sql = "UPDATE ordenes_trabajo SET venta_id = ?, historial_clinico_id = ?, estado_fisico = ?, tipo_trabajo = ?, usa_montura_cliente = ?, detalles_montura_cliente = ?, usa_luna_cliente = ?, detalles_luna_cliente = ?, fecha_prometida = ? WHERE id = ?";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entity.getVentaId());
            if (entity.getHistorialClinicoId() != null) ps.setInt(2, entity.getHistorialClinicoId());
            else ps.setNull(2, java.sql.Types.INTEGER);
            ps.setString(3, entity.getEstadoFisico().name());
            ps.setString(4, entity.getTipoTrabajo().name());
            ps.setBoolean(5, entity.getUsaMonturaCliente() != null && entity.getUsaMonturaCliente());
            ps.setString(6, entity.getDetallesMonturaCliente());
            ps.setBoolean(7, entity.getUsaLunaCliente() != null && entity.getUsaLunaCliente());
            ps.setString(8, entity.getDetallesLunaCliente());
            if (entity.getFechaPrometida() != null) ps.setTimestamp(9, Timestamp.valueOf(entity.getFechaPrometida()));
            else ps.setNull(9, java.sql.Types.TIMESTAMP);
            ps.setInt(10, entity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar orden de trabajo", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("DELETE FROM ordenes_trabajo WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar orden de trabajo", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT 1 FROM ordenes_trabajo WHERE id = ?")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de orden de trabajo", e);
        }
    }

    @Override
    public long count() {
        try (var conn = getConnection(); var ps = conn.prepareStatement("SELECT COUNT(*) FROM ordenes_trabajo"); var rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar Ã³rdenes de trabajo", e);
        }
    }

    @Override
    public List<OrdenTrabajo> findPendientes() {
        var list = new ArrayList<OrdenTrabajo>();
        var sql = "SELECT ot.id, ot.venta_id, ot.historial_clinico_id, ot.estado_fisico, ot.tipo_trabajo, ot.usa_montura_cliente, ot.detalles_montura_cliente, ot.usa_luna_cliente, ot.detalles_luna_cliente, ot.fecha_creacion, ot.fecha_prometida FROM ordenes_trabajo ot WHERE ot.estado_fisico != 'ENTREGADO' ORDER BY ot.fecha_prometida ASC";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar Ã³rdenes pendientes", e);
        }
        return list;
    }

    @Override
    public List<OrdenTrabajo> findByVentaId(Integer ventaId) {
        var list = new ArrayList<OrdenTrabajo>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE venta_id = ?")) {
            ps.setInt(1, ventaId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar Ã³rdenes por venta", e);
        }
        return list;
    }

    @Override
    public List<OrdenTrabajo> findByEstado(EstadoFisicoOT estado) {
        var list = new ArrayList<OrdenTrabajo>();
        try (var conn = getConnection(); var ps = conn.prepareStatement(SELECT_COLUMNS + " WHERE estado_fisico = ?")) {
            ps.setString(1, estado.name());
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar Ã³rdenes por estado", e);
        }
        return list;
    }

    @Override
    public List<OrdenTrabajo> findVencidas() {
        var list = new ArrayList<OrdenTrabajo>();
        var sql = SELECT_COLUMNS + " WHERE fecha_prometida < NOW() AND estado_fisico != 'ENTREGADO' ORDER BY fecha_prometida ASC";
        try (var conn = getConnection(); var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar Ã³rdenes vencidas", e);
        }
        return list;
    }

    @Override
    public void actualizarEstado(Integer ordenId, EstadoFisicoOT nuevoEstado) {
        try (var conn = getConnection(); var ps = conn.prepareStatement("UPDATE ordenes_trabajo SET estado_fisico = ? WHERE id = ?")) {
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, ordenId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar estado de orden de trabajo", e);
        }
    }

    private OrdenTrabajo map(ResultSet rs) throws SQLException {
        var o = new OrdenTrabajo();
        o.setId(rs.getInt("id"));
        o.setVentaId(rs.getInt("venta_id"));
        var hcId = rs.getObject("historial_clinico_id");
        if (hcId != null) o.setHistorialClinicoId(rs.getInt("historial_clinico_id"));
        o.setEstadoFisico(EstadoFisicoOT.valueOf(rs.getString("estado_fisico")));
        o.setTipoTrabajo(TipoTrabajo.valueOf(rs.getString("tipo_trabajo")));
        o.setUsaMonturaCliente(rs.getBoolean("usa_montura_cliente"));
        o.setDetallesMonturaCliente(rs.getString("detalles_montura_cliente"));
        o.setUsaLunaCliente(rs.getBoolean("usa_luna_cliente"));
        o.setDetallesLunaCliente(rs.getString("detalles_luna_cliente"));
        var ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) o.setFechaCreacion(ts.toLocalDateTime());
        ts = rs.getTimestamp("fecha_prometida");
        if (ts != null) o.setFechaPrometida(ts.toLocalDateTime());
        return o;
    }
}

