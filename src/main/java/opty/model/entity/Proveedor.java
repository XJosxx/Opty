package opty.model.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Proveedor {

    private Integer id;
    private String nombreEmpresa;
    private String nombreContacto;
    private String telefono;
    private String email;
    private String ruc;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private Integer tiendaId;

    public Proveedor() {}

    public Proveedor(Integer id, String nombreEmpresa, String nombreContacto, String telefono,
                     String email, String ruc, Boolean activo, LocalDateTime fechaRegistro,
                     Integer tiendaId) {
        this.id = id;
        setNombreEmpresa(nombreEmpresa);
        setNombreContacto(nombreContacto);
        setTelefono(telefono);
        setEmail(email);
        setRuc(ruc);
        setActivo(activo);
        setFechaRegistro(fechaRegistro);
        setTiendaId(tiendaId);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) {
        if (nombreEmpresa == null || nombreEmpresa.isBlank()) throw new IllegalArgumentException("El nombre de empresa no puede estar vacío");
        this.nombreEmpresa = nombreEmpresa.trim();
    }

    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) {
        if (nombreContacto == null || nombreContacto.isBlank()) throw new IllegalArgumentException("El nombre de contacto no puede estar vacío");
        this.nombreContacto = nombreContacto.trim();
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono != null ? telefono.trim() : null; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email != null ? email.trim() : null; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) {
        if (ruc == null || ruc.isBlank()) throw new IllegalArgumentException("El RUC no puede estar vacío");
        this.ruc = ruc.trim();
    }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Integer getTiendaId() { return tiendaId; }
    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proveedor proveedor)) return false;
        return Objects.equals(id, proveedor.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return nombreEmpresa; }
}
