package opty.model.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Consulta {

    private Integer id;
    private Integer pacienteId;
    private Integer usuarioId;
    private Integer tiendaId;
    private Integer ventaDetalleId;
    private LocalDateTime fecha;
    private String motivo;

    public Consulta() {}

    public Consulta(Integer id, Integer pacienteId, Integer usuarioId, Integer tiendaId,
                    Integer ventaDetalleId, LocalDateTime fecha, String motivo) {
        this.id = id;
        setPacienteId(pacienteId);
        setUsuarioId(usuarioId);
        setTiendaId(tiendaId);
        setVentaDetalleId(ventaDetalleId);
        setFecha(fecha);
        setMotivo(motivo);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPacienteId() { return pacienteId; }
    public void setPacienteId(Integer pacienteId) {
        if (pacienteId == null) throw new IllegalArgumentException("El paciente no puede ser nulo");
        this.pacienteId = pacienteId;
    }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("El usuario no puede ser nulo");
        this.usuarioId = usuarioId;
    }

    public Integer getTiendaId() { return tiendaId; }
    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public Integer getVentaDetalleId() { return ventaDetalleId; }
    public void setVentaDetalleId(Integer ventaDetalleId) { this.ventaDetalleId = ventaDetalleId; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo != null ? motivo.trim() : null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consulta consulta)) return false;
        return Objects.equals(id, consulta.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "Consulta #" + id + " - paciente #" + pacienteId; }
}
