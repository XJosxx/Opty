package opty.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VentaDetalle {

    @EqualsAndHashCode.Include
    private Integer id;
    private Integer ventaId;
    private Integer productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public void setVentaId(Integer ventaId) {
        if (ventaId == null) throw new IllegalArgumentException("La venta asociada no puede ser nula");
        this.ventaId = ventaId;
    }

    public void setProductoId(Integer productoId) {
        if (productoId == null) throw new IllegalArgumentException("El producto no puede ser nulo");
        this.productoId = productoId;
    }

    public void setCantidad(Integer cantidad) {
        if (cantidad == null) throw new IllegalArgumentException("La cantidad no puede ser nula");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null) throw new IllegalArgumentException("El precio unitario no puede ser nulo");
        if (precioUnitario.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
        this.precioUnitario = precioUnitario;
    }

    public void setSubtotal(BigDecimal subtotal) {
        if (subtotal == null) throw new IllegalArgumentException("El subtotal no puede ser nulo");
        if (subtotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El subtotal no puede ser negativo");
        this.subtotal = subtotal;
    }

    @Override
    public String toString() { return "Detalle venta #" + ventaId + " - prod #" + productoId; }
}
