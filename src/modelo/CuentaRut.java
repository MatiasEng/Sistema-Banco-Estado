package modelo;

public class CuentaRut extends Cuenta {

    private boolean activa = true;

    public CuentaRut(String numeroCuenta) {
        super(numeroCuenta);
    }

    public void bloquearCuenta() {
        activa = false;
    }

    public boolean isActiva() {
        return activa;
    }
}
