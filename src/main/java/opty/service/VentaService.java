package opty.service;

import opty.model.entity.VentaCabecera;
import opty.model.entity.VentaDetalle;
import opty.model.enums.TipoComprobante;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaService {

    Optional<VentaCabecera> findById(Integer id);

    List<VentaCabecera> findAll();

    VentaCabecera save(VentaCabecera venta);

    void update(VentaCabecera venta);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();

    VentaCabecera processSale(Integer pacienteId, Integer usuarioId, Integer tiendaId,
                              TipoComprobante tipoComprobante, Integer productoId, Integer cantidad, String metodoPago);

    Optional<VentaCabecera> findByNumeroTicket(String numeroTicket);

    List<VentaCabecera> findByDateRange(LocalDateTime desde, LocalDateTime hasta);

    List<VentaCabecera> findByPacienteId(Integer pacienteId);

    List<VentaCabecera> findByTiendaId(Integer tiendaId);

    List<VentaCabecera> findByUsuarioId(Integer usuarioId);

    List<VentaDetalle> findDetallesByVentaId(Integer ventaId);

    BigDecimal sumVentasByDateRange(LocalDateTime desde, LocalDateTime hasta);

    VentaCabecera registrarVentaMultiproducto(Integer pacienteId, Integer usuarioId, Integer tiendaId,
                                              TipoComprobante tipoComprobante, List<VentaDetalle> detalles, String metodoPago);
}
