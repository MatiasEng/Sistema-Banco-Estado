package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sucursal implements Serializable {

    private String codigo;
    private String direccion;
    private List<Empleado> empleados;

    public Sucursal(String codigo, String direccion) {
        this.codigo = codigo;
        this.direccion = direccion;
        this.empleados = new ArrayList<>();
    }

    public void agregarEmpleado(Empleado e) {
        empleados.add(e);
    }

    public List<Empleado> getEmpleados() {
        return empleados;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDireccion() {
        return direccion;
    }
}
