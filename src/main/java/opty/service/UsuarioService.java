package opty.service;

import opty.model.entity.Usuario;
import opty.model.enums.Rol;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    Optional<Usuario> findById(Integer id);

    List<Usuario> findAll();

    Usuario save(Usuario usuario);

    void update(Usuario usuario);

    void delete(Integer id);

    boolean existsById(Integer id);

    long count();

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByTiendaId(Integer tiendaId);

    Optional<Usuario> findByNumDocumento(String numDocumento);

    List<Usuario> findActivos();

    void actualizarUltimoAcceso(Integer usuarioId);
}
