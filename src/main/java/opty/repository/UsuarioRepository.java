package opty.repository;

import opty.model.entity.Usuario;
import opty.model.enums.Rol;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByTiendaId(Integer tiendaId);

    Optional<Usuario> findByNumDocumento(String numDocumento);

    List<Usuario> findActivos();

    void actualizarUltimoAcceso(Integer usuarioId);
}
