package opty.repository;

import opty.model.entity.VentaCabecera;
import opty.model.entity.VentaDetalle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends CrudRepository<VentaCabecera, Integer> {

    Optional<VentaCabecera> findByNumeroTicket(String numeroTicket);

    List<VentaCabecera> findByDateRange(LocalDateTime desde, LocalDateTime hasta);

    List<VentaCabecera> findByPacienteId(Integer pacienteId);

    List<VentaCabecera> findByTiendaId(Integer tiendaId);

    List<VentaCabecera> findByUsuarioId(Integer usuarioId);

    VentaCabecera processSale(Integer pacienteId, Integer usuarioId, Integer tiendaId,
                              Integer productoId, Integer cantidad, String metodoPago);

    List<VentaDetalle> findDetallesByVentaId(Integer ventaId);

    BigDecimal sumVentasByDateRange(LocalDateTime desde, LocalDateTime hasta);
}
