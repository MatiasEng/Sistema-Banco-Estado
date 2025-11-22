package vista;

import controlador.ControladorBancoEstado;
import modelo.Cliente;
import modelo.Cuenta;
import modelo.CuentaAhorro;
import modelo.CuentaCorriente;
import modelo.CuentaRut;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CuentaPanel extends JPanel {
    private ControladorBancoEstado controlador;

    private JComboBox<String> cbClientes;
    private JTextField tfNumero;
    private JComboBox<String> cbTipo;
    private JTextField tfExtra; // tasa o linea

    private DefaultListModel<String> modelCuentas;
    private JList<String> listCuentas;

    public CuentaPanel(ControladorBancoEstado controlador) {
        this.controlador = controlador;
        initUI();
        cargarClientes();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5,2,6,6));
        form.setBorder(BorderFactory.createTitledBorder("Crear Cuenta"));
        cbClientes = new JComboBox<>();
        tfNumero = new JTextField();
        cbTipo = new JComboBox<>(new String[]{"CuentaAhorro","CuentaCorriente","CuentaRut"});
        tfExtra = new JTextField(); // tasa o lineaCredito

        form.add(new JLabel("Cliente:"));
        form.add(cbClientes);
        form.add(new JLabel("N° Cuenta:"));
        form.add(tfNumero);
        form.add(new JLabel("Tipo:"));
        form.add(cbTipo);
        form.add(new JLabel("Tasa/Linea (opcional):"));
        form.add(tfExtra);



        JButton btnCrear = new JButton("Crear Cuenta");
        btnCrear.addActionListener(e -> onCrearCuenta());
        form.add(new JLabel());   // celda vacía
        form.add(btnCrear);       // botón alineado como en sucursal

        // AGREGAR FORMULARIO ARRIBA
        add(form, BorderLayout.NORTH);

        modelCuentas = new DefaultListModel<>();
        listCuentas = new JList<>(modelCuentas);
        JScrollPane sc = new JScrollPane(listCuentas);
        sc.setBorder(BorderFactory.createTitledBorder("Cuentas (seleccionar para acciones)"));
        add(sc, BorderLayout.SOUTH);

        // Popup menu para depositar/retirar
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miDepositar = new JMenuItem("Depositar");
        JMenuItem miRetirar = new JMenuItem("Retirar");

        miDepositar.addActionListener(a -> operarCuenta(true));
        miRetirar.addActionListener(a -> operarCuenta(false));

        menu.add(miDepositar);
        menu.add(miRetirar);
        listCuentas.setComponentPopupMenu(menu);
    }

    private void cargarClientes() {
        cbClientes.removeAllItems();
        List<Cliente> clientes = controlador.getClientes();
        for (Cliente c : clientes) {
            cbClientes.addItem(c.getNombre() + " - " + c.getRut());
        }
        refrescarCuentas();
    }

    private void onCrearCuenta() {
        int sel = cbClientes.getSelectedIndex();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente.");
            return;
        }
        List<Cliente> clientes = controlador.getClientes();
        Cliente c = clientes.get(sel);
        String numero = tfNumero.getText().trim();
        String tipo = (String) cbTipo.getSelectedItem();
        String extra = tfExtra.getText().trim();
        if (numero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa número de cuenta.");
            return;
        }

        Cuenta created = null;
        switch (tipo) {
            case "CuentaAhorro":
                double tasa = 0.01;
                if (!extra.isEmpty()) {
                    try { tasa = Double.parseDouble(extra); } catch (NumberFormatException ex) {}
                }
                created = controlador.crearCuentaAhorro(c, numero, tasa);
                break;
            case "CuentaCorriente":
                double linea = 0.0;
                if (!extra.isEmpty()) {
                    try { linea = Double.parseDouble(extra); } catch (NumberFormatException ex) {}
                }
                created = controlador.crearCuentaCorriente(c, numero, linea);
                break;
            case "CuentaRut":
                created = controlador.crearCuentaRut(c, numero);
                break;
        }
        JOptionPane.showMessageDialog(this, "Cuenta creada: " + created.getNumeroCuenta());
        tfNumero.setText("");
        tfExtra.setText("");
        refrescarCuentas();
    }

    private void refrescarCuentas() {
        modelCuentas.clear();
        List<Cliente> clientes = controlador.getClientes();
        for (Cliente c : clientes) {
            for (Cuenta cu : c.getCuentas()) {
                modelCuentas.addElement(c.getNombre() + " | " + cu.getNumeroCuenta() + " | Saldo: " + cu.getSaldo());
            }
        }
    }

    private void operarCuenta(boolean depositar) {
        int idx = listCuentas.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una cuenta.");
            return;
        }
        // Encontrar cuenta por índice (misma lógica que refrescar)
        int counter = -1;
        Cuenta target = null;
        Cliente owner = null;
        for (Cliente c : controlador.getClientes()) {
            for (Cuenta cu : c.getCuentas()) {
                counter++;
                if (counter == idx) {
                    target = cu;
                    owner = c;
                    break;
                }
            }
            if (target != null) break;
        }
        if (target == null) return;

        String sMonto = JOptionPane.showInputDialog(this, (depositar ? "Monto a depositar:" : "Monto a retirar:"));
        if (sMonto == null || sMonto.trim().isEmpty()) return;
        double monto;
        try { monto = Double.parseDouble(sMonto); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Monto inválido.");
            return;
        }

        if (depositar) {
            target.depositar(monto);
            controlador.guardarClientes(); // método existe en controlador
            JOptionPane.showMessageDialog(this, "Depósito realizado.");
        } else {
            boolean ok = target.retirar(monto);
            if (ok) {
                controlador.guardarClientes();
                JOptionPane.showMessageDialog(this, "Retiro realizado.");
            } else {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente.");
            }
        }
        refrescarCuentas();
    }
}
