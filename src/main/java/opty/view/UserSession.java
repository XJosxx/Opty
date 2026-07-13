package opty.view;

import opty.model.entity.Paciente;
import opty.model.entity.Usuario;

public class UserSession {

    private static UserSession instance;
    private Usuario usuario;
    private Paciente pacienteSeleccionado;
    private MainController mainController;

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

    public Paciente getPacienteSeleccionado() {
        return pacienteSeleccionado;
    }

    public void setPacienteSeleccionado(Paciente pacienteSeleccionado) {
        this.pacienteSeleccionado = pacienteSeleccionado;
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public boolean isLoggedIn() {
        return usuario != null;
    }

    public void cleanSession() {
        this.usuario = null;
        this.pacienteSeleccionado = null;
        this.mainController = null;
    }
}
