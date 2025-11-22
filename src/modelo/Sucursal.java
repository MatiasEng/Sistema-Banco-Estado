package modelo;
import java.io.Serializable;


public class Sucursal implements Serializable{
    private String codigo;
    private String direccion;

    public Sucursal(String codigo, String direccion) {
        this.codigo = codigo;
        this.direccion = direccion;

    }

    public boolean abrirCuenta(Cliente c, Cuenta cuenta) {

        return false;

    }


}
