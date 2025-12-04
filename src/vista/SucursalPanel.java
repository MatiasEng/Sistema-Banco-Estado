package vista;

import controlador.ControladorBancoEstado;
import modelo.Sucursal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SucursalPanel extends JPanel {

    private ControladorBancoEstado controlador;

    private DefaultListModel<String> modelSuc;
    private JList<String> listSuc;

    private JTextField tfCodigo;
    private JTextField tfDireccion;

    public SucursalPanel(ControladorBancoEstado controlador) {
        this.controlador = controlador;
        initUI();
        cargarSucursales();
    }

    private void initUI() {

        setLayout(new BorderLayout());

        // FORMULARIO
        JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Crear Sucursal"));

        tfCodigo = new JTextField();
        tfDireccion = new JTextField();

        form.add(new JLabel("Código:"));
        form.add(tfCodigo);

        form.add(new JLabel("Dirección:"));
        form.add(tfDireccion);

        JButton btnCrear = new JButton("Crear Sucursal");
        btnCrear.addActionListener(this::onCrearSucursal);

        form.add(new JLabel());
        form.add(btnCrear);

        add(form, BorderLayout.NORTH);

        // LISTA
        modelSuc = new DefaultListModel<>();
        listSuc = new JList<>(modelSuc);

        JScrollPane sc = new JScrollPane(listSuc);
        sc.setBorder(BorderFactory.createTitledBorder("Sucursales"));
        add(sc, BorderLayout.CENTER);

        // BOTÓN AGREGAR EMPLEADO
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAgregarEmpleado = new JButton("Agregar Ejecutivo");
        btnAgregarEmpleado.addActionListener(e -> agregarEmpleado());

        bottom.add(btnAgregarEmpleado);

        add(bottom, BorderLayout.SOUTH);
    }

    // ===================================
    // CREAR SUCURSAL
    // ===================================
    private void onCrearSucursal(ActionEvent ev) {

        String codigo = tfCodigo.getText().trim();
        String direccion = tfDireccion.getText().trim();

        if (codigo.isEmpty() || direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa código y dirección.");
            return;
        }

        controlador.crearSucursal(codigo, direccion);

        tfCodigo.setText("");
        tfDireccion.setText("");

        cargarSucursales();
    }

    // ===================================
    // AGREGAR EMPLEADO
    // ===================================
    private void agregarEmpleado() {

        int idx = listSuc.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una sucursal.");
            return;
        }

        // Usamos sucursales actualizadas
        Sucursal s = controlador.getSucursalesActualizadas().get(idx);

        String nombre = JOptionPane.showInputDialog(this, "Nombre del empleado:");
        if (nombre == null || nombre.trim().isEmpty()) return;

        String rut = JOptionPane.showInputDialog(this, "RUT del empleado:");
        if (rut == null || rut.trim().isEmpty()) return;

        String cargo = JOptionPane.showInputDialog(this, "Cargo:");
        if (cargo == null || cargo.trim().isEmpty()) return;

        controlador.agregarEmpleadoASucursal(s, nombre, rut, cargo);

        JOptionPane.showMessageDialog(this, "Empleado agregado.");

        cargarSucursales();
    }

    // ===================================
    // CARGAR SUCURSALES
    // ===================================
    void cargarSucursales() {

        modelSuc.clear();

        List<Sucursal> sucursales = controlador.getSucursalesActualizadas();

        for (Sucursal s : sucursales) {
            modelSuc.addElement(
                    s.getCodigo() + " - " + s.getDireccion() +
                            " (empleados: " + s.getEmpleados().size() + ")"
            );
        }
    }
}
