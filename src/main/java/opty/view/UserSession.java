package opty.view;

import opty.model.entity.Usuario;

public class UserSession {

    private static UserSession instance;
    private Usuario usuario;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isLoggedIn() {
        return usuario != null;
    }

    public void cleanSession() {
        this.usuario = null;
    }
}
