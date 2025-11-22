package modelo;

import java.io.Serializable;

public class Tarjeta implements Serializable {

    private String numeroTarjeta;
    private String tipo;

    public Tarjeta(String numero, String tipo) {
        this.numeroTarjeta = numero;
        this.tipo = tipo;
    }

    public String getNumeroTarjeta() { return numeroTarjeta; }
}
