package opty.repository;

import opty.model.entity.MovimientoCaja;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoCajaRepository extends CrudRepository<MovimientoCaja, Integer> {

    List<MovimientoCaja> findByDateRange(LocalDateTime desde, LocalDateTime hasta);

    List<MovimientoCaja> findByTiendaId(Integer tiendaId);

    List<MovimientoCaja> findByVentaId(Integer ventaId);

    List<MovimientoCaja> findByCompraId(Integer compraId);

    BigDecimal sumIngresosByDateRange(LocalDateTime desde, LocalDateTime hasta, Integer tiendaId);

    BigDecimal sumEgresosByDateRange(LocalDateTime desde, LocalDateTime hasta, Integer tiendaId);
}
