package opty.service.impl;

import opty.model.entity.ConfigTienda;
import opty.repository.ConfigTiendaRepository;
import opty.repository.implementacion.ConfigTiendaRepositoryImpl;
import opty.service.ConfigTiendaService;

import java.util.List;
import java.util.Optional;

public class ConfigTiendaServiceImpl implements ConfigTiendaService {

    private final ConfigTiendaRepository configTiendaRepository;

    public ConfigTiendaServiceImpl() {
        this.configTiendaRepository = new ConfigTiendaRepositoryImpl();
    }

    public ConfigTiendaServiceImpl(ConfigTiendaRepository configTiendaRepository) {
        this.configTiendaRepository = configTiendaRepository;
    }

    @Override
    public Optional<ConfigTienda> findById(Integer id) {
        return configTiendaRepository.findById(id);
    }

    @Override
    public List<ConfigTienda> findAll() {
        return configTiendaRepository.findAll();
    }

    @Override
    public ConfigTienda save(ConfigTienda configTienda) {
        return configTiendaRepository.save(configTienda);
    }

    @Override
    public void update(ConfigTienda configTienda) {
        configTiendaRepository.update(configTienda);
    }

    @Override
    public void delete(Integer id) {
        configTiendaRepository.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return configTiendaRepository.existsById(id);
    }

    @Override
    public long count() {
        return configTiendaRepository.count();
    }

    @Override
    public Optional<ConfigTienda> findByCodigo(String codigo) {
        return configTiendaRepository.findByCodigo(codigo);
    }

    @Override
    public List<ConfigTienda> findByRuc(String ruc) {
        return configTiendaRepository.findByRuc(ruc);
    }
}

