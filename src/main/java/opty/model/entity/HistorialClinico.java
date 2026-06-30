package opty.model.entity;

import java.util.Objects;

public class HistorialClinico {

    private Integer id;
    private Integer consultaId;
    private String graduacionOd;
    private String graduacionOi;
    private String observaciones;

    public HistorialClinico() {}

    public HistorialClinico(Integer id, Integer consultaId, String graduacionOd,
                            String graduacionOi, String observaciones) {
        this.id = id;
        setConsultaId(consultaId);
        setGraduacionOd(graduacionOd);
        setGraduacionOi(graduacionOi);
        setObservaciones(observaciones);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getConsultaId() { return consultaId; }
    public void setConsultaId(Integer consultaId) {
        if (consultaId == null) throw new IllegalArgumentException("La consulta asociada no puede ser nula");
        this.consultaId = consultaId;
    }

    public String getGraduacionOd() { return graduacionOd; }
    public void setGraduacionOd(String graduacionOd) { this.graduacionOd = graduacionOd != null ? graduacionOd.trim() : null; }

    public String getGraduacionOi() { return graduacionOi; }
    public void setGraduacionOi(String graduacionOi) { this.graduacionOi = graduacionOi != null ? graduacionOi.trim() : null; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones != null ? observaciones.trim() : null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistorialClinico that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return "Historial #" + id + " - consulta #" + consultaId; }
}
