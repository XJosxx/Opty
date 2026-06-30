package opty.service.impl;

import opty.model.entity.Proveedor;
import opty.repository.ProveedorRepository;
import opty.repository.ProveedorRepositoryImpl;
import opty.service.ProveedorService;

import java.util.List;
import java.util.Optional;

public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl() {
        this.proveedorRepository = new ProveedorRepositoryImpl();
    }

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public Optional<Proveedor> findById(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Override
    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    @Override
    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Override
    public void update(Proveedor proveedor) {
        proveedorRepository.update(proveedor);
    }

    @Override
    public void delete(Integer id) {
        proveedorRepository.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return proveedorRepository.existsById(id);
    }

    @Override
    public long count() {
        return proveedorRepository.count();
    }

    @Override
    public Optional<Proveedor> findByRuc(String ruc) {
        return proveedorRepository.findByRuc(ruc);
    }

    @Override
    public List<Proveedor> findActivos() {
        return proveedorRepository.findActivos();
    }

    @Override
    public List<Proveedor> findByTiendaId(Integer tiendaId) {
        return proveedorRepository.findByTiendaId(tiendaId);
    }
}
