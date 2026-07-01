package opty.service;

import opty.model.entity.CompraCabecera;
import opty.model.entity.CompraDetalle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompraService {

    Optional<CompraCabecera> findById(Integer id);

    List<CompraCabecera> findAll();

    CompraCabecera save(CompraCabecera compra);

    void update(CompraCabecera compra);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();

    CompraCabecera processPurchase(Integer proveedorId, Integer usuarioId, Integer tiendaId,
                                   Integer insumoId, Integer cantidad, BigDecimal precioUnit,
                                   String metodoPago);

    Optional<CompraCabecera> findByNumeroOrden(String numeroOrden);

    List<CompraCabecera> findByDateRange(LocalDateTime desde, LocalDateTime hasta);

    List<CompraCabecera> findByProveedorId(Integer proveedorId);

    List<CompraCabecera> findByTiendaId(Integer tiendaId);

    List<CompraDetalle> findDetallesByCompraId(Integer compraId);

    BigDecimal sumComprasByDateRange(LocalDateTime desde, LocalDateTime hasta);

    CompraCabecera registrarCompraMultiproducto(Integer proveedorId, Integer usuarioId, Integer tiendaId,
                                                List<CompraDetalle> detalles, String metodoPago);
}
