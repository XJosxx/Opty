package opty.service;

import opty.model.entity.Consulta;
import opty.model.entity.HistorialClinico;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ConsultaService {

    Optional<Consulta> findById(Integer id);

    List<Consulta> findAll();

    Consulta save(Consulta consulta);

    void update(Consulta consulta);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();

    Consulta registrarConsultaConVenta(Integer pacienteId, Integer usuarioId, Integer tiendaId,
                                       String motivo, Integer productoId, BigDecimal precioServicio,
                                       String tipoComprobante);

    List<Consulta> findByPacienteId(Integer pacienteId);

    List<Consulta> findByUsuarioId(Integer usuarioId);

    List<Consulta> findByTiendaId(Integer tiendaId);

    HistorialClinico findHistorialByConsultaId(Integer consultaId);

    void saveHistorialClinico(HistorialClinico historial);

    HistorialClinico findHistorialById(Integer id);
}
