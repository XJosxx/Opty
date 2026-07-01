package opty.service.impl;

import opty.model.entity.OrdenTrabajo;
import opty.model.enums.EstadoFisicoOT;
import opty.repository.OrdenTrabajoRepository;
import opty.repository.implementacion.OrdenTrabajoRepositoryImpl;
import opty.service.OrdenTrabajoService;

import java.util.List;
import java.util.Optional;

public class OrdenTrabajoServiceImpl implements OrdenTrabajoService {

    private final OrdenTrabajoRepository ordenTrabajoRepository;

    public OrdenTrabajoServiceImpl() {
        this.ordenTrabajoRepository = new OrdenTrabajoRepositoryImpl();
    }

    public OrdenTrabajoServiceImpl(OrdenTrabajoRepository ordenTrabajoRepository) {
        this.ordenTrabajoRepository = ordenTrabajoRepository;
    }

    @Override
    public Optional<OrdenTrabajo> findById(Integer id) {
        return ordenTrabajoRepository.findById(id);
    }

    @Override
    public List<OrdenTrabajo> findAll() {
        return ordenTrabajoRepository.findAll();
    }

    @Override
    public OrdenTrabajo save(OrdenTrabajo ordenTrabajo) {
        return ordenTrabajoRepository.save(ordenTrabajo);
    }

    @Override
    public void update(OrdenTrabajo ordenTrabajo) {
        ordenTrabajoRepository.update(ordenTrabajo);
    }

    @Override
    public void delete(Integer id) {
        ordenTrabajoRepository.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return ordenTrabajoRepository.existsById(id);
    }

    @Override
    public long count() {
        return ordenTrabajoRepository.count();
    }

    @Override
    public List<OrdenTrabajo> findPendientes() {
        return ordenTrabajoRepository.findPendientes();
    }

    @Override
    public List<OrdenTrabajo> findByVentaId(Integer ventaId) {
        return ordenTrabajoRepository.findByVentaId(ventaId);
    }

    @Override
    public List<OrdenTrabajo> findByEstado(EstadoFisicoOT estado) {
        return ordenTrabajoRepository.findByEstado(estado);
    }

    @Override
    public List<OrdenTrabajo> findVencidas() {
        return ordenTrabajoRepository.findVencidas();
    }

    @Override
    public void actualizarEstado(Integer ordenId, EstadoFisicoOT nuevoEstado) {
        ordenTrabajoRepository.actualizarEstado(ordenId, nuevoEstado);
    }
}

