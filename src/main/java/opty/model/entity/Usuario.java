package opty.model.entity;

import opty.model.enums.Rol;
import opty.model.enums.TipoDocumento;
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
public class Usuario {

    @EqualsAndHashCode.Include
    private Integer id;
    private String username;
    private String password;
    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private TipoDocumento tipoDocumento;
    private String numDocumento;
    private Rol rol;
    private Boolean activo;
    private LocalDateTime ultimoAcceso;
    private Integer tiendaId;

    public void setUsername(String username) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("El username no puede estar vacío");
        this.username = username.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.isBlank()) throw new IllegalArgumentException("La contraseña no puede estar vacía");
        this.password = password;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío");
        this.nombre = nombre.trim();
    }

    public void setApellidoP(String apellidoP) {
        if (apellidoP == null || apellidoP.isBlank()) throw new IllegalArgumentException("El apellido paterno no puede estar vacío");
        this.apellidoP = apellidoP.trim();
    }

    public void setApellidoM(String apellidoM) {
        if (apellidoM == null || apellidoM.isBlank()) throw new IllegalArgumentException("El apellido materno no puede estar vacío");
        this.apellidoM = apellidoM.trim();
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        if (tipoDocumento == null) throw new IllegalArgumentException("El tipo de documento no puede ser nulo");
        this.tipoDocumento = tipoDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        if (numDocumento == null || numDocumento.isBlank()) throw new IllegalArgumentException("El número de documento no puede estar vacío");
        this.numDocumento = numDocumento.trim();
    }

    public void setRol(Rol rol) {
        if (rol == null) throw new IllegalArgumentException("El rol no puede ser nulo");
        this.rol = rol;
    }

    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public String nombreCompleto() {
        return nombre + " " + apellidoP + " " + apellidoM;
    }

    @Override
    public String toString() { return nombreCompleto() + " (" + username + ")"; }
}
