package opty.model.entity;

import opty.model.enums.MetodoPago;
import opty.model.enums.TipoMovimientoCaja;
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
public class MovimientoCaja {

    @EqualsAndHashCode.Include
    private Integer id;
    private Integer tiendaId;
    private Integer usuarioId;
    private Integer ventaId;
    private Integer compraId;
    private TipoMovimientoCaja tipo;
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fecha;

    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public void setUsuarioId(Integer usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("El usuario no puede ser nulo");
        this.usuarioId = usuarioId;
    }

    public void setTipo(TipoMovimientoCaja tipo) {
        if (tipo == null) throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo");
        this.tipo = tipo;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        if (metodoPago == null) throw new IllegalArgumentException("El método de pago no puede ser nulo");
        this.metodoPago = metodoPago;
    }

    public void setMonto(BigDecimal monto) {
        if (monto == null) throw new IllegalArgumentException("El monto no puede ser nulo");
        if (monto.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("El monto debe ser mayor a cero");
        this.monto = monto;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion.trim() : null;
    }

    @Override
    public String toString() { return tipo + " S/ " + monto + " - " + descripcion; }
}
