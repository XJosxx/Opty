package opty.repository;

import opty.model.entity.Producto;
import opty.model.enums.CategoriaProducto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends CrudRepository<Producto, Integer> {

    Optional<Producto> findByCodigoAndTienda(String codigo, Integer tiendaId);

    List<Producto> findByCategoria(CategoriaProducto categoria);

    List<Producto> findByTiendaId(Integer tiendaId);

    List<Producto> findDisponibles(Integer tiendaId);

    List<Producto> findActivos();
}
