package opty.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HistorialClinico {

    @EqualsAndHashCode.Include
    private Integer id;
    private Integer consultaId;
    private String graduacionOd;
    private String graduacionOi;
    private String observaciones;

    public void setConsultaId(Integer consultaId) {
        if (consultaId == null) throw new IllegalArgumentException("La consulta asociada no puede ser nula");
        this.consultaId = consultaId;
    }

    public void setGraduacionOd(String graduacionOd) {
        this.graduacionOd = graduacionOd != null ? graduacionOd.trim() : null;
    }

    public void setGraduacionOi(String graduacionOi) {
        this.graduacionOi = graduacionOi != null ? graduacionOi.trim() : null;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones != null ? observaciones.trim() : null;
    }

    @Override
    public String toString() { return "Historial #" + id + " - consulta #" + consultaId; }
}
