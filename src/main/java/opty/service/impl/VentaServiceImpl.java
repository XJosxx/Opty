package opty.service.impl;

import opty.model.entity.VentaCabecera;
import opty.model.entity.VentaDetalle;
import opty.repository.VentaRepository;
import opty.repository.VentaRepositoryImpl;
import opty.service.VentaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;

    public VentaServiceImpl() {
        this.ventaRepository = new VentaRepositoryImpl();
    }

    public VentaServiceImpl(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Override
    public Optional<VentaCabecera> findById(Integer id) {
        return ventaRepository.findById(id);
    }

    @Override
    public List<VentaCabecera> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public VentaCabecera save(VentaCabecera venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public void update(VentaCabecera venta) {
        ventaRepository.update(venta);
    }

    @Override
    public void delete(Integer id) {
        ventaRepository.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return ventaRepository.existsById(id);
    }

    @Override
    public long count() {
        return ventaRepository.count();
    }

    @Override
    public VentaCabecera processSale(Integer pacienteId, Integer usuarioId, Integer tiendaId,
                                      Integer productoId, Integer cantidad, String metodoPago) {
        return ventaRepository.processSale(pacienteId, usuarioId, tiendaId, productoId, cantidad, metodoPago);
    }

    @Override
    public Optional<VentaCabecera> findByNumeroTicket(String numeroTicket) {
        return ventaRepository.findByNumeroTicket(numeroTicket);
    }

    @Override
    public List<VentaCabecera> findByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        return ventaRepository.findByDateRange(desde, hasta);
    }

    @Override
    public List<VentaCabecera> findByPacienteId(Integer pacienteId) {
        return ventaRepository.findByPacienteId(pacienteId);
    }

    @Override
    public List<VentaCabecera> findByTiendaId(Integer tiendaId) {
        return ventaRepository.findByTiendaId(tiendaId);
    }

    @Override
    public List<VentaCabecera> findByUsuarioId(Integer usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<VentaDetalle> findDetallesByVentaId(Integer ventaId) {
        return ventaRepository.findDetallesByVentaId(ventaId);
    }

    @Override
    public BigDecimal sumVentasByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        return ventaRepository.sumVentasByDateRange(desde, hasta);
    }
}
