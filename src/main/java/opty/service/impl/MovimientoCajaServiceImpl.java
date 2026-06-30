package opty.service.impl;

import opty.model.entity.MovimientoCaja;
import opty.repository.MovimientoCajaRepository;
import opty.repository.MovimientoCajaRepositoryImpl;
import opty.service.MovimientoCajaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MovimientoCajaServiceImpl implements MovimientoCajaService {

    private final MovimientoCajaRepository movimientoCajaRepository;

    public MovimientoCajaServiceImpl() {
        this.movimientoCajaRepository = new MovimientoCajaRepositoryImpl();
    }

    public MovimientoCajaServiceImpl(MovimientoCajaRepository movimientoCajaRepository) {
        this.movimientoCajaRepository = movimientoCajaRepository;
    }

    @Override
    public Optional<MovimientoCaja> findById(Integer id) {
        return movimientoCajaRepository.findById(id);
    }

    @Override
    public List<MovimientoCaja> findAll() {
        return movimientoCajaRepository.findAll();
    }

    @Override
    public MovimientoCaja save(MovimientoCaja movimientoCaja) {
        return movimientoCajaRepository.save(movimientoCaja);
    }

    @Override
    public List<MovimientoCaja> findByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        return movimientoCajaRepository.findByDateRange(desde, hasta);
    }

    @Override
    public List<MovimientoCaja> findByTiendaId(Integer tiendaId) {
        return movimientoCajaRepository.findByTiendaId(tiendaId);
    }

    @Override
    public List<MovimientoCaja> findByVentaId(Integer ventaId) {
        return movimientoCajaRepository.findByVentaId(ventaId);
    }

    @Override
    public List<MovimientoCaja> findByCompraId(Integer compraId) {
        return movimientoCajaRepository.findByCompraId(compraId);
    }

    @Override
    public BigDecimal sumIngresosByDateRange(LocalDateTime desde, LocalDateTime hasta, Integer tiendaId) {
        return movimientoCajaRepository.sumIngresosByDateRange(desde, hasta, tiendaId);
    }

    @Override
    public BigDecimal sumEgresosByDateRange(LocalDateTime desde, LocalDateTime hasta, Integer tiendaId) {
        return movimientoCajaRepository.sumEgresosByDateRange(desde, hasta, tiendaId);
    }
}
