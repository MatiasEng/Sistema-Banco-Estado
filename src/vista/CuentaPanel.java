package vista;

import controlador.ControladorBancoEstado;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CuentaPanel extends JPanel {

    private ControladorBancoEstado controlador;

    private JComboBox<String> cbClientes;
    private JComboBox<String> cbSucursales;
    private JComboBox<String> cbEmpleados;
    private JComboBox<String> cbTipo;

    private JTextField tfSaldoInicial;

    private JTable tabla;
    private DefaultTableModel tableModel;

    public CuentaPanel(ControladorBancoEstado controlador) {
        this.controlador = controlador;
        initUI();
        cargarClientes();
        cargarSucursales();
        refrescarTabla();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // =========================
        // FORMULARIO
        // =========================
        JPanel form = new JPanel(new GridLayout(6, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Crear Cuenta"));

        cbClientes = new JComboBox<>();
        cbSucursales = new JComboBox<>();
        cbEmpleados = new JComboBox<>();
        cbTipo = new JComboBox<>(new String[]{
                "CuentaAhorro", "CuentaCorriente", "CuentaRut"
        });
        tfSaldoInicial = new JTextField();

        form.add(new JLabel("Cliente:"));
        form.add(cbClientes);

        form.add(new JLabel("Sucursal:"));
        form.add(cbSucursales);

        form.add(new JLabel("Ejecutivo:"));
        form.add(cbEmpleados);

        form.add(new JLabel("Tipo de cuenta:"));
        form.add(cbTipo);

        form.add(new JLabel("Saldo inicial (opcional):"));
        form.add(tfSaldoInicial);

        JButton btnCrear = new JButton("Crear Cuenta");
        btnCrear.addActionListener(e -> onCrearCuenta());

        form.add(new JLabel());
        form.add(btnCrear);

        add(form, BorderLayout.NORTH);

        // =========================
        // TABLA CUENTAS
        // =========================
        tableModel = new DefaultTableModel(
                new Object[]{"N° Cuenta", "Cliente", "Saldo", "Tipo"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabla = new JTable(tableModel);
        JScrollPane sc = new JScrollPane(tabla);
        sc.setBorder(BorderFactory.createTitledBorder("Cuentas Registradas"));
        add(sc, BorderLayout.CENTER);

        // =========================
        // BOTÓN OPERACIONES
        // =========================
        JButton btnOperacion = new JButton("Operación con la cuenta");
        btnOperacion.addActionListener(e -> abrirOperacionCuenta());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(btnOperacion);
        add(bottom, BorderLayout.SOUTH);

        cbSucursales.addActionListener(e -> cargarEmpleados());
    }

    // =========================
    // CREAR CUENTA
    // =========================
    private void onCrearCuenta() {

        int iCliente = cbClientes.getSelectedIndex();
        int iSucursal = cbSucursales.getSelectedIndex();
        int iEmpleado = cbEmpleados.getSelectedIndex();

        if (iCliente < 0 || iSucursal < 0 || iEmpleado < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona cliente, sucursal y ejecutivo.");
            return;
        }

        Cliente cliente = controlador.getClientesActualizados().get(iCliente);
        Sucursal suc = controlador.getSucursalesActualizadas().get(iSucursal);
        Empleado emp = suc.getEmpleados().get(iEmpleado);

        double saldo = 0;
        if (!tfSaldoInicial.getText().trim().isEmpty()) {
            try {
                saldo = Double.parseDouble(tfSaldoInicial.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Saldo inválido.");
                return;
            }
        }

        String tipo = (String) cbTipo.getSelectedItem();

        switch (tipo) {
            case "CuentaAhorro":
                controlador.crearCuentaAhorro(cliente, saldo, suc, emp);
                break;

            case "CuentaCorriente":
                controlador.crearCuentaCorriente(cliente, saldo, suc, emp);
                break;

            case "CuentaRut":
                controlador.crearCuentaRut(cliente, saldo, suc, emp);
                break;
        }

        JOptionPane.showMessageDialog(this, "Cuenta creada correctamente.");
        tfSaldoInicial.setText("");
        refrescarTabla();
    }

    // =========================
    // OPERACIONES
    // =========================
    private void abrirOperacionCuenta() {
        Cuenta cuenta = getCuentaSeleccionada();
        if (cuenta == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una cuenta.");
            return;
        }

        JDialog dialog = new OperacionCuentaDialog(
                SwingUtilities.getWindowAncestor(this),
                controlador,
                cuenta
        );

        dialog.setVisible(true);
        refrescarTabla();
    }

    private Cuenta getCuentaSeleccionada() {
        int row = tabla.getSelectedRow();
        if (row < 0) return null;

        String numero = tableModel.getValueAt(row, 0).toString();
        return controlador.buscarCuentaGlobal(numero);
    }

    // =========================
    // CARGAS
    // =========================
    void cargarClientes() {
        cbClientes.removeAllItems();
        for (Cliente c : controlador.getClientesActualizados()) {
            cbClientes.addItem(c.getNombre() + " - " + c.getRut());
        }
    }

    void cargarSucursales() {
        cbSucursales.removeAllItems();
        for (Sucursal s : controlador.getSucursalesActualizadas()) {
            cbSucursales.addItem(s.getCodigo() + " - " + s.getDireccion());
        }
        cargarEmpleados();
    }

    private void cargarEmpleados() {
        cbEmpleados.removeAllItems();
        int idx = cbSucursales.getSelectedIndex();
        if (idx < 0) return;

        for (Empleado e : controlador
                .getSucursalesActualizadas()
                .get(idx)
                .getEmpleados()) {

            cbEmpleados.addItem(e.getNombre() + " - " + e.getRut());
        }
    }

    // =========================
    // REFRESCAR TABLA
    // =========================
    private void refrescarTabla() {
        tableModel.setRowCount(0);

        List<Cliente> clientes = controlador.getClientesActualizados();
        for (Cliente c : clientes) {
            for (Cuenta cu : c.getCuentas()) {
                tableModel.addRow(new Object[]{
                        cu.getNumeroCuenta(),
                        c.getNombre(),
                        cu.getSaldo(),
                        cu.getClass().getSimpleName()
                });
            }
        }
    }
}
