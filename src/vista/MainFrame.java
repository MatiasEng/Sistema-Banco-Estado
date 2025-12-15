package vista;

import controlador.ControladorBancoEstado;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainFrame extends JFrame {
    private ControladorBancoEstado controlador;

    public MainFrame() {
        controlador = ControladorBancoEstado.getInstance();
        initUI();
    }

    private void initUI() {
        setTitle("Sistema BancoEstado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        headerPanel.setBackground(new Color(245, 245, 245)); // Un gris muy clarito de fondo

        ImageIcon logoIcon = null;
        try {
            ImageIcon originalIcon = new ImageIcon("src/icons/Logo_Banco_Estado.png");

            // Verificar si cargó la imagen
            if (originalIcon.getIconWidth() > 0) {
                Image scaledImage = originalIcon.getImage()
                        .getScaledInstance(-1, 60, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(scaledImage);
            } else {
                System.out.println("Advertencia: No se encontró 'logo_be.png' en la raíz del proyecto.");
            }
        } catch (Exception e) {
            System.out.println("Error cargando imagen: " + e.getMessage());
        }


        JLabel lblLogo = new JLabel();
        if (logoIcon != null) {
            lblLogo.setIcon(logoIcon);
        } else {
            // Si falla la imagen, mostrar un texto de respaldo
            lblLogo.setText("[Logo BancoEstado]");
            lblLogo.setForeground(Color.RED);
        }


        JLabel lblTitulo = new JLabel("Plataforma de Gestión Bancaria");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(50, 50, 50)); // Gris oscuro


        headerPanel.add(lblLogo);
        headerPanel.add(lblTitulo);


        add(headerPanel, BorderLayout.NORTH);


        ClientePanel clientePanel = new ClientePanel(controlador);
        SucursalPanel sucursalPanel = new SucursalPanel(controlador);
        CuentaPanel cuentaPanel = new CuentaPanel(controlador);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Gestión Clientes", clientePanel);
        tabs.addTab("Gestión Sucursales", sucursalPanel);
        tabs.addTab("Operaciones Cuentas", cuentaPanel);

        // Refrescar tabs automáticamente al cambiar
        tabs.addChangeListener(e -> {
            Component comp = tabs.getSelectedComponent();
            if (comp instanceof ClientePanel cp) cp.cargarClientes();
            if (comp instanceof SucursalPanel sp) sp.cargarSucursales();
            if (comp instanceof CuentaPanel cup) {
                cup.cargarClientes();
                cup.cargarSucursales();
            }
        });

        add(tabs, BorderLayout.CENTER);
    }
}