package opty.service.impl;

import opty.model.entity.Usuario;
import opty.model.enums.Rol;
import opty.repository.UsuarioRepository;
import opty.repository.UsuarioRepositoryImpl;
import opty.service.LoginService;

import java.util.Optional;

public class LoginServiceImpl implements LoginService {

    private final UsuarioRepository usuarioRepository;

    public LoginServiceImpl() {
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    public LoginServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Usuario> login(String username, String password) {
        if (username == null || username.isBlank()) return Optional.empty();
        if (password == null || password.isBlank()) return Optional.empty();

        var optUsuario = usuarioRepository.findByUsername(username.trim());
        if (optUsuario.isEmpty()) return Optional.empty();

        var usuario = optUsuario.get();
        if (Boolean.FALSE.equals(usuario.getActivo())) return Optional.empty();

        if (!usuario.getPassword().equals(password)) return Optional.empty();

        return optUsuario;
    }

    @Override
    public boolean checkAccess(Usuario usuario, Rol... rolesPermitidos) {
        if (usuario == null || rolesPermitidos == null || rolesPermitidos.length == 0) return false;
        if (Boolean.FALSE.equals(usuario.getActivo())) return false;

        for (var rol : rolesPermitidos) {
            if (usuario.getRol() == rol) return true;
        }
        return false;
    }

    @Override
    public void updateLastAccess(Integer usuarioId) {
        usuarioRepository.actualizarUltimoAcceso(usuarioId);
    }
}
