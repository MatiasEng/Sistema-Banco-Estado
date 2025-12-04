package controlador;

import java.io.*;
import modelo.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorBancoEstado implements Serializable {

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

    // ===============================
    // ACTUALIZACIONES
    // ===============================
    public List<Cliente> getClientesActualizados() {
        clientes = cargarClientes();
        return clientes;
    }

    public List<Sucursal> getSucursalesActualizadas() {
        sucursales = cargarSucursales();
        return sucursales;
    }

    // ===============================
    // CLIENTES
    // ===============================
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

    // ===============================
    // SUCURSALES
    // ===============================
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

    public List<Sucursal> getSucursales() { return sucursales; }
    public List<Cliente> getClientes() { return clientes; }

    // ===============================
    // CUENTAS (ARREGLADO)
    // ===============================
    public Cuenta crearCuentaCorriente(Cliente cliente, String numero, double linea,
                                       Sucursal sucursal, Empleado ejecutivo) {

        CuentaCorriente cc = new CuentaCorriente(numero, linea);
        cc.setSucursal(sucursal);
        cc.setEjecutivo(ejecutivo);

        cliente.agregarCuenta(cc);
        guardarClientes();

        generarContrato(cliente, cc, sucursal, ejecutivo);
        return cc;
    }

    public Cuenta crearCuentaAhorro(Cliente cliente, String numero, double tasa,
                                    Sucursal sucursal, Empleado ejecutivo) {

        CuentaAhorro ca = new CuentaAhorro(numero, tasa);
        ca.setSucursal(sucursal);
        ca.setEjecutivo(ejecutivo);

        cliente.agregarCuenta(ca);
        guardarClientes();

        generarContrato(cliente, ca, sucursal, ejecutivo);
        return ca;
    }

    public Cuenta crearCuentaRut(Cliente cliente, String numero,
                                 Sucursal sucursal, Empleado ejecutivo) {

        CuentaRut cr = new CuentaRut(numero);
        cr.setSucursal(sucursal);
        cr.setEjecutivo(ejecutivo);

        cliente.agregarCuenta(cr);
        guardarClientes();

        generarContrato(cliente, cr, sucursal, ejecutivo);
        return cr;
    }

    // ===============================
    // CONTRATO (FUNCIONANDO)
    // ===============================
    private void generarContrato(Cliente c, Cuenta cuenta, Sucursal suc, Empleado e) {

        String dirName = "contratos";
        File dir = new File(dirName);
        if (!dir.exists()) dir.mkdir();

        String fileName = "ContratoCuenta-" + cuenta.getNumeroCuenta() + ".txt";
        File archivo = new File(dir, fileName);

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {

            pw.println("=== CONTRATO DE APERTURA DE CUENTA ===\n");
            pw.println("Cliente: " + c.getNombre() + " (" + c.getRut() + ")");
            pw.println("Sucursal: " + suc.getCodigo() + " - " + suc.getDireccion());
            pw.println("Ejecutivo: " + e.getNombre() + " (" + e.getRut() + ")");
            pw.println("Tipo de cuenta: " + cuenta.getClass().getSimpleName());
            pw.println("Número de cuenta: " + cuenta.getNumeroCuenta());
            pw.println("Fecha: " + java.time.LocalDate.now());
            pw.println("--------------------------------------");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ===============================
    // PERSISTENCIA
    // ===============================
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
