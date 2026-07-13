package opty.service.impl;

import opty.model.entity.CompraCabecera;
import opty.model.entity.CompraDetalle;
import opty.repository.CompraRepository;
import opty.repository.implementacion.CompraRepositoryImpl;
import opty.service.CompraService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;

    public CompraServiceImpl() {
        this.compraRepository = new CompraRepositoryImpl();
    }

    public CompraServiceImpl(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    @Override
    public Optional<CompraCabecera> findById(Integer id) {
        return compraRepository.findById(id);
    }

    @Override
    public List<CompraCabecera> findAll() {
        return compraRepository.findAll();
    }

    @Override
    public CompraCabecera save(CompraCabecera compra) {
        return compraRepository.save(compra);
    }

    @Override
    public void update(CompraCabecera compra) {
        compraRepository.update(compra);
    }

    @Override
    public void delete(Integer id) {
        compraRepository.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return compraRepository.existsById(id);
    }

    @Override
    public long count() {
        return compraRepository.count();
    }

    @Override
    public CompraCabecera processPurchase(Integer proveedorId, Integer usuarioId, Integer tiendaId,
                                           Integer insumoId, Integer cantidad, BigDecimal precioUnit,
                                           String metodoPago) {
        return compraRepository.processPurchase(proveedorId, usuarioId, tiendaId, insumoId,
                cantidad, precioUnit, metodoPago);
    }

    @Override
    public Optional<CompraCabecera> findByNumeroOrden(String numeroOrden) {
        return compraRepository.findByNumeroOrden(numeroOrden);
    }

    @Override
    public List<CompraCabecera> findByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        return compraRepository.findByDateRange(desde, hasta);
    }

    @Override
    public List<CompraCabecera> findByProveedorId(Integer proveedorId) {
        return compraRepository.findByProveedorId(proveedorId);
    }

    @Override
    public List<CompraCabecera> findByTiendaId(Integer tiendaId) {
        return compraRepository.findByTiendaId(tiendaId);
    }

    @Override
    public List<CompraDetalle> findDetallesByCompraId(Integer compraId) {
        return compraRepository.findDetallesByCompraId(compraId);
    }

    @Override
    public BigDecimal sumComprasByDateRange(LocalDateTime desde, LocalDateTime hasta) {
        return compraRepository.sumComprasByDateRange(desde, hasta);
    }

    @Override
    public CompraCabecera registrarCompraMultiproducto(Integer proveedorId, Integer usuarioId, Integer tiendaId,
                                                       List<CompraDetalle> detalles, String metodoPago, BigDecimal montoPagado) {
        return compraRepository.registrarCompraMultiproducto(proveedorId, usuarioId, tiendaId, detalles, metodoPago, montoPagado);
    }
}

