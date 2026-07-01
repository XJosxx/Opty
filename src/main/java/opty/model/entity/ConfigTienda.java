package opty.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ConfigTienda {

    @EqualsAndHashCode.Include
    private Integer id;
    private String codigo;
    private String nombreOptica;
    private String ruc;
    private String direccion;
    private String telefono;

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("El código de tienda no puede estar vacío");
        this.codigo = codigo.trim();
    }

    public void setNombreOptica(String nombreOptica) {
        if (nombreOptica == null || nombreOptica.isBlank()) throw new IllegalArgumentException("El nombre de la óptica no puede estar vacío");
        this.nombreOptica = nombreOptica.trim();
    }

    public void setRuc(String ruc) {
        if (ruc == null || ruc.isBlank()) throw new IllegalArgumentException("El RUC no puede estar vacío");
        this.ruc = ruc.trim();
    }

    public void setDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) throw new IllegalArgumentException("La dirección no puede estar vacía");
        this.direccion = direccion.trim();
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono != null ? telefono.trim() : null;
    }

    @Override
    public String toString() { return nombreOptica + " (" + codigo + ")"; }
}
