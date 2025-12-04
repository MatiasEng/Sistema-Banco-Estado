package modelo;

public class CuentaAhorro extends Cuenta {

    private double tasaInteres;

    public CuentaAhorro(String numeroCuenta, double tasaInteres) {
        super(numeroCuenta);
        this.tasaInteres = tasaInteres;
    }

    public double calcularInteres() {
        return saldo * tasaInteres;
    }

    public double getTasaInteres() {
        return tasaInteres;
    }
}
