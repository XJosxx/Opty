package opty.model.entity;

import opty.model.enums.EstadoFisicoOT;
import opty.model.enums.TipoTrabajo;
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
public class OrdenTrabajo {

    @EqualsAndHashCode.Include
    private Integer id;
    private Integer ventaId;
    private Integer historialClinicoId;
    private EstadoFisicoOT estadoFisico;
    private TipoTrabajo tipoTrabajo;
    private Boolean usaMonturaCliente;
    private String detallesMonturaCliente;
    private Boolean usaLunaCliente;
    private String detallesLunaCliente;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaPrometida;

    // Campos virtuales/relacionales para visualización en la interfaz
    private String numeroTicket;
    private String nombrePaciente;

    public void setVentaId(Integer ventaId) {
        if (ventaId == null) throw new IllegalArgumentException("La venta asociada no puede ser nula");
        this.ventaId = ventaId;
    }

    public void setEstadoFisico(EstadoFisicoOT estadoFisico) {
        if (estadoFisico == null) throw new IllegalArgumentException("El estado físico no puede ser nulo");
        this.estadoFisico = estadoFisico;
    }

    public void setTipoTrabajo(TipoTrabajo tipoTrabajo) {
        if (tipoTrabajo == null) throw new IllegalArgumentException("El tipo de trabajo no puede ser nulo");
        this.tipoTrabajo = tipoTrabajo;
    }

    public void setDetallesMonturaCliente(String detallesMonturaCliente) {
        this.detallesMonturaCliente = detallesMonturaCliente != null ? detallesMonturaCliente.trim() : null;
    }

    public void setDetallesLunaCliente(String detallesLunaCliente) {
        this.detallesLunaCliente = detallesLunaCliente != null ? detallesLunaCliente.trim() : null;
    }

    @Override
    public String toString() { return "OT #" + id + " - " + tipoTrabajo + " [" + estadoFisico + "]"; }
}
