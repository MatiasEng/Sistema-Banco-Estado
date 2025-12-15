package vista;

import controlador.ControladorBancoEstado;
import modelo.*;

import javax.swing.*;
import java.awt.*;

public class OperacionCuentaDialog extends JDialog {

    private ControladorBancoEstado controlador;
    private Cuenta cuenta;

    public OperacionCuentaDialog(
            Window owner,
            ControladorBancoEstado controlador,
            Cuenta cuenta
    ) {
        super(owner, "Operaciones - Cuenta " + cuenta.getNumeroCuenta(),
                ModalityType.APPLICATION_MODAL);

        this.controlador = controlador;
        this.cuenta = cuenta;

        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Depositar", panelDeposito());
        tabs.addTab("Retirar", panelRetiro());
        tabs.addTab("Transferir (propias)", panelTransferenciaPropias());
        tabs.addTab("Transferir (terceros)", panelTransferenciaTerceros());

        add(tabs, BorderLayout.CENTER);
    }

    // =====================================
    // DEPÓSITO
    // =====================================
    private JPanel panelDeposito() {
        JPanel p = new JPanel(new GridLayout(3, 2, 6, 6));

        JTextField tfMonto = new JTextField();

        p.add(new JLabel("Cuenta destino:"));
        p.add(new JLabel(cuenta.getNumeroCuenta()));

        p.add(new JLabel("Monto:"));
        p.add(tfMonto);

        JButton btn = new JButton("Depositar");
        btn.addActionListener(e -> {
            try {
                double monto = Double.parseDouble(tfMonto.getText());
                cuenta.depositar(monto);
                controlador.guardarClientes();
                JOptionPane.showMessageDialog(this, "Depósito realizado.");
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido.");
            }
        });

        p.add(new JLabel());
        p.add(btn);

        return p;
    }

    // =====================================
    // RETIRO
    // =====================================
    private JPanel panelRetiro() {
        JPanel p = new JPanel(new GridLayout(3, 2, 6, 6));

        JTextField tfMonto = new JTextField();

        p.add(new JLabel("Cuenta origen:"));
        p.add(new JLabel(cuenta.getNumeroCuenta()));

        p.add(new JLabel("Monto:"));
        p.add(tfMonto);

        JButton btn = new JButton("Retirar");
        btn.addActionListener(e -> {
            try {
                double monto = Double.parseDouble(tfMonto.getText());

                if (cuenta.retirar(monto)) {
                    controlador.guardarClientes();
                    JOptionPane.showMessageDialog(this, "Retiro realizado.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Saldo insuficiente.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido.");
            }
        });

        p.add(new JLabel());
        p.add(btn);

        return p;
    }

    // =====================================
    // TRANSFERENCIA ENTRE CUENTAS DEL CLIENTE
    // =====================================
    private JPanel panelTransferenciaPropias() {
        JPanel p = new JPanel(new GridLayout(4, 2, 6, 6));

        JComboBox<String> cbCuentas = new JComboBox<>();
        JTextField tfMonto = new JTextField();

        Cliente owner = controlador.buscarClientePorCuenta(cuenta);

        for (Cuenta c : owner.getCuentas()) {
            if (!c.equals(cuenta)) {
                cbCuentas.addItem(c.getNumeroCuenta());
            }
        }

        p.add(new JLabel("Cuenta origen:"));
        p.add(new JLabel(cuenta.getNumeroCuenta()));

        p.add(new JLabel("Cuenta destino:"));
        p.add(cbCuentas);

        p.add(new JLabel("Monto:"));
        p.add(tfMonto);

        JButton btn = new JButton("Transferir");
        btn.addActionListener(e -> {
            try {
                String destino = (String) cbCuentas.getSelectedItem();
                double monto = Double.parseDouble(tfMonto.getText());

                if (controlador.transferir(
                        cuenta.getNumeroCuenta(), destino, monto)) {

                    JOptionPane.showMessageDialog(this, "Transferencia exitosa.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo realizar la transferencia.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos.");
            }
        });

        p.add(new JLabel());
        p.add(btn);

        return p;
    }

    // =====================================
    // TRANSFERENCIA A TERCEROS
    // =====================================
    private JPanel panelTransferenciaTerceros() {
        JPanel p = new JPanel(new GridLayout(4, 2, 6, 6));

        JTextField tfDestino = new JTextField();
        JTextField tfMonto = new JTextField();

        p.add(new JLabel("Cuenta origen:"));
        p.add(new JLabel(cuenta.getNumeroCuenta()));

        p.add(new JLabel("Cuenta destino:"));
        p.add(tfDestino);

        p.add(new JLabel("Monto:"));
        p.add(tfMonto);

        JButton btn = new JButton("Transferir");
        btn.addActionListener(e -> {
            try {
                String destino = tfDestino.getText().trim();
                double monto = Double.parseDouble(tfMonto.getText());

                boolean ok = controlador.transferir(
                        cuenta.getNumeroCuenta(), destino, monto);

                if (ok) {
                    JOptionPane.showMessageDialog(this, "Transferencia realizada.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error: saldo insuficiente o cuenta inválida.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos.");
            }
        });

        p.add(new JLabel());
        p.add(btn);

        return p;
    }
}
