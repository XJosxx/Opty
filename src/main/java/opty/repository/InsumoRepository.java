package opty.repository;

import opty.model.entity.Insumo;
import opty.model.enums.CategoriaInsumo;

import java.util.List;
import java.util.Optional;

public interface InsumoRepository extends CrudRepository<Insumo, Integer> {

    Optional<Insumo> findByCodigoAndTienda(String codigo, Integer tiendaId);

    List<Insumo> findByCategoria(CategoriaInsumo categoria);

    List<Insumo> findByTiendaId(Integer tiendaId);

    List<Insumo> findBajoStock(Integer tiendaId);

    List<Insumo> findActivos();
}
