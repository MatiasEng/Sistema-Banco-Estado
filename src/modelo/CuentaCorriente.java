package modelo;

public class CuentaCorriente extends Cuenta {

    private double lineaCredito;

    public CuentaCorriente(String numeroCuenta, double lineaCredito) {
        super(numeroCuenta);
        this.lineaCredito = lineaCredito;
    }

    public boolean usarLineaCredito(double monto) {
        if (monto <= lineaCredito) {
            lineaCredito -= monto;
            saldo += monto;
            transacciones.add(new Transaccion("LINEA_CREDITO", monto));
            return true;
        }
        return false;
    }

    public double getLineaCredito() { return lineaCredito; }
}
