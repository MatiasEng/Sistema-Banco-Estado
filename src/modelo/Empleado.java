package modelo;

import java.io.Serializable;

public class Empleado implements Serializable {

    private String nombre;
    private String rut;
    private String cargo;

    public Empleado(String nombre, String rut, String cargo) {
        this.nombre = nombre;
        this.rut = rut;
        this.cargo = cargo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRut() {
        return rut;
    }

    public String getCargo() {
        return cargo;
    }
}

