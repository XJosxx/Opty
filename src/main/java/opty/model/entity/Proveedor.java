package opty.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Proveedor {

    @EqualsAndHashCode.Include
    private Integer id;
    private String nombreEmpresa;
    private String nombreContacto;
    private String telefono;
    private String email;
    private String ruc;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private Integer tiendaId;

    public void setNombreEmpresa(String nombreEmpresa) {
        if (nombreEmpresa == null || nombreEmpresa.isBlank()) throw new IllegalArgumentException("El nombre de empresa no puede estar vacío");
        this.nombreEmpresa = nombreEmpresa.trim();
    }

    public void setNombreContacto(String nombreContacto) {
        if (nombreContacto == null || nombreContacto.isBlank()) throw new IllegalArgumentException("El nombre de contacto no puede estar vacío");
        this.nombreContacto = nombreContacto.trim();
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono != null ? telefono.trim() : null;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public void setRuc(String ruc) {
        if (ruc == null || ruc.isBlank()) throw new IllegalArgumentException("El RUC no puede estar vacío");
        this.ruc = ruc.trim();
    }

    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    @Override
    public String toString() { return nombreEmpresa; }
}
