package opty.model.entity;

import opty.model.enums.EstadoFisicoOT;
import opty.model.enums.TipoTrabajo;

import java.time.LocalDateTime;
import java.util.Objects;

public class OrdenTrabajo {

    private Integer id;
    private Integer ventaId;
    private Integer historialClinicoId;
    private EstadoFisicoOT estadoFisico;
    private TipoTrabajo tipoTrabajo;
    private Boolean usaMonturaCliente;
    private String detallesMonturaCliente;
    private Boolean usaLunaCliente;
    private String detallesLunaCliente;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaPrometida;

    public OrdenTrabajo() {}

    public OrdenTrabajo(Integer id, Integer ventaId, Integer historialClinicoId,
                        EstadoFisicoOT estadoFisico, TipoTrabajo tipoTrabajo,
                        Boolean usaMonturaCliente, String detallesMonturaCliente,
                        Boolean usaLunaCliente, String detallesLunaCliente,
                        LocalDateTime fechaCreacion, LocalDateTime fechaPrometida) {
        this.id = id;
        setVentaId(ventaId);
        setHistorialClinicoId(historialClinicoId);
        setEstadoFisico(estadoFisico);
        setTipoTrabajo(tipoTrabajo);
        setUsaMonturaCliente(usaMonturaCliente);
        setDetallesMonturaCliente(detallesMonturaCliente);
        setUsaLunaCliente(usaLunaCliente);
        setDetallesLunaCliente(detallesLunaCliente);
        setFechaCreacion(fechaCreacion);
        setFechaPrometida(fechaPrometida);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) {
        if (ventaId == null) throw new IllegalArgumentException("La venta asociada no puede ser nula");
        this.ventaId = ventaId;
    }

    public Integer getHistorialClinicoId() { return historialClinicoId; }
    public void setHistorialClinicoId(Integer historialClinicoId) { this.historialClinicoId = historialClinicoId; }

    public EstadoFisicoOT getEstadoFisico() { return estadoFisico; }
    public void setEstadoFisico(EstadoFisicoOT estadoFisico) {
        if (estadoFisico == null) throw new IllegalArgumentException("El estado físico no puede ser nulo");
        this.estadoFisico = estadoFisico;
    }

    public TipoTrabajo getTipoTrabajo() { return tipoTrabajo; }
    public void setTipoTrabajo(TipoTrabajo tipoTrabajo) {
        if (tipoTrabajo == null) throw new IllegalArgumentException("El tipo de trabajo no puede ser nulo");
        this.tipoTrabajo = tipoTrabajo;
    }

    public Boolean getUsaMonturaCliente() { return usaMonturaCliente; }
    public void setUsaMonturaCliente(Boolean usaMonturaCliente) { this.usaMonturaCliente = usaMonturaCliente; }

    public String getDetallesMonturaCliente() { return detallesMonturaCliente; }
    public void setDetallesMonturaCliente(String detallesMonturaCliente) { this.detallesMonturaCliente = detallesMonturaCliente != null ? detallesMonturaCliente.trim() : null; }

    public Boolean getUsaLunaCliente() { return usaLunaCliente; }
    public void setUsaLunaCliente(Boolean usaLunaCliente) { this.usaLunaCliente = usaLunaCliente; }

    public String getDetallesLunaCliente() { return detallesLunaCliente; }
    public void setDetallesLunaCliente(String detallesLunaCliente) { this.detallesLunaCliente = detallesLunaCliente != null ? detallesLunaCliente.trim() : null; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaPrometida() { return fechaPrometida; }
    public void setFechaPrometida(LocalDateTime fechaPrometida) { this.fechaPrometida = fechaPrometida; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdenTrabajo that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "OT #" + id + " - " + tipoTrabajo + " [" + estadoFisico + "]"; }
}
