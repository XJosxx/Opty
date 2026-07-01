package opty.model.entity;

import opty.model.enums.TipoMovimientoKardex;
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
public class Kardex {

    @EqualsAndHashCode.Include
    private Integer id;
    private Integer tiendaId;
    private Integer usuarioId;
    private Integer insumoId;
    private Integer productoId;
    private Integer compraId;
    private Integer ventaId;
    private TipoMovimientoKardex tipoMovimiento;
    private String motivo;
    private Integer cantidad;
    private Integer cantidadSaldo;
    private LocalDateTime fecha;

    public void setTiendaId(Integer tiendaId) {
        if (tiendaId == null) throw new IllegalArgumentException("La tienda asignada no puede ser nula");
        this.tiendaId = tiendaId;
    }

    public void setUsuarioId(Integer usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("El usuario no puede ser nulo");
        this.usuarioId = usuarioId;
    }

    public void setTipoMovimiento(TipoMovimientoKardex tipoMovimiento) {
        if (tipoMovimiento == null) throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo");
        this.tipoMovimiento = tipoMovimiento;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo != null ? motivo.trim() : null;
    }

    public void setCantidad(Integer cantidad) {
        if (cantidad == null) throw new IllegalArgumentException("La cantidad no puede ser nula");
        this.cantidad = cantidad;
    }

    public void setCantidadSaldo(Integer cantidadSaldo) {
        if (cantidadSaldo == null) throw new IllegalArgumentException("El saldo no puede ser nulo");
        if (cantidadSaldo < 0) throw new IllegalArgumentException("El saldo no puede ser negativo");
        this.cantidadSaldo = cantidadSaldo;
    }

    @Override
    public String toString() { return tipoMovimiento + " - " + motivo; }
}
