package vista;

import controlador.ControladorBancoEstado;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CuentaPanel extends JPanel {

    private ControladorBancoEstado controlador;

    private JComboBox<String> cbClientes;
    private JComboBox<String> cbSucursales;
    private JComboBox<String> cbEmpleados;

    private JTextField tfNumero;
    private JComboBox<String> cbTipo;
    private JTextField tfExtra; // tasa o linea

    private DefaultListModel<String> modelCuentas;
    private JList<String> listCuentas;

    public CuentaPanel(ControladorBancoEstado controlador) {
        this.controlador = controlador;
        initUI();
        cargarClientes();
        cargarSucursales();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(7, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Crear Cuenta"));

        cbClientes = new JComboBox<>();
        cbSucursales = new JComboBox<>();
        cbEmpleados = new JComboBox<>();
        tfNumero = new JTextField();
        cbTipo = new JComboBox<>(new String[]{"CuentaAhorro", "CuentaCorriente", "CuentaRut"});
        tfExtra = new JTextField();

        form.add(new JLabel("Cliente:")); form.add(cbClientes);
        form.add(new JLabel("Sucursal:")); form.add(cbSucursales);
        form.add(new JLabel("Ejecutivo:")); form.add(cbEmpleados);
        form.add(new JLabel("N° Cuenta:")); form.add(tfNumero);
        form.add(new JLabel("Tipo:")); form.add(cbTipo);
        form.add(new JLabel("Tasa / Línea (opcional):")); form.add(tfExtra);

        JButton btnCrear = new JButton("Crear Cuenta");
        btnCrear.addActionListener(e -> onCrearCuenta());
        form.add(new JLabel()); form.add(btnCrear);

        add(form, BorderLayout.NORTH);

        // --- LISTA CENTRAL ---
        modelCuentas = new DefaultListModel<>();
        listCuentas = new JList<>(modelCuentas);
        JScrollPane sc = new JScrollPane(listCuentas);
        sc.setBorder(BorderFactory.createTitledBorder("Cuentas Registradas"));
        add(sc, BorderLayout.CENTER);


        JPopupMenu menu = new JPopupMenu();

        JMenuItem miDepositar = new JMenuItem("Depositar");
        JMenuItem miRetirar = new JMenuItem("Retirar");


        JMenuItem miTransferir = new JMenuItem("Transferir");


        miDepositar.addActionListener(a -> operarCuenta(true));
        miRetirar.addActionListener(a -> operarCuenta(false));
        miTransferir.addActionListener(a -> onTransferir());


        menu.add(miDepositar);
        menu.add(miRetirar);
        menu.addSeparator();
        menu.add(miTransferir);

        listCuentas.setComponentPopupMenu(menu);

        cbSucursales.addActionListener(e -> cargarEmpleados());
    }

    // ====================================
    // CARGA COMBOBOX CLIENTES
    // ====================================
    void cargarClientes() {
        cbClientes.removeAllItems();

        List<Cliente> clientes = controlador.getClientesActualizados();

        for (Cliente c : clientes) {
            cbClientes.addItem(c.getNombre() + " - " + c.getRut());
        }

        refrescarCuentas();
    }

    // ====================================
    // CARGA SUCURSALES
    // ====================================
    void cargarSucursales() {
        cbSucursales.removeAllItems();

        List<Sucursal> sucursales = controlador.getSucursalesActualizadas();
        for (Sucursal s : sucursales) {
            cbSucursales.addItem(s.getCodigo() + " - " + s.getDireccion());
        }

        cargarEmpleados();
    }

    private void cargarEmpleados() {
        cbEmpleados.removeAllItems();

        int idx = cbSucursales.getSelectedIndex();
        if (idx < 0) return;

        Sucursal suc = controlador.getSucursalesActualizadas().get(idx);

        for (Empleado emp : suc.getEmpleados()) {
            cbEmpleados.addItem(emp.getNombre() + " - " + emp.getRut());
        }
    }

    // ====================================
    // CREAR CUENTA
    // ====================================
    private void onCrearCuenta() {

        int selCliente = cbClientes.getSelectedIndex();
        int selSucursal = cbSucursales.getSelectedIndex();
        int selEmpleado = cbEmpleados.getSelectedIndex();

        if (selCliente < 0 || selSucursal < 0 || selEmpleado < 0) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar cliente, sucursal y ejecutivo.");
            return;
        }

        Cliente cliente = controlador.getClientesActualizados().get(selCliente);
        Sucursal sucursal = controlador.getSucursalesActualizadas().get(selSucursal);
        Empleado ejecutivo = sucursal.getEmpleados().get(selEmpleado);

        String numero = tfNumero.getText().trim();
        if (numero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa número de cuenta.");
            return;
        }

        String tipo = (String) cbTipo.getSelectedItem();
        String extra = tfExtra.getText().trim();

        Cuenta created = null;

        switch (tipo) {
            case "CuentaAhorro":
                double tasa = 0.01;
                if (!extra.isEmpty()) {
                    try { tasa = Double.parseDouble(extra); } catch (NumberFormatException ignored) {}
                }
                created = controlador.crearCuentaAhorro(cliente, numero, tasa, sucursal, ejecutivo);
                break;

            case "CuentaCorriente":
                double linea = 0;
                if (!extra.isEmpty()) {
                    try { linea = Double.parseDouble(extra); } catch (NumberFormatException ignored) {}
                }
                created = controlador.crearCuentaCorriente(cliente, numero, linea, sucursal, ejecutivo);
                break;

            case "CuentaRut":
                created = controlador.crearCuentaRut(cliente, numero, sucursal, ejecutivo);
                break;
        }

        JOptionPane.showMessageDialog(this, "Cuenta creada correctamente.");

        tfNumero.setText("");
        tfExtra.setText("");

        refrescarCuentas();
    }

    // ====================================
    // REFRESCAR LISTA DE CUENTAS
    // ====================================
    private void refrescarCuentas() {
        modelCuentas.clear();

        List<Cliente> clientes = controlador.getClientesActualizados();

        for (Cliente c : clientes) {
            for (Cuenta cu : c.getCuentas()) {

                String suc = cu.getSucursal() != null ? cu.getSucursal().getCodigo() : "N/A";
                String emp = cu.getEjecutivo() != null ? cu.getEjecutivo().getNombre() : "N/A";

                modelCuentas.addElement(
                        c.getNombre() + " | " + cu.getNumeroCuenta() +
                                " | Sucursal: " + suc +
                                " | Ejecutivo: " + emp +
                                " | Saldo: " + cu.getSaldo()
                );
            }
        }
    }

    // ====================================
    // DEPÓSITOS / RETIROS
    // ====================================
    private void operarCuenta(boolean depositar) {

        int idx = listCuentas.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una cuenta.");
            return;
        }

        // Buscar cuenta por índice
        Cuenta target = null;
        Cliente owner = null;
        int counter = -1;

        for (Cliente c : controlador.getClientesActualizados()) {
            for (Cuenta cu : c.getCuentas()) {
                counter++;
                if (counter == idx) {
                    target = cu;
                    owner = c;
                }
            }
        }

        if (target == null) return;

        String inp = JOptionPane.showInputDialog(this,
                depositar ? "Monto a depositar:" : "Monto a retirar:");

        if (inp == null || inp.trim().isEmpty()) return;

        try {

            double monto = Double.parseDouble(inp);

            if (depositar) {
                target.depositar(monto);
                controlador.guardarClientes();
                JOptionPane.showMessageDialog(this, "Depósito realizado.");
            } else {
                if (target.retirar(monto)) {
                    controlador.guardarClientes();
                    JOptionPane.showMessageDialog(this, "Retiro realizado.");
                } else {
                    JOptionPane.showMessageDialog(this, "Saldo insuficiente.");
                }
            }

            refrescarCuentas();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Monto inválido.");
        }
    }
    private void onTransferir() {
        // 1. Obtener la cuenta seleccionada en la lista (la tuya)
        int idx = listCuentas.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona de la lista la cuenta de origen.");
            return;
        }

        // Truco para obtener el objeto Cuenta desde la lista visual
        Cuenta cuentaOrigen = null;
        int count = 0;
        for (Cliente c : controlador.getClientesActualizados()) {
            for (Cuenta cu : c.getCuentas()) {
                if (count == idx) cuentaOrigen = cu;
                count++;
            }
        }

        if (cuentaOrigen == null) return;

        // 2. Pedir datos de destino
        String numDestino = JOptionPane.showInputDialog(this,
                "Origen: " + cuentaOrigen.getNumeroCuenta() + "\n\nIngresa el N° de cuenta destino:");
        if (numDestino == null || numDestino.isEmpty()) return;

        String montoStr = JOptionPane.showInputDialog(this, "Monto a transferir:");
        if (montoStr == null) return;

        try {
            double monto = Double.parseDouble(montoStr);

            // 3. Llamar al controlador
            boolean exito = controlador.transferir(cuentaOrigen.getNumeroCuenta(), numDestino, monto);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Transferencia realizada con éxito.");
                refrescarCuentas(); // Actualiza la lista para ver el nuevo saldo
            } else {
                JOptionPane.showMessageDialog(this, "Error: Saldo insuficiente o cuenta destino no existe.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El monto debe ser un número.");
        }
    }
}
