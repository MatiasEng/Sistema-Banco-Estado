package vista;

import controlador.ControladorBancoEstado;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private ControladorBancoEstado controlador;

    public MainFrame() {
        // ✔ Use the singleton, not a new instance
        controlador = ControladorBancoEstado.getInstance();
        initUI();
    }

    private void initUI() {
        setTitle("BancoEstado - Proyecto (Java Swing)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Panels must be created ONCE and reused
        ClientePanel clientePanel = new ClientePanel(controlador);
        SucursalPanel sucursalPanel = new SucursalPanel(controlador);
        CuentaPanel cuentaPanel = new CuentaPanel(controlador);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clientes", clientePanel);
        tabs.addTab("Sucursales", sucursalPanel);
        tabs.addTab("Cuentas", cuentaPanel);

        // ✔ Refresh tabs automatically when switching
        tabs.addChangeListener(e -> {
            Component comp = tabs.getSelectedComponent();

            if (comp instanceof ClientePanel cp) {
                cp.cargarClientes();
            }
            if (comp instanceof SucursalPanel sp) {
                sp.cargarSucursales();
            }
            if (comp instanceof CuentaPanel cup) {
                cup.cargarClientes();   // newly added clients
                cup.cargarSucursales(); // newly added sucursales/employees
            }
        });

        add(tabs, BorderLayout.CENTER);
    }
}
