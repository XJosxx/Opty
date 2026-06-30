package opty.service;

import opty.model.entity.Producto;
import opty.model.enums.CategoriaProducto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {

    Optional<Producto> findById(Integer id);

    List<Producto> findAll();

    Producto save(Producto producto);

    void update(Producto producto);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();

    Optional<Producto> findByCodigoAndTienda(String codigo, Integer tiendaId);

    List<Producto> findByCategoria(CategoriaProducto categoria);

    List<Producto> findByTiendaId(Integer tiendaId);

    List<Producto> findDisponibles(Integer tiendaId);

    List<Producto> findActivos();
}
