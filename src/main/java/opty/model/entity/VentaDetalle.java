package opty.model.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class VentaDetalle {

    private Integer id;
    private Integer ventaId;
    private Integer productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public VentaDetalle() {}

    public VentaDetalle(Integer id, Integer ventaId, Integer productoId, Integer cantidad,
                        BigDecimal precioUnitario, BigDecimal subtotal) {
        this.id = id;
        setVentaId(ventaId);
        setProductoId(productoId);
        setCantidad(cantidad);
        setPrecioUnitario(precioUnitario);
        setSubtotal(subtotal);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) {
        if (ventaId == null) throw new IllegalArgumentException("La venta asociada no puede ser nula");
        this.ventaId = ventaId;
    }

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) {
        if (productoId == null) throw new IllegalArgumentException("El producto no puede ser nulo");
        this.productoId = productoId;
    }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) {
        if (cantidad == null) throw new IllegalArgumentException("La cantidad no puede ser nula");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null) throw new IllegalArgumentException("El precio unitario no puede ser nulo");
        if (precioUnitario.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) {
        if (subtotal == null) throw new IllegalArgumentException("El subtotal no puede ser nulo");
        if (subtotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El subtotal no puede ser negativo");
        this.subtotal = subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VentaDetalle that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "Detalle venta #" + ventaId + " - prod #" + productoId; }
}
