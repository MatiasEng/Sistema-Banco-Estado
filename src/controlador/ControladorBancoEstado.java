package controlador;

import java.io.*;
import modelo.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorBancoEstado implements Serializable{
    private static ControladorBancoEstado instance = null;

    public static ControladorBancoEstado getInstance() {
        if (instance == null) {
            instance = new ControladorBancoEstado();
        }
        return instance;
    }

    private List<Cliente> clientes;
    private List<Sucursal> sucursales;

    private final String archivoClientes = "clientes.dat";
    private final String archivoSucursales = "sucursales.dat";

    public ControladorBancoEstado() {
        clientes = cargarClientes();
        sucursales = cargarSucursales();
    }

    // ------------------------
    // Clientes
    // ------------------------
    public Cliente crearCliente(String nombre, String rut) {
        Cliente c = new Cliente(nombre, rut);
        clientes.add(c);
        guardarClientes();
        return c;
    }

    public Cliente buscarCliente(String rut) {
        return clientes.stream()
                .filter(c -> c.getRut().equals(rut))
                .findFirst()
                .orElse(null);
    }

    // ------------------------
    // Sucursales
    // ------------------------
    public Sucursal crearSucursal(String codigo, String direccion) {
        Sucursal s = new Sucursal(codigo, direccion);
        sucursales.add(s);
        guardarSucursales();
        return s;
    }

    public void agregarEmpleadoASucursal(Sucursal s, String nombre, String rut, String cargo) {
        Empleado e = new Empleado(nombre, rut, cargo);
        s.agregarEmpleado(e);
        guardarSucursales();
    }

    public List<Sucursal> getSucursales() {
        return sucursales;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    // ------------------------
    // Cuentas
    // ------------------------
    public Cuenta crearCuentaCorriente(Cliente c, String numero, double lineaCredito) {
        CuentaCorriente cc = new CuentaCorriente(numero, lineaCredito);
        c.agregarCuenta(cc);
        guardarClientes();
        return cc;
    }

    public Cuenta crearCuentaAhorro(Cliente c, String numero, double tasa) {
        CuentaAhorro ca = new CuentaAhorro(numero, tasa);
        c.agregarCuenta(ca);
        guardarClientes();
        return ca;
    }

    public Cuenta crearCuentaRut(Cliente c, String numero) {
        CuentaRut cr = new CuentaRut(numero);
        c.agregarCuenta(cr);
        guardarClientes();
        return cr;
    }

    // ------------------------
    // Persistencia
    // ------------------------
    public void guardarClientes() {
        guardarObjeto(archivoClientes, clientes);
    }

    public void guardarSucursales() {
        guardarObjeto(archivoSucursales, sucursales);
    }

    private List<Cliente> cargarClientes() {
        return (List<Cliente>) cargarObjeto(archivoClientes, new ArrayList<>());
    }

    private List<Sucursal> cargarSucursales() {
        return (List<Sucursal>) cargarObjeto(archivoSucursales, new ArrayList<>());
    }

    // Uso genérico de persistencia
    private void guardarObjeto(String archivo, Object data) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            out.writeObject(data);
        } catch (Exception e) {
            System.out.println("Error al guardar " + archivo + ": " + e.getMessage());
        }
    }

    private Object cargarObjeto(String archivo, Object valorPorDefecto) {
        File f = new File(archivo);
        if (!f.exists()) return valorPorDefecto;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return in.readObject();
        } catch (Exception e) {
            System.out.println("Error al cargar " + archivo + ": " + e.getMessage());
            return valorPorDefecto;
        }
    }


}
