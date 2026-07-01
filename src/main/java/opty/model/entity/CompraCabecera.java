package opty.model.entity;

import opty.model.enums.EstadoFisicoCompra;
import opty.model.enums.EstadoFinanciero;
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
public class CompraCabecera {

    @EqualsAndHashCode.Include
    private Integer id;
    private Integer proveedorId;
    private Integer usuarioId;
    private Integer tiendaId;
    private String numeroOrden;
    private LocalDateTime fechaEmision;
    private EstadoFisicoCompra estadoFisico;
    private EstadoFinanciero estadoFinanciero;
    private BigDecimal montoTotal;

    public void setProveedorId(Integer proveedorId) {
        if (proveedorId == null) throw new IllegalArgumentException("El proveedor no puede ser nulo");
        this.proveedorId = proveedorId;
    }

    public void setUsuarioId(Integer usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("El usuario no puede ser nulo");
        this.usuarioId = usuarioId;
    }

    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public void setNumeroOrden(String numeroOrden) {
        if (numeroOrden == null || numeroOrden.isBlank()) throw new IllegalArgumentException("El número de orden no puede estar vacío");
        this.numeroOrden = numeroOrden.trim();
    }

    public void setEstadoFisico(EstadoFisicoCompra estadoFisico) {
        if (estadoFisico == null) throw new IllegalArgumentException("El estado físico no puede ser nulo");
        this.estadoFisico = estadoFisico;
    }

    public void setEstadoFinanciero(EstadoFinanciero estadoFinanciero) {
        if (estadoFinanciero == null) throw new IllegalArgumentException("El estado financiero no puede ser nulo");
        this.estadoFinanciero = estadoFinanciero;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        if (montoTotal == null) throw new IllegalArgumentException("El monto total no puede ser nulo");
        if (montoTotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El monto total no puede ser negativo");
        this.montoTotal = montoTotal;
    }

    @Override
    public String toString() { return numeroOrden; }
}
