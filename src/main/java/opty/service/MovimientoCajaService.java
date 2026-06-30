package opty.service;

import opty.model.entity.MovimientoCaja;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoCajaService {

    Optional<MovimientoCaja> findById(Integer id);

    List<MovimientoCaja> findAll();

    MovimientoCaja save(MovimientoCaja movimientoCaja);

    List<MovimientoCaja> findByDateRange(LocalDateTime desde, LocalDateTime hasta);

    List<MovimientoCaja> findByTiendaId(Integer tiendaId);

    List<MovimientoCaja> findByVentaId(Integer ventaId);

    List<MovimientoCaja> findByCompraId(Integer compraId);

    BigDecimal sumIngresosByDateRange(LocalDateTime desde, LocalDateTime hasta, Integer tiendaId);

    BigDecimal sumEgresosByDateRange(LocalDateTime desde, LocalDateTime hasta, Integer tiendaId);
}
