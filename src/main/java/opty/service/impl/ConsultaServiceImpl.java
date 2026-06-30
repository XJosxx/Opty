package opty.service.impl;

import opty.model.entity.Consulta;
import opty.model.entity.HistorialClinico;
import opty.repository.ConsultaRepository;
import opty.repository.ConsultaRepositoryImpl;
import opty.service.ConsultaService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ConsultaServiceImpl implements ConsultaService {

    private final ConsultaRepository consultaRepository;

    public ConsultaServiceImpl() {
        this.consultaRepository = new ConsultaRepositoryImpl();
    }

    public ConsultaServiceImpl(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    @Override
    public Optional<Consulta> findById(Integer id) {
        return consultaRepository.findById(id);
    }

    @Override
    public List<Consulta> findAll() {
        return consultaRepository.findAll();
    }

    @Override
    public Consulta save(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    @Override
    public void update(Consulta consulta) {
        consultaRepository.update(consulta);
    }

    @Override
    public void delete(Integer id) {
        consultaRepository.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return consultaRepository.existsById(id);
    }

    @Override
    public long count() {
        return consultaRepository.count();
    }

    @Override
    public Consulta registrarConsultaConVenta(Integer pacienteId, Integer usuarioId, Integer tiendaId,
                                               String motivo, Integer productoId, BigDecimal precioServicio,
                                               String tipoComprobante) {
        return consultaRepository.registrarConsultaConVenta(pacienteId, usuarioId, tiendaId,
                motivo, productoId, precioServicio, tipoComprobante);
    }

    @Override
    public List<Consulta> findByPacienteId(Integer pacienteId) {
        return consultaRepository.findByPacienteId(pacienteId);
    }

    @Override
    public List<Consulta> findByUsuarioId(Integer usuarioId) {
        return consultaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Consulta> findByTiendaId(Integer tiendaId) {
        return consultaRepository.findByTiendaId(tiendaId);
    }

    @Override
    public HistorialClinico findHistorialByConsultaId(Integer consultaId) {
        return consultaRepository.findHistorialByConsultaId(consultaId);
    }

    @Override
    public void saveHistorialClinico(HistorialClinico historial) {
        consultaRepository.saveHistorialClinico(historial);
    }
}
