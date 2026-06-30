package opty.model.entity;

import opty.model.enums.TipoDestacado;
import opty.model.enums.TipoDocumento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Paciente {

    private Integer id;
    private String nombre;
    private String apellidoP;
    private String apellidoM;
    private TipoDocumento tipoDocumento;
    private String numDocumento;
    private String telefono;
    private LocalDate fechaNacimiento;
    private Boolean esDestacado;
    private TipoDestacado tipoDestacado;
    private LocalDateTime fechaRegistro;
    private Integer tiendaId;

    public Paciente() {}

    public Paciente(Integer id, String nombre, String apellidoP, String apellidoM,
                    TipoDocumento tipoDocumento, String numDocumento, String telefono,
                    LocalDate fechaNacimiento, Boolean esDestacado, TipoDestacado tipoDestacado,
                    LocalDateTime fechaRegistro, Integer tiendaId) {
        this.id = id;
        setNombre(nombre);
        setApellidoP(apellidoP);
        setApellidoM(apellidoM);
        setTipoDocumento(tipoDocumento);
        setNumDocumento(numDocumento);
        setTelefono(telefono);
        setFechaNacimiento(fechaNacimiento);
        setEsDestacado(esDestacado);
        setTipoDestacado(tipoDestacado);
        setFechaRegistro(fechaRegistro);
        setTiendaId(tiendaId);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono != null ? telefono.trim() : null; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Boolean getEsDestacado() { return esDestacado; }
    public void setEsDestacado(Boolean esDestacado) { this.esDestacado = esDestacado; }

    public TipoDestacado getTipoDestacado() { return tipoDestacado; }
    public void setTipoDestacado(TipoDestacado tipoDestacado) { this.tipoDestacado = tipoDestacado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

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
        if (!(o instanceof Paciente paciente)) return false;
        return Objects.equals(id, paciente.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return nombreCompleto() + " (" + numDocumento + ")"; }
}
