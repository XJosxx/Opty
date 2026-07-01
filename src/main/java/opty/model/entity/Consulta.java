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
public class Consulta {

    @EqualsAndHashCode.Include
    private Integer id;
    private Integer pacienteId;
    private Integer usuarioId;
    private Integer tiendaId;
    private Integer ventaDetalleId;
    private LocalDateTime fecha;
    private String motivo;

    public void setPacienteId(Integer pacienteId) {
        if (pacienteId == null) throw new IllegalArgumentException("El paciente no puede ser nulo");
        this.pacienteId = pacienteId;
    }

    public void setUsuarioId(Integer usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("El usuario no puede ser nulo");
        this.usuarioId = usuarioId;
    }

    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo != null ? motivo.trim() : null;
    }

    @Override
    public String toString() { return "Consulta #" + id + " - paciente #" + pacienteId; }
}
