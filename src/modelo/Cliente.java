package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cliente implements Serializable {

    private String nombre;
    private String rut;
    private List<Cuenta> cuentas = new ArrayList<>();

    public Cliente(String nombre, String rut) {
        this.nombre = nombre;
        this.rut = rut;
    }

    public void agregarCuenta(Cuenta c) {
        cuentas.add(c);
    }

    public List<Cuenta> getCuentas() { return cuentas; }
    public String getNombre() { return nombre; }
    public String getRut() { return rut; }
}
