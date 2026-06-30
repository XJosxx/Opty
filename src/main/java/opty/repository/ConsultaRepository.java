package opty.repository;

import opty.model.entity.Consulta;
import opty.model.entity.HistorialClinico;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ConsultaRepository extends CrudRepository<Consulta, Integer> {

    List<Consulta> findByPacienteId(Integer pacienteId);

    List<Consulta> findByUsuarioId(Integer usuarioId);

    List<Consulta> findByTiendaId(Integer tiendaId);

    Consulta registrarConsultaConVenta(Integer pacienteId, Integer usuarioId, Integer tiendaId,
                                        String motivo, Integer productoId, BigDecimal precioServicio,
                                        String tipoComprobante);

    HistorialClinico findHistorialByConsultaId(Integer consultaId);

    void saveHistorialClinico(HistorialClinico historial);
}
