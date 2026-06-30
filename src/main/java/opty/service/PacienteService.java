package opty.service;

import opty.model.entity.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteService {

    Optional<Paciente> findById(Integer id);

    List<Paciente> findAll();

    Paciente save(Paciente paciente);

    void update(Paciente paciente);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();

    Optional<Paciente> findByNumDocumento(String numDocumento);

    List<Paciente> searchByNombre(String query);

    List<Paciente> findByTiendaId(Integer tiendaId);

    List<Paciente> findDestacados();

    long countByTiendaId(Integer tiendaId);
}
