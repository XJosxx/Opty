package opty.repository;

import opty.model.entity.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends CrudRepository<Proveedor, Integer> {

    Optional<Proveedor> findByRuc(String ruc);

    List<Proveedor> findActivos();

    List<Proveedor> findByTiendaId(Integer tiendaId);
}
