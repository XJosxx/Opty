package opty.model.entity;

import opty.model.enums.CategoriaProducto;
import opty.model.enums.GeneroObjetivo;
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
public class Producto {

    @EqualsAndHashCode.Include
    private Integer id;
    private Integer tiendaId;
    private String codigo;
    private String nombre;
    private CategoriaProducto categoria;
    private GeneroObjetivo generoObjetivo;
    private BigDecimal precioVenta;
    private Integer stockActual;
    private Integer stockMinimo;
    private Boolean activo;

    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo != null ? codigo.trim() : null;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        this.nombre = nombre.trim();
    }

    public void setCategoria(CategoriaProducto categoria) {
        if (categoria == null) throw new IllegalArgumentException("La categoría no puede ser nula");
        this.categoria = categoria;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        if (precioVenta == null) throw new IllegalArgumentException("El precio de venta no puede ser nulo");
        if (precioVenta.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El precio de venta no puede ser negativo");
        this.precioVenta = precioVenta;
    }

    public void setStockActual(Integer stockActual) {
        if (stockActual == null) throw new IllegalArgumentException("El stock actual no puede ser nulo");
        if (stockActual < 0) throw new IllegalArgumentException("El stock actual no puede ser negativo");
        this.stockActual = stockActual;
    }

    public void setStockMinimo(Integer stockMinimo) {
        if (stockMinimo == null) throw new IllegalArgumentException("El stock mínimo no puede ser nulo");
        if (stockMinimo < 0) throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        this.stockMinimo = stockMinimo;
    }

    @Override
    public String toString() { return nombre + " (" + codigo + ")"; }
}
