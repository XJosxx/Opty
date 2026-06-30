package opty.repository;

import opty.model.entity.Kardex;

import java.time.LocalDateTime;
import java.util.List;

public interface KardexRepository extends CrudRepository<Kardex, Integer> {

    List<Kardex> findByProductoId(Integer productoId);

    List<Kardex> findByInsumoId(Integer insumoId);

    List<Kardex> findByDateRange(LocalDateTime desde, LocalDateTime hasta);

    List<Kardex> findByTiendaId(Integer tiendaId);

    List<Kardex> findByTipoMovimiento(String tipoMovimiento);
}
