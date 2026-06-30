package opty.repository;

import opty.model.entity.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends CrudRepository<Paciente, Integer> {

    Optional<Paciente> findByNumDocumento(String numDocumento);

    List<Paciente> searchByNombre(String query);

    List<Paciente> findByTiendaId(Integer tiendaId);

    List<Paciente> findDestacados();

    long countByTiendaId(Integer tiendaId);
}
