package opty.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import opty.model.enums.CategoriaInsumo;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Insumo {

    @EqualsAndHashCode.Include
    private Integer id;
    private Integer tiendaId;
    private String codigo;
    private String nombre;
    private CategoriaInsumo categoria;
    private String material;
    private BigDecimal precioCosto;
    private Integer stockActual;
    private Integer stockMinimo;
    private Boolean activo;

    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("El código de insumo no puede estar vacío");
        this.codigo = codigo.trim();
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del insumo no puede estar vacío");
        this.nombre = nombre.trim();
    }

    public void setCategoria(CategoriaInsumo categoria) {
        if (categoria == null) throw new IllegalArgumentException("La categoría no puede ser nula");
        this.categoria = categoria;
    }

    public void setMaterial(String material) {
        this.material = material != null ? material.trim() : null;
    }

    public void setPrecioCosto(BigDecimal precioCosto) {
        if (precioCosto == null) throw new IllegalArgumentException("El precio de costo no puede ser nulo");
        if (precioCosto.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El precio de costo no puede ser negativo");
        this.precioCosto = precioCosto;
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
