package opty.model.entity;

import opty.model.enums.TipoMovimientoKardex;

import java.time.LocalDateTime;
import java.util.Objects;

public class Kardex {

    private Integer id;
    private Integer tiendaId;
    private Integer usuarioId;
    private Integer insumoId;
    private Integer productoId;
    private Integer compraId;
    private Integer ventaId;
    private TipoMovimientoKardex tipoMovimiento;
    private String motivo;
    private Integer cantidad;
    private Integer cantidadSaldo;
    private LocalDateTime fecha;

    public Kardex() {}

    public Kardex(Integer id, Integer tiendaId, Integer usuarioId, Integer insumoId,
                  Integer productoId, Integer compraId, Integer ventaId,
                  TipoMovimientoKardex tipoMovimiento, String motivo,
                  Integer cantidad, Integer cantidadSaldo, LocalDateTime fecha) {
        this.id = id;
        setTiendaId(tiendaId);
        setUsuarioId(usuarioId);
        setInsumoId(insumoId);
        setProductoId(productoId);
        setCompraId(compraId);
        setVentaId(ventaId);
        setTipoMovimiento(tipoMovimiento);
        setMotivo(motivo);
        setCantidad(cantidad);
        setCantidadSaldo(cantidadSaldo);
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

    public Integer getInsumoId() { return insumoId; }
    public void setInsumoId(Integer insumoId) { this.insumoId = insumoId; }

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }

    public Integer getCompraId() { return compraId; }
    public void setCompraId(Integer compraId) { this.compraId = compraId; }

    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }

    public TipoMovimientoKardex getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(TipoMovimientoKardex tipoMovimiento) {
        if (tipoMovimiento == null) throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo");
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo != null ? motivo.trim() : null; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) {
        if (cantidad == null) throw new IllegalArgumentException("La cantidad no puede ser nula");
        this.cantidad = cantidad;
    }

    public Integer getCantidadSaldo() { return cantidadSaldo; }
    public void setCantidadSaldo(Integer cantidadSaldo) {
        if (cantidadSaldo == null) throw new IllegalArgumentException("El saldo no puede ser nulo");
        if (cantidadSaldo < 0) throw new IllegalArgumentException("El saldo no puede ser negativo");
        this.cantidadSaldo = cantidadSaldo;
    }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kardex kardex)) return false;
        return Objects.equals(id, kardex.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return tipoMovimiento + " - " + motivo; }
}
