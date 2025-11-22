package controlador;

import java.io.Serializable;
public class ControladorBancoEstado implements Serializable{
    private static ControladorBancoEstado instance = null;

    public static ControladorBancoEstado getInstance() {
        if (instance == null) {
            instance = new ControladorBancoEstado();
        }
        return instance;
    }

}
