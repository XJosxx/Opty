package opty.service;

import opty.model.entity.OrdenTrabajo;
import opty.model.enums.EstadoFisicoOT;

import java.util.List;
import java.util.Optional;

public interface OrdenTrabajoService {

    Optional<OrdenTrabajo> findById(Integer id);

    List<OrdenTrabajo> findAll();

    OrdenTrabajo save(OrdenTrabajo ordenTrabajo);

    void update(OrdenTrabajo ordenTrabajo);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();

    List<OrdenTrabajo> findPendientes();

    List<OrdenTrabajo> findByVentaId(Integer ventaId);

    List<OrdenTrabajo> findByEstado(EstadoFisicoOT estado);

    List<OrdenTrabajo> findVencidas();

    void actualizarEstado(Integer ordenId, EstadoFisicoOT nuevoEstado);
}
