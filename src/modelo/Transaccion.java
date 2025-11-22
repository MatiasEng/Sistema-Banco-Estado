package modelo;
import java.io.Serializable;
import java.util.Date;

public class Transaccion implements Serializable {

    private String tipo;
    private double monto;
    private Date fecha;

    public Transaccion(String tipo, double monto) {
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = new Date();
    }
}
