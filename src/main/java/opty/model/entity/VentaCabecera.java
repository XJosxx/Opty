package opty.model.entity;

import opty.model.enums.EstadoFinanciero;
import opty.model.enums.TipoComprobante;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VentaCabecera {

    @EqualsAndHashCode.Include
    private Integer id;
    private Integer pacienteId;
    private Integer usuarioId;
    private Integer tiendaId;
    private String numeroTicket;
    private LocalDateTime fechaEmision;
    private EstadoFinanciero estadoFinanciero;
    private TipoComprobante tipoComprobante;
    private BigDecimal montoSubtotal;
    private BigDecimal montoIgv;
    private BigDecimal montoTotal;

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

    public void setNumeroTicket(String numeroTicket) {
        if (numeroTicket == null || numeroTicket.isBlank()) throw new IllegalArgumentException("El número de ticket no puede estar vacío");
        this.numeroTicket = numeroTicket.trim();
    }

    public void setEstadoFinanciero(EstadoFinanciero estadoFinanciero) {
        if (estadoFinanciero == null) throw new IllegalArgumentException("El estado financiero no puede ser nulo");
        this.estadoFinanciero = estadoFinanciero;
    }

    public void setTipoComprobante(TipoComprobante tipoComprobante) {
        if (tipoComprobante == null) throw new IllegalArgumentException("El tipo de comprobante no puede ser nulo");
        this.tipoComprobante = tipoComprobante;
    }

    public void setMontoSubtotal(BigDecimal montoSubtotal) {
        if (montoSubtotal == null) throw new IllegalArgumentException("El monto subtotal no puede ser nulo");
        if (montoSubtotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El monto subtotal no puede ser negativo");
        this.montoSubtotal = montoSubtotal;
    }

    public void setMontoIgv(BigDecimal montoIgv) {
        if (montoIgv == null) throw new IllegalArgumentException("El monto IGV no puede ser nulo");
        if (montoIgv.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El monto IGV no puede ser negativo");
        this.montoIgv = montoIgv;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        if (montoTotal == null) throw new IllegalArgumentException("El monto total no puede ser nulo");
        if (montoTotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El monto total no puede ser negativo");
        this.montoTotal = montoTotal;
    }

    @Override
    public String toString() { return numeroTicket + " - S/ " + montoTotal; }
}
