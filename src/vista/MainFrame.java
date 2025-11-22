package vista;

import controlador.ControladorBancoEstado;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private ControladorBancoEstado controlador;

    public MainFrame() {
        controlador = new ControladorBancoEstado();
        initUI();
    }

    private void initUI() {
        setTitle("BancoEstado - Proyecto (Java Swing)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clientes", new ClientePanel(controlador));
        tabs.addTab("Sucursales", new SucursalPanel(controlador));
        tabs.addTab("Cuentas", new CuentaPanel(controlador));

        add(tabs, BorderLayout.CENTER);
    }
}
