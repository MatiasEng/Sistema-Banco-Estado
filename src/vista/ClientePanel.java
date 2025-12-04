package vista;

import controlador.ControladorBancoEstado;
import modelo.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ClientePanel extends JPanel {

    private ControladorBancoEstado controlador;
    private DefaultListModel<String> listModel;
    private JList<String> listaClientes;

    private JTextField tfNombre;
    private JTextField tfRut;

    public ClientePanel(ControladorBancoEstado controlador) {
        this.controlador = controlador;
        initUI();
        cargarClientes();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Formulario
        JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Crear Cliente"));
        tfNombre = new JTextField();
        tfRut = new JTextField();

        form.add(new JLabel("Nombre:"));
        form.add(tfNombre);
        form.add(new JLabel("RUT:"));
        form.add(tfRut);

        JButton btnCrear = new JButton("Crear Cliente");
        btnCrear.addActionListener(this::onCrearCliente);

        form.add(new JLabel());
        form.add(btnCrear);

        add(form, BorderLayout.NORTH);

        // Lista
        listModel = new DefaultListModel<>();
        listaClientes = new JList<>(listModel);
        JScrollPane scroll = new JScrollPane(listaClientes);
        scroll.setBorder(BorderFactory.createTitledBorder("Clientes"));
        add(scroll, BorderLayout.CENTER);

        // Buscar
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField tfBuscarRut = new JTextField(12);
        JButton btnBuscar = new JButton("Buscar por RUT");

        btnBuscar.addActionListener(e -> {
            String rut = tfBuscarRut.getText().trim();
            if (rut.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese RUT para buscar.");
                return;
            }
            Cliente c = controlador.buscarCliente(rut);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Encontrado: " + c.getNombre() + " - " + c.getRut());
            }
        });

        bottom.add(new JLabel("RUT:"));
        bottom.add(tfBuscarRut);
        bottom.add(btnBuscar);

        add(bottom, BorderLayout.SOUTH);
    }

    private void onCrearCliente(ActionEvent ev) {
        String nombre = tfNombre.getText().trim();
        String rut = tfRut.getText().trim();

        if (nombre.isEmpty() || rut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa nombre y RUT.");
            return;
        }

        Cliente c = controlador.crearCliente(nombre, rut);

        JOptionPane.showMessageDialog(this, "Cliente creado: " + c.getNombre());

        tfNombre.setText("");
        tfRut.setText("");
        cargarClientes();
    }

    void cargarClientes() {
        listModel.clear();

        // Cambio importante: usa la versión actualizada
        List<Cliente> clientes = controlador.getClientesActualizados();
        if (clientes == null) return;

        for (Cliente c : clientes) {
            listModel.addElement(c.getNombre() + " - " + c.getRut());
        }
    }
}
