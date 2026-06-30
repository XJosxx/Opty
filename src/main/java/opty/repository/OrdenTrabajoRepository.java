package opty.repository;

import opty.model.entity.OrdenTrabajo;
import opty.model.enums.EstadoFisicoOT;

import java.util.List;
import java.util.Optional;

public interface OrdenTrabajoRepository extends CrudRepository<OrdenTrabajo, Integer> {

    List<OrdenTrabajo> findPendientes();

    List<OrdenTrabajo> findByVentaId(Integer ventaId);

    List<OrdenTrabajo> findByEstado(EstadoFisicoOT estado);

    List<OrdenTrabajo> findVencidas();

    void actualizarEstado(Integer ordenId, EstadoFisicoOT nuevoEstado);
}
