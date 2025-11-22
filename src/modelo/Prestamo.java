package modelo;

import java.io.Serializable;

public class Prestamo implements Serializable {

    private String idPrestamo;
    private double monto;
    private int cuotas;

    public Prestamo(String idPrestamo, double monto, int cuotas) {
        this.idPrestamo = idPrestamo;
        this.monto = monto;
        this.cuotas = cuotas;
    }
}
