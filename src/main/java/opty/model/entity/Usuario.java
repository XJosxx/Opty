package opty.model.entity;

import opty.model.enums.Rol;
import opty.model.enums.TipoDocumento;

import java.time.LocalDateTime;
import java.util.Objects;

public class Usuario {

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

    public Usuario() {}

    public Usuario(Integer id, String username, String password, String nombre, String apellidoP,
                   String apellidoM, TipoDocumento tipoDocumento, String numDocumento, Rol rol,
                   Boolean activo, LocalDateTime ultimoAcceso, Integer tiendaId) {
        this.id = id;
        setUsername(username);
        setPassword(password);
        setNombre(nombre);
        setApellidoP(apellidoP);
        setApellidoM(apellidoM);
        setTipoDocumento(tipoDocumento);
        setNumDocumento(numDocumento);
        setRol(rol);
        setActivo(activo);
        setUltimoAcceso(ultimoAcceso);
        setTiendaId(tiendaId);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("El username no puede estar vacío");
        this.username = username.trim();
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password == null || password.isBlank()) throw new IllegalArgumentException("La contraseña no puede estar vacía");
        this.password = password;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío");
        this.nombre = nombre.trim();
    }

    public String getApellidoP() { return apellidoP; }
    public void setApellidoP(String apellidoP) {
        if (apellidoP == null || apellidoP.isBlank()) throw new IllegalArgumentException("El apellido paterno no puede estar vacío");
        this.apellidoP = apellidoP.trim();
    }

    public String getApellidoM() { return apellidoM; }
    public void setApellidoM(String apellidoM) {
        if (apellidoM == null || apellidoM.isBlank()) throw new IllegalArgumentException("El apellido materno no puede estar vacío");
        this.apellidoM = apellidoM.trim();
    }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        if (tipoDocumento == null) throw new IllegalArgumentException("El tipo de documento no puede ser nulo");
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumDocumento() { return numDocumento; }
    public void setNumDocumento(String numDocumento) {
        if (numDocumento == null || numDocumento.isBlank()) throw new IllegalArgumentException("El número de documento no puede estar vacío");
        this.numDocumento = numDocumento.trim();
    }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) {
        if (rol == null) throw new IllegalArgumentException("El rol no puede ser nulo");
        this.rol = rol;
    }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public Integer getTiendaId() { return tiendaId; }
    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public String nombreCompleto() {
        return nombre + " " + apellidoP + " " + apellidoM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return nombreCompleto() + " (" + username + ")"; }
}
