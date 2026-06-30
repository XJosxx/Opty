package opty.service.impl;

import opty.model.entity.Insumo;
import opty.model.enums.CategoriaInsumo;
import opty.repository.InsumoRepository;
import opty.repository.InsumoRepositoryImpl;
import opty.service.InsumoService;

import java.util.List;
import java.util.Optional;

public class InsumoServiceImpl implements InsumoService {

    private final InsumoRepository insumoRepository;

    public InsumoServiceImpl() {
        this.insumoRepository = new InsumoRepositoryImpl();
    }

    public InsumoServiceImpl(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @Override
    public Optional<Insumo> findById(Integer id) {
        return insumoRepository.findById(id);
    }

    @Override
    public List<Insumo> findAll() {
        return insumoRepository.findAll();
    }

    @Override
    public Insumo save(Insumo insumo) {
        return insumoRepository.save(insumo);
    }

    @Override
    public void update(Insumo insumo) {
        insumoRepository.update(insumo);
    }

    @Override
    public void delete(Integer id) {
        insumoRepository.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return insumoRepository.existsById(id);
    }

    @Override
    public long count() {
        return insumoRepository.count();
    }

    @Override
    public Optional<Insumo> findByCodigoAndTienda(String codigo, Integer tiendaId) {
        return insumoRepository.findByCodigoAndTienda(codigo, tiendaId);
    }

    @Override
    public List<Insumo> findByCategoria(CategoriaInsumo categoria) {
        return insumoRepository.findByCategoria(categoria);
    }

    @Override
    public List<Insumo> findByTiendaId(Integer tiendaId) {
        return insumoRepository.findByTiendaId(tiendaId);
    }

    @Override
    public List<Insumo> findBajoStock(Integer tiendaId) {
        return insumoRepository.findBajoStock(tiendaId);
    }

    @Override
    public List<Insumo> findActivos() {
        return insumoRepository.findActivos();
    }
}
