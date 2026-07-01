package opty.service.impl;

import opty.model.entity.Kardex;
import opty.repository.KardexRepository;
import opty.repository.implementacion.KardexRepositoryImpl;
import opty.service.KardexService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class KardexServiceImpl implements KardexService {

    private final KardexRepository kardexRepository;

    public KardexServiceImpl() {
        this.kardexRepository = new KardexRepositoryImpl();
    }

    public KardexServiceImpl(KardexRepository kardexRepository) {
        this.kardexRepository = kardexRepository;
    }

    @Override
    public Optional<Kardex> findById(Integer id) {
        return kardexRepository.findById(id);
    }

    @Override
    public List<Kardex> findAll() {
        return kardexRepository.findAll();
    }

    @Override
    public Kardex save(Kardex kardex) {
        return kardexRepository.save(kardex);
    }

    @Override
    public List<Kardex> findByProductoId(Integer productoId) {
        return kardexRepository.findByProductoId(productoId);
    }

    @Override
    public List<Kardex> findByInsumoId(Integer insumoId) {
        return kardexRepository.findByInsumoId(insumoId);
    }

    @Override
    public List<Kardex> findByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        return kardexRepository.findByDateRange(desde, hasta);
    }

    @Override
    public List<Kardex> findByTiendaId(Integer tiendaId) {
        return kardexRepository.findByTiendaId(tiendaId);
    }

    @Override
    public List<Kardex> findByTipoMovimiento(String tipoMovimiento) {
        return kardexRepository.findByTipoMovimiento(tipoMovimiento);
    }
}

