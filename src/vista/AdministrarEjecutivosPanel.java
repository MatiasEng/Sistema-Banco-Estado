package vista;

import controlador.ControladorBancoEstado;
import modelo.Empleado;
import modelo.Sucursal;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdministrarEjecutivosPanel extends JPanel {

    private ControladorBancoEstado controlador;
    private Sucursal sucursal;

    private DefaultListModel<String> model;
    private JList<String> lista;

    private JTextField tfNombre;
    private JTextField tfRut;

    public AdministrarEjecutivosPanel(ControladorBancoEstado controlador, Sucursal sucursal) {
        this.controlador = controlador;
        this.sucursal = sucursal;
        initUI();
        cargarEjecutivos();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // =========================
        // FORMULARIO
        // =========================
        JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo Ejecutivo"));

        tfNombre = new JTextField();
        tfRut = new JTextField();

        form.add(new JLabel("Nombre:"));
        form.add(tfNombre);

        form.add(new JLabel("RUT:"));
        form.add(tfRut);

        JButton btnAgregar = new JButton("Agregar Ejecutivo");
        btnAgregar.addActionListener(e -> agregarEjecutivo());

        form.add(new JLabel());
        form.add(btnAgregar);

        add(form, BorderLayout.NORTH);

        // =========================
        // LISTA
        // =========================
        model = new DefaultListModel<>();
        lista = new JList<>(model);

        JScrollPane sc = new JScrollPane(lista);
        sc.setBorder(BorderFactory.createTitledBorder("Ejecutivos de la sucursal"));
        add(sc, BorderLayout.CENTER);

        // =========================
        // BOTÓN ELIMINAR
        // =========================
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnEliminar = new JButton("Eliminar Ejecutivo");
        btnEliminar.addActionListener(e -> eliminarEjecutivo());

        bottom.add(btnEliminar);
        add(bottom, BorderLayout.SOUTH);
    }

    // =========================
    // AGREGAR EJECUTIVO
    // =========================
    private void agregarEjecutivo() {
        String nombre = tfNombre.getText().trim();
        String rut = tfRut.getText().trim();

        if (nombre.isEmpty() || rut.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nombre y RUT son obligatorios.",
                    "Campos obligatorios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Todos son ejecutivos
        controlador.agregarEmpleadoASucursal(sucursal, nombre, rut);

        tfNombre.setText("");
        tfRut.setText("");

        cargarEjecutivos();
    }

    // =========================
    // ELIMINAR EJECUTIVO
    // =========================
    private void eliminarEjecutivo() {
        int idx = lista.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un ejecutivo.");
            return;
        }

        Empleado emp = sucursal.getEmpleados().get(idx);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar al ejecutivo " + emp.getNombre() + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            sucursal.getEmpleados().remove(idx);
            controlador.guardarSucursales();
            cargarEjecutivos();
        }
    }

    // =========================
    // CARGAR LISTA
    // =========================
    private void cargarEjecutivos() {
        model.clear();
        List<Empleado> empleados = sucursal.getEmpleados();

        for (Empleado e : empleados) {
            model.addElement(
                    e.getNombre() + " (RUT: " + e.getRut() + ")"
            );
        }
    }
}
