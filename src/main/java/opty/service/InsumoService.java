package opty.service;

import opty.model.entity.Insumo;
import opty.model.enums.CategoriaInsumo;

import java.util.List;
import java.util.Optional;

public interface InsumoService {

    Optional<Insumo> findById(Integer id);

    List<Insumo> findAll();

    Insumo save(Insumo insumo);

    void update(Insumo insumo);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();

    Optional<Insumo> findByCodigoAndTienda(String codigo, Integer tiendaId);

    List<Insumo> findByCategoria(CategoriaInsumo categoria);

    List<Insumo> findByTiendaId(Integer tiendaId);

    List<Insumo> findBajoStock(Integer tiendaId);

    List<Insumo> findActivos();
}
