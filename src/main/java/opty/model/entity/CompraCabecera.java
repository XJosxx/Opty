package opty.model.entity;

import opty.model.enums.EstadoFisicoCompra;
import opty.model.enums.EstadoFinanciero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class CompraCabecera {

    private Integer id;
    private Integer proveedorId;
    private Integer usuarioId;
    private Integer tiendaId;
    private String numeroOrden;
    private LocalDateTime fechaEmision;
    private EstadoFisicoCompra estadoFisico;
    private EstadoFinanciero estadoFinanciero;
    private BigDecimal montoTotal;

    public CompraCabecera() {}

    public CompraCabecera(Integer id, Integer proveedorId, Integer usuarioId, Integer tiendaId,
                          String numeroOrden, LocalDateTime fechaEmision,
                          EstadoFisicoCompra estadoFisico, EstadoFinanciero estadoFinanciero,
                          BigDecimal montoTotal) {
        this.id = id;
        setProveedorId(proveedorId);
        setUsuarioId(usuarioId);
        setTiendaId(tiendaId);
        setNumeroOrden(numeroOrden);
        setFechaEmision(fechaEmision);
        setEstadoFisico(estadoFisico);
        setEstadoFinanciero(estadoFinanciero);
        setMontoTotal(montoTotal);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) {
        if (proveedorId == null) throw new IllegalArgumentException("El proveedor no puede ser nulo");
        this.proveedorId = proveedorId;
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

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) {
        if (numeroOrden == null || numeroOrden.isBlank()) throw new IllegalArgumentException("El número de orden no puede estar vacío");
        this.numeroOrden = numeroOrden.trim();
    }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public EstadoFisicoCompra getEstadoFisico() { return estadoFisico; }
    public void setEstadoFisico(EstadoFisicoCompra estadoFisico) {
        if (estadoFisico == null) throw new IllegalArgumentException("El estado físico no puede ser nulo");
        this.estadoFisico = estadoFisico;
    }

    public EstadoFinanciero getEstadoFinanciero() { return estadoFinanciero; }
    public void setEstadoFinanciero(EstadoFinanciero estadoFinanciero) {
        if (estadoFinanciero == null) throw new IllegalArgumentException("El estado financiero no puede ser nulo");
        this.estadoFinanciero = estadoFinanciero;
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
        if (!(o instanceof CompraCabecera that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return numeroOrden; }
}
