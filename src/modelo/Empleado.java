package modelo;

import java.io.Serializable;

public class Empleado implements Serializable {

    private String nombre;
    private String rut;

    public Empleado(String nombre, String rut) {
        this.nombre = nombre;
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRut() {
        return rut;
    }

}

