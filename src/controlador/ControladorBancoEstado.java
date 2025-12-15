package controlador;

import java.io.*;
import modelo.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorBancoEstado implements Serializable {

    private static ControladorBancoEstado instance;

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

    private ControladorBancoEstado() {
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
    public Cliente buscarClientePorCuenta(Cuenta cuenta) {
        for (Cliente c : clientes) {
            if (c.getCuentas().contains(cuenta)) {
                return c;
            }
        }
        return null;
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

    public void agregarEmpleadoASucursal(Sucursal s, String nombre, String rut) {
        Empleado e = new Empleado(nombre, rut);
        s.agregarEmpleado(e);
        guardarSucursales();
    }

    // ===============================
    // CUENTAS (API LIMPIA)
    // ===============================
    public Cuenta crearCuentaAhorro(
            Cliente cliente,
            double saldoInicial,
            Sucursal sucursal,
            Empleado ejecutivo
    ) {
        String numero = generarNumeroCuenta();
        double tasa = 0.01;

        CuentaAhorro ca = new CuentaAhorro(numero, tasa);
        ca.setSucursal(sucursal);
        ca.setEjecutivo(ejecutivo);
        ca.setSaldo(saldoInicial);

        cliente.agregarCuenta(ca);
        guardarClientes();
        generarContrato(cliente, ca, sucursal, ejecutivo);

        return ca;
    }

    public Cuenta crearCuentaCorriente(
            Cliente cliente,
            double saldoInicial,
            Sucursal sucursal,
            Empleado ejecutivo
    ) {
        String numero = generarNumeroCuenta();
        double lineaCredito = 0;

        CuentaCorriente cc = new CuentaCorriente(numero, lineaCredito);
        cc.setSucursal(sucursal);
        cc.setEjecutivo(ejecutivo);
        cc.setSaldo(saldoInicial);

        cliente.agregarCuenta(cc);
        guardarClientes();
        generarContrato(cliente, cc, sucursal, ejecutivo);

        return cc;
    }

    public Cuenta crearCuentaRut(
            Cliente cliente,
            double saldoInicial,
            Sucursal sucursal,
            Empleado ejecutivo
    ) {
        // RUT sin dígito verificador
        String numero = cliente.getRut().split("-")[0];

        CuentaRut cr = new CuentaRut(numero);
        cr.setSucursal(sucursal);
        cr.setEjecutivo(ejecutivo);
        cr.setSaldo(saldoInicial);

        cliente.agregarCuenta(cr);
        guardarClientes();
        generarContrato(cliente, cr, sucursal, ejecutivo);

        return cr;
    }

    // ===============================
    // UTILIDADES
    // ===============================
    private String generarNumeroCuenta() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    public Cuenta buscarCuentaGlobal(String numeroCuenta) {
        for (Cliente c : clientes) {
            for (Cuenta cuenta : c.getCuentas()) {
                if (cuenta.getNumeroCuenta().equals(numeroCuenta)) {
                    return cuenta;
                }
            }
        }
        return null;
    }

    public boolean transferir(String origen, String destino, double monto) {
        Cuenta cOrigen = buscarCuentaGlobal(origen);
        Cuenta cDestino = buscarCuentaGlobal(destino);

        if (cOrigen == null || cDestino == null) return false;

        if (cOrigen.retirar(monto)) {
            cDestino.depositar(monto);
            guardarClientes();
            return true;
        }
        return false;
    }

    // ===============================
    // CONTRATO
    // ===============================
    private void generarContrato(Cliente c, Cuenta cuenta, Sucursal s, Empleado e) {

        File dir = new File("contratos");
        if (!dir.exists()) dir.mkdir();

        File archivo = new File(dir, "ContratoCuenta-" + cuenta.getNumeroCuenta() + ".txt");

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {

            pw.println("=== CONTRATO DE APERTURA DE CUENTA ===\n");
            pw.println("Cliente: " + c.getNombre() + " (" + c.getRut() + ")");
            pw.println("Sucursal: " + s.getCodigo() + " - " + s.getDireccion());
            pw.println("Ejecutivo: " + e.getNombre());
            pw.println("Tipo de cuenta: " + cuenta.getClass().getSimpleName());
            pw.println("Número de cuenta: " + cuenta.getNumeroCuenta());
            pw.println("Saldo inicial: " + cuenta.getSaldo());
            pw.println("Fecha: " + java.time.LocalDate.now());

        } catch (IOException ex) {
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

    protected List<Cliente> cargarClientes() {
        return (List<Cliente>) cargarObjeto(archivoClientes, new ArrayList<>());
    }

    protected List<Sucursal> cargarSucursales() {
        return (List<Sucursal>) cargarObjeto(archivoSucursales, new ArrayList<>());
    }

    protected void guardarObjeto(String archivo, Object data) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            out.writeObject(data);
        } catch (Exception ignored) {}
    }

    protected Object cargarObjeto(String archivo, Object def) {
        File f = new File(archivo);
        if (!f.exists()) return def;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return in.readObject();
        } catch (Exception e) {
            return def;
        }
    }
}
