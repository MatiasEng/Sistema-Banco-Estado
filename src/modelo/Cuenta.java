package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Cuenta implements Serializable {
    protected String numeroCuenta;
    protected double saldo;
    protected List<Transaccion> transacciones = new ArrayList<>();
    protected Tarjeta tarjeta;

    public Cuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
        this.saldo = 0.0;
    }

    public void depositar(double monto) {
        saldo += monto;
        transacciones.add(new Transaccion("DEP", monto));
    }

    public boolean retirar(double monto) {
        if (saldo >= monto) {
            saldo -= monto;
            transacciones.add(new Transaccion("RET", monto));
            return true;
        }
        return false;
    }

    public double getSaldo() { return saldo; }
    public String getNumeroCuenta() { return numeroCuenta; }
}