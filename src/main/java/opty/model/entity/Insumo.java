package opty.model.entity;

import opty.model.enums.CategoriaInsumo;

import java.math.BigDecimal;
import java.util.Objects;

public class Insumo {

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

    public Insumo() {}

    public Insumo(Integer id, Integer tiendaId, String codigo, String nombre,
                  CategoriaInsumo categoria, String material, BigDecimal precioCosto,
                  Integer stockActual, Integer stockMinimo, Boolean activo) {
        this.id = id;
        setTiendaId(tiendaId);
        setCodigo(codigo);
        setNombre(nombre);
        setCategoria(categoria);
        setMaterial(material);
        setPrecioCosto(precioCosto);
        setStockActual(stockActual);
        setStockMinimo(stockMinimo);
        setActivo(activo);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTiendaId() { return tiendaId; }
    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("El código de insumo no puede estar vacío");
        this.codigo = codigo.trim();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del insumo no puede estar vacío");
        this.nombre = nombre.trim();
    }

    public CategoriaInsumo getCategoria() { return categoria; }
    public void setCategoria(CategoriaInsumo categoria) {
        if (categoria == null) throw new IllegalArgumentException("La categoría no puede ser nula");
        this.categoria = categoria;
    }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material != null ? material.trim() : null; }

    public BigDecimal getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(BigDecimal precioCosto) {
        if (precioCosto == null) throw new IllegalArgumentException("El precio de costo no puede ser nulo");
        if (precioCosto.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El precio de costo no puede ser negativo");
        this.precioCosto = precioCosto;
    }

    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) {
        if (stockActual == null) throw new IllegalArgumentException("El stock actual no puede ser nulo");
        if (stockActual < 0) throw new IllegalArgumentException("El stock actual no puede ser negativo");
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) {
        if (stockMinimo == null) throw new IllegalArgumentException("El stock mínimo no puede ser nulo");
        if (stockMinimo < 0) throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        this.stockMinimo = stockMinimo;
    }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Insumo insumo)) return false;
        return Objects.equals(id, insumo.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return nombre + " (" + codigo + ")"; }
}
