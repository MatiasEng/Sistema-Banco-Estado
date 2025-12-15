package vista;

import controlador.ControladorBancoEstado;
import modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ClientePanel extends JPanel {

    private ControladorBancoEstado controlador;

    // ===== TABLA =====
    private JTable tablaClientes;
    private DefaultTableModel tableModel;

    // Campos
    private JTextField tfNombres;
    private JTextField tfApellidoPaterno;
    private JTextField tfApellidoMaterno;

    // RUT dividido
    private JTextField tfRutNumero;
    private JTextField tfRutDV;

    // Labels obligatorio
    private JLabel lblReqNombres;
    private JLabel lblReqApPat;
    private JLabel lblReqApMat;
    private JLabel lblReqRut;

    public ClientePanel(ControladorBancoEstado controlador) {
        this.controlador = controlador;
        initUI();
        cargarClientes();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // =========================
        // FORMULARIO
        // =========================
        JPanel form = new JPanel(new GridLayout(5, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Crear Cliente"));

        tfNombres = new JTextField();
        tfApellidoPaterno = new JTextField();
        tfApellidoMaterno = new JTextField();

        tfRutNumero = new JTextField(10);
        tfRutDV = new JTextField(2);

        form.add(new JLabel("Nombre(s):"));
        lblReqNombres = agregarCampoConObligatorio(form, tfNombres);

        form.add(new JLabel("Apellido paterno:"));
        lblReqApPat = agregarCampoConObligatorio(form, tfApellidoPaterno);

        form.add(new JLabel("Apellido materno:"));
        lblReqApMat = agregarCampoConObligatorio(form, tfApellidoMaterno);

        form.add(new JLabel("RUT:"));
        lblReqRut = agregarRutConObligatorio(form);

        JButton btnCrear = new JButton("Crear Cliente");
        btnCrear.addActionListener(this::onCrearCliente);

        form.add(new JLabel());
        form.add(btnCrear);

        add(form, BorderLayout.NORTH);

        // =========================
        // TABLA
        // =========================
        tableModel = new DefaultTableModel(
                new Object[]{"Nombre", "RUT", "Cuentas"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tablaClientes = new JTable(tableModel);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBorder(BorderFactory.createTitledBorder("Clientes Registrados"));
        add(scroll, BorderLayout.CENTER);

        // =========================
        // BOTONES INFERIORES
        // =========================
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnEliminar = new JButton("Eliminar Cliente");
        btnEliminar.addActionListener(e -> eliminarClienteSeleccionado());

        bottom.add(btnEliminar);
        add(bottom, BorderLayout.SOUTH);

        // =========================
        // LISTENERS
        // =========================
        addDocumentListener(tfNombres, lblReqNombres);
        addDocumentListener(tfApellidoPaterno, lblReqApPat);
        addDocumentListener(tfApellidoMaterno, lblReqApMat);
        addDocumentListener(tfRutNumero, lblReqRut);
        addDocumentListener(tfRutDV, lblReqRut);
    }

    // =========================
    // CREAR CLIENTE
    // =========================
    private void onCrearCliente(ActionEvent ev) {

        boolean valido = true;
        valido &= validarCampo(tfNombres, lblReqNombres);
        valido &= validarCampo(tfApellidoPaterno, lblReqApPat);
        valido &= validarCampo(tfApellidoMaterno, lblReqApMat);
        valido &= validarRut();
        valido &= validarFormatoRut();

        if (!valido) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes completar todos los campos obligatorios.",
                    "Campos obligatorios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!esTextoValido(tfNombres.getText())
                || !esTextoValido(tfApellidoPaterno.getText())
                || !esTextoValido(tfApellidoMaterno.getText())) {

            JOptionPane.showMessageDialog(this,
                    "Los nombres y apellidos solo pueden contener letras.");
            return;
        }

        String nombreCompleto =
                tfNombres.getText().trim() + " " +
                        tfApellidoPaterno.getText().trim() + " " +
                        tfApellidoMaterno.getText().trim();

        String rutCompleto =
                tfRutNumero.getText().trim() + "-" +
                        tfRutDV.getText().trim().toUpperCase();

        controlador.crearCliente(nombreCompleto, rutCompleto);

        limpiarFormulario();
        cargarClientes();
    }

    // =========================
    // ELIMINAR CLIENTE
    // =========================
    private void eliminarClienteSeleccionado() {

        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un cliente de la tabla.");
            return;
        }

        String rut = tableModel.getValueAt(fila, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el cliente con RUT " + rut + "?\nSe eliminarán también sus cuentas.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        controlador.getClientesActualizados()
                .removeIf(c -> c.getRut().equals(rut));

        controlador.guardarClientes();
        cargarClientes();
    }

    // =========================
    // VALIDACIONES
    // =========================
    private boolean validarCampo(JTextField campo, JLabel label) {
        boolean ok = !campo.getText().trim().isEmpty();
        label.setForeground(ok ? Color.GRAY : Color.RED);
        return ok;
    }

    private boolean validarRut() {
        boolean ok = !tfRutNumero.getText().trim().isEmpty()
                && !tfRutDV.getText().trim().isEmpty();
        lblReqRut.setForeground(ok ? Color.GRAY : Color.RED);
        return ok;
    }

    private boolean validarFormatoRut() {
        if (!tfRutNumero.getText().matches("\\d+")) return false;
        return tfRutDV.getText().matches("[0-9kK]");
    }

    private boolean esTextoValido(String texto) {
        return texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }

    // =========================
    // UI HELPERS
    // =========================
    private JLabel agregarCampoConObligatorio(JPanel form, JTextField campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel obligatorio = new JLabel("* obligatorio");
        obligatorio.setFont(new Font("Arial", Font.PLAIN, 10));
        obligatorio.setForeground(Color.RED);

        panel.add(campo);
        panel.add(Box.createVerticalStrut(2));
        panel.add(obligatorio);

        form.add(panel);
        return obligatorio;
    }

    private JLabel agregarRutConObligatorio(JPanel form) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel rutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        rutPanel.add(tfRutNumero);
        rutPanel.add(new JLabel("-"));
        rutPanel.add(tfRutDV);

        JLabel obligatorio = new JLabel("* obligatorio");
        obligatorio.setFont(new Font("Arial", Font.PLAIN, 10));
        obligatorio.setForeground(Color.RED);

        panel.add(rutPanel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(obligatorio);

        form.add(panel);
        return obligatorio;
    }

    private void addDocumentListener(JTextField field, JLabel label) {
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validarCampo(field, label);
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validarCampo(field, label);
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validarCampo(field, label);
            }
        });
    }

    private void limpiarFormulario() {
        tfNombres.setText("");
        tfApellidoPaterno.setText("");
        tfApellidoMaterno.setText("");
        tfRutNumero.setText("");
        tfRutDV.setText("");

        lblReqNombres.setForeground(Color.RED);
        lblReqApPat.setForeground(Color.RED);
        lblReqApMat.setForeground(Color.RED);
        lblReqRut.setForeground(Color.RED);
    }

    // =========================
    // CARGAR CLIENTES
    // =========================
    void cargarClientes() {
        tableModel.setRowCount(0);

        List<Cliente> clientes = controlador.getClientesActualizados();
        if (clientes == null) return;

        for (Cliente c : clientes) {
            tableModel.addRow(new Object[]{
                    c.getNombre(),
                    c.getRut(),
                    c.getCuentas().size()
            });
        }
    }
}
