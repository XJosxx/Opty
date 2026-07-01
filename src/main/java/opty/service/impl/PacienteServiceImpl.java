package opty.service.impl;

import opty.model.entity.Paciente;
import opty.repository.PacienteRepository;
import opty.repository.implementacion.PacienteRepositoryImpl;
import opty.service.PacienteService;

import java.util.List;
import java.util.Optional;

public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl() {
        this.pacienteRepository = new PacienteRepositoryImpl();
    }

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Optional<Paciente> findById(Integer id) {
        return pacienteRepository.findById(id);
    }

    @Override
    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    @Override
    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    @Override
    public void update(Paciente paciente) {
        pacienteRepository.update(paciente);
    }

    @Override
    public void delete(Integer id) {
        pacienteRepository.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return pacienteRepository.existsById(id);
    }

    @Override
    public long count() {
        return pacienteRepository.count();
    }

    @Override
    public Optional<Paciente> findByNumDocumento(String numDocumento) {
        return pacienteRepository.findByNumDocumento(numDocumento);
    }

    @Override
    public List<Paciente> searchByNombre(String query) {
        return pacienteRepository.searchByNombre(query);
    }

    @Override
    public List<Paciente> findByTiendaId(Integer tiendaId) {
        return pacienteRepository.findByTiendaId(tiendaId);
    }

    @Override
    public List<Paciente> findDestacados() {
        return pacienteRepository.findDestacados();
    }

    @Override
    public long countByTiendaId(Integer tiendaId) {
        return pacienteRepository.countByTiendaId(tiendaId);
    }
}

