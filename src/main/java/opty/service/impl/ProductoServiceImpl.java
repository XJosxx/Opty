package opty.service.impl;

import opty.model.entity.Producto;
import opty.model.enums.CategoriaProducto;
import opty.repository.ProductoRepository;
import opty.repository.implementacion.ProductoRepositoryImpl;
import opty.service.ProductoService;

import java.util.List;
import java.util.Optional;

public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl() {
        this.productoRepository = new ProductoRepositoryImpl();
    }

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Optional<Producto> findById(Integer id) {
        return productoRepository.findById(id);
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void update(Producto producto) {
        productoRepository.update(producto);
    }

    @Override
    public void delete(Integer id) {
        productoRepository.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return productoRepository.existsById(id);
    }

    @Override
    public long count() {
        return productoRepository.count();
    }

    @Override
    public Optional<Producto> findByCodigoAndTienda(String codigo, Integer tiendaId) {
        return productoRepository.findByCodigoAndTienda(codigo, tiendaId);
    }

    @Override
    public List<Producto> findByCategoria(CategoriaProducto categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    @Override
    public List<Producto> findByTiendaId(Integer tiendaId) {
        return productoRepository.findByTiendaId(tiendaId);
    }

    @Override
    public List<Producto> findDisponibles(Integer tiendaId) {
        return productoRepository.findDisponibles(tiendaId);
    }

    @Override
    public List<Producto> findActivos() {
        return productoRepository.findActivos();
    }
}

