package opty.service;

import opty.model.entity.Usuario;
import opty.model.enums.Rol;

import java.util.Optional;

public interface LoginService {

    Optional<Usuario> login(String username, String password);

    boolean checkAccess(Usuario usuario, Rol... rolesPermitidos);

    void updateLastAccess(Integer usuarioId);
}
