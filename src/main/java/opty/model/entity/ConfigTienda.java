package opty.model.entity;

import java.util.Objects;

public class ConfigTienda {

    private Integer id;
    private String codigo;
    private String nombreOptica;
    private String ruc;
    private String direccion;
    private String telefono;

    public ConfigTienda() {}

    public ConfigTienda(Integer id, String codigo, String nombreOptica, String ruc, String direccion, String telefono) {
        this.id = id;
        setCodigo(codigo);
        setNombreOptica(nombreOptica);
        setRuc(ruc);
        setDireccion(direccion);
        setTelefono(telefono);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("El código de tienda no puede estar vacío");
        this.codigo = codigo.trim();
    }

    public String getNombreOptica() { return nombreOptica; }
    public void setNombreOptica(String nombreOptica) {
        if (nombreOptica == null || nombreOptica.isBlank()) throw new IllegalArgumentException("El nombre de la óptica no puede estar vacío");
        this.nombreOptica = nombreOptica.trim();
    }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) {
        if (ruc == null || ruc.isBlank()) throw new IllegalArgumentException("El RUC no puede estar vacío");
        this.ruc = ruc.trim();
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) throw new IllegalArgumentException("La dirección no puede estar vacía");
        this.direccion = direccion.trim();
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono != null ? telefono.trim() : null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigTienda that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return nombreOptica + " (" + codigo + ")"; }
}
