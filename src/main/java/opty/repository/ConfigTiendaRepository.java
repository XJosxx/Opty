package opty.repository;

import opty.model.entity.ConfigTienda;

import java.util.List;
import java.util.Optional;

public interface ConfigTiendaRepository extends CrudRepository<ConfigTienda, Integer> {

    Optional<ConfigTienda> findByCodigo(String codigo);

    List<ConfigTienda> findByRuc(String ruc);
}
