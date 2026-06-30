package opty.service;

import opty.model.entity.ConfigTienda;

import java.util.List;
import java.util.Optional;

public interface ConfigTiendaService {

    Optional<ConfigTienda> findById(Integer id);

    List<ConfigTienda> findAll();

    ConfigTienda save(ConfigTienda configTienda);

    void update(ConfigTienda configTienda);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();

    Optional<ConfigTienda> findByCodigo(String codigo);

    List<ConfigTienda> findByRuc(String ruc);
}
