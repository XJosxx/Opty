package opty.model.entity;

import opty.model.enums.MetodoPago;
import opty.model.enums.TipoMovimientoCaja;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class MovimientoCaja {

    private Integer id;
    private Integer tiendaId;
    private Integer usuarioId;
    private Integer ventaId;
    private Integer compraId;
    private TipoMovimientoCaja tipo;
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fecha;

    public MovimientoCaja() {}

    public MovimientoCaja(Integer id, Integer tiendaId, Integer usuarioId, Integer ventaId,
                          Integer compraId, TipoMovimientoCaja tipo, MetodoPago metodoPago,
                          BigDecimal monto, String descripcion, LocalDateTime fecha) {
        this.id = id;
        setTiendaId(tiendaId);
        setUsuarioId(usuarioId);
        setVentaId(ventaId);
        setCompraId(compraId);
        setTipo(tipo);
        setMetodoPago(metodoPago);
        setMonto(monto);
        setDescripcion(descripcion);
        setFecha(fecha);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTiendaId() { return tiendaId; }
    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("El usuario no puede ser nulo");
        this.usuarioId = usuarioId;
    }

    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }

    public Integer getCompraId() { return compraId; }
    public void setCompraId(Integer compraId) { this.compraId = compraId; }

    public TipoMovimientoCaja getTipo() { return tipo; }
    public void setTipo(TipoMovimientoCaja tipo) {
        if (tipo == null) throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo");
        this.tipo = tipo;
    }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) {
        if (metodoPago == null) throw new IllegalArgumentException("El método de pago no puede ser nulo");
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) {
        if (monto == null) throw new IllegalArgumentException("El monto no puede ser nulo");
        if (monto.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("El monto debe ser mayor a cero");
        this.monto = monto;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion != null ? descripcion.trim() : null; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovimientoCaja that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return tipo + " S/ " + monto + " - " + descripcion; }
}
