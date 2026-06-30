package opty.model.entity;

import opty.model.enums.CategoriaProducto;
import opty.model.enums.GeneroObjetivo;

import java.math.BigDecimal;
import java.util.Objects;

public class Producto {

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

    public Producto() {}

    public Producto(Integer id, Integer tiendaId, String codigo, String nombre,
                    CategoriaProducto categoria, GeneroObjetivo generoObjetivo,
                    BigDecimal precioVenta, Integer stockActual, Integer stockMinimo,
                    Boolean activo) {
        this.id = id;
        setTiendaId(tiendaId);
        setCodigo(codigo);
        setNombre(nombre);
        setCategoria(categoria);
        setGeneroObjetivo(generoObjetivo);
        setPrecioVenta(precioVenta);
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
    public void setCodigo(String codigo) { this.codigo = codigo != null ? codigo.trim() : null; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        this.nombre = nombre.trim();
    }

    public CategoriaProducto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProducto categoria) {
        if (categoria == null) throw new IllegalArgumentException("La categoría no puede ser nula");
        this.categoria = categoria;
    }

    public GeneroObjetivo getGeneroObjetivo() { return generoObjetivo; }
    public void setGeneroObjetivo(GeneroObjetivo generoObjetivo) { this.generoObjetivo = generoObjetivo; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) {
        if (precioVenta == null) throw new IllegalArgumentException("El precio de venta no puede ser nulo");
        if (precioVenta.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El precio de venta no puede ser negativo");
        this.precioVenta = precioVenta;
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
        if (!(o instanceof Producto producto)) return false;
        return Objects.equals(id, producto.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return nombre + " (" + codigo + ")"; }
}
