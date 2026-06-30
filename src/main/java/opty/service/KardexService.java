package opty.service;

import opty.model.entity.Kardex;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface KardexService {

    Optional<Kardex> findById(Integer id);

    List<Kardex> findAll();

    Kardex save(Kardex kardex);

    List<Kardex> findByProductoId(Integer productoId);

    List<Kardex> findByInsumoId(Integer insumoId);

    List<Kardex> findByDateRange(LocalDateTime desde, LocalDateTime hasta);

    List<Kardex> findByTiendaId(Integer tiendaId);

    List<Kardex> findByTipoMovimiento(String tipoMovimiento);
}
