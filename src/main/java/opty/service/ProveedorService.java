package opty.service;

import opty.model.entity.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProveedorService {

    Optional<Proveedor> findById(Integer id);

    List<Proveedor> findAll();

    Proveedor save(Proveedor proveedor);

    void update(Proveedor proveedor);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();

    Optional<Proveedor> findByRuc(String ruc);

    List<Proveedor> findActivos();

    List<Proveedor> findByTiendaId(Integer tiendaId);
}
