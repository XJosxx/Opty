package opty.service.impl;

import opty.model.entity.Usuario;
import opty.model.enums.Rol;
import opty.repository.UsuarioRepository;
import opty.repository.implementacion.UsuarioRepositoryImpl;
import opty.service.UsuarioService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl() {
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$") && !usuario.getPassword().startsWith("$2b$")) {
            usuario.setPassword(BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt()));
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public void update(Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$") && !usuario.getPassword().startsWith("$2b$")) {
            usuario.setPassword(BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt()));
        }
        usuarioRepository.update(usuario);
    }

    @Override
    public void delete(Integer id) {
        usuarioRepository.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    public long count() {
        return usuarioRepository.count();
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public List<Usuario> findByRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Override
    public List<Usuario> findByTiendaId(Integer tiendaId) {
        return usuarioRepository.findByTiendaId(tiendaId);
    }

    @Override
    public Optional<Usuario> findByNumDocumento(String numDocumento) {
        return usuarioRepository.findByNumDocumento(numDocumento);
    }

    @Override
    public List<Usuario> findActivos() {
        return usuarioRepository.findActivos();
    }

    @Override
    public void actualizarUltimoAcceso(Integer usuarioId) {
        usuarioRepository.actualizarUltimoAcceso(usuarioId);
    }
}

