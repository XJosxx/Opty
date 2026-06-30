package opty.model.entity;

import opty.model.enums.EstadoFinanciero;
import opty.model.enums.TipoComprobante;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class VentaCabecera {

    private Integer id;
    private Integer pacienteId;
    private Integer usuarioId;
    private Integer tiendaId;
    private String numeroTicket;
    private LocalDateTime fechaEmision;
    private EstadoFinanciero estadoFinanciero;
    private TipoComprobante tipoComprobante;
    private BigDecimal montoSubtotal;
    private BigDecimal montoIgv;
    private BigDecimal montoTotal;

    public VentaCabecera() {}

    public VentaCabecera(Integer id, Integer pacienteId, Integer usuarioId, Integer tiendaId,
                         String numeroTicket, LocalDateTime fechaEmision,
                         EstadoFinanciero estadoFinanciero, TipoComprobante tipoComprobante,
                         BigDecimal montoSubtotal, BigDecimal montoIgv, BigDecimal montoTotal) {
        this.id = id;
        setPacienteId(pacienteId);
        setUsuarioId(usuarioId);
        setTiendaId(tiendaId);
        setNumeroTicket(numeroTicket);
        setFechaEmision(fechaEmision);
        setEstadoFinanciero(estadoFinanciero);
        setTipoComprobante(tipoComprobante);
        setMontoSubtotal(montoSubtotal);
        setMontoIgv(montoIgv);
        setMontoTotal(montoTotal);
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

    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String numeroTicket) {
        if (numeroTicket == null || numeroTicket.isBlank()) throw new IllegalArgumentException("El número de ticket no puede estar vacío");
        this.numeroTicket = numeroTicket.trim();
    }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public EstadoFinanciero getEstadoFinanciero() { return estadoFinanciero; }
    public void setEstadoFinanciero(EstadoFinanciero estadoFinanciero) {
        if (estadoFinanciero == null) throw new IllegalArgumentException("El estado financiero no puede ser nulo");
        this.estadoFinanciero = estadoFinanciero;
    }

    public TipoComprobante getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        if (tipoComprobante == null) throw new IllegalArgumentException("El tipo de comprobante no puede ser nulo");
        this.tipoComprobante = tipoComprobante;
    }

    public BigDecimal getMontoSubtotal() { return montoSubtotal; }
    public void setMontoSubtotal(BigDecimal montoSubtotal) {
        if (montoSubtotal == null) throw new IllegalArgumentException("El monto subtotal no puede ser nulo");
        if (montoSubtotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El monto subtotal no puede ser negativo");
        this.montoSubtotal = montoSubtotal;
    }

    public BigDecimal getMontoIgv() { return montoIgv; }
    public void setMontoIgv(BigDecimal montoIgv) {
        if (montoIgv == null) throw new IllegalArgumentException("El monto IGV no puede ser nulo");
        if (montoIgv.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El monto IGV no puede ser negativo");
        this.montoIgv = montoIgv;
    }

    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) {
        if (montoTotal == null) throw new IllegalArgumentException("El monto total no puede ser nulo");
        if (montoTotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El monto total no puede ser negativo");
        this.montoTotal = montoTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VentaCabecera that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return numeroTicket + " - S/ " + montoTotal; }
}
