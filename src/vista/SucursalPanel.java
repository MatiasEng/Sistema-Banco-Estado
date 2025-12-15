package vista;

import controlador.ControladorBancoEstado;
import modelo.Sucursal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class SucursalPanel extends JPanel {

    private ControladorBancoEstado controlador;

    // Tabla
    private JTable tableSuc;
    private DefaultTableModel tableModel;

    // Campos
    private JTextField tfCodigo;
    private JTextField tfDireccion;

    // Labels obligatorio
    private JLabel lblReqCodigo;
    private JLabel lblReqDireccion;

    public SucursalPanel(ControladorBancoEstado controlador) {
        this.controlador = controlador;
        initUI();
        cargarSucursales();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // =========================
        // FORMULARIO
        // =========================
        JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Crear Sucursal"));

        tfCodigo = new JTextField();
        tfDireccion = new JTextField();

        form.add(new JLabel("Código:"));
        lblReqCodigo = agregarCampoConObligatorio(form, tfCodigo);

        form.add(new JLabel("Dirección:"));
        lblReqDireccion = agregarCampoConObligatorio(form, tfDireccion);

        JButton btnCrear = new JButton("Crear Sucursal");
        btnCrear.addActionListener(this::onCrearSucursal);

        form.add(new JLabel());
        form.add(btnCrear);

        add(form, BorderLayout.NORTH);

        // =========================
        // TABLA SUCURSALES
        // =========================
        tableModel = new DefaultTableModel(
                new Object[]{"Código", "Dirección", "Ejecutivos"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableSuc = new JTable(tableModel);
        tableSuc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sc = new JScrollPane(tableSuc);
        sc.setBorder(BorderFactory.createTitledBorder("Sucursales"));
        add(sc, BorderLayout.CENTER);

        // =========================
        // BOTONES INFERIORES
        // =========================
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdmin = new JButton("Administrar Ejecutivos");
        btnAdmin.addActionListener(e -> abrirAdminEjecutivos());

        bottom.add(btnAdmin);
        add(bottom, BorderLayout.SOUTH);

        // =========================
        // LISTENERS VALIDACIÓN
        // =========================
        addDocumentListener(tfCodigo, lblReqCodigo);
        addDocumentListener(tfDireccion, lblReqDireccion);
    }

    // =========================
    // CREAR SUCURSAL
    // =========================
    private void onCrearSucursal(ActionEvent ev) {

        boolean valido = true;
        valido &= validarCampo(tfCodigo, lblReqCodigo);
        valido &= validarCampo(tfDireccion, lblReqDireccion);

        if (!valido) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes completar todos los campos obligatorios.",
                    "Campos obligatorios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        controlador.crearSucursal(
                tfCodigo.getText().trim(),
                tfDireccion.getText().trim()
        );

        limpiarFormulario();
        cargarSucursales();
    }

    // =========================
    // ADMINISTRAR EJECUTIVOS
    // =========================
    private void abrirAdminEjecutivos() {

        int idx = tableSuc.getSelectedRow();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una sucursal.");
            return;
        }

        Sucursal s = controlador.getSucursalesActualizadas().get(idx);

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Ejecutivos - Sucursal " + s.getCodigo(),
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setContentPane(new AdministrarEjecutivosPanel(controlador, s));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        cargarSucursales(); // refresca contador
    }

    // =========================
    // CARGAR SUCURSALES
    // =========================
    void cargarSucursales() {
        tableModel.setRowCount(0);

        List<Sucursal> sucursales = controlador.getSucursalesActualizadas();

        for (Sucursal s : sucursales) {
            tableModel.addRow(new Object[]{
                    s.getCodigo(),
                    s.getDireccion(),
                    s.getEmpleados().size()
            });
        }
    }

    // =========================
    // VALIDACIONES
    // =========================
    private boolean validarCampo(JTextField campo, JLabel label) {
        if (campo.getText().trim().isEmpty()) {
            label.setForeground(Color.RED);
            return false;
        } else {
            label.setForeground(Color.GRAY);
            return true;
        }
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

    // =========================
    // UI HELPERS
    // =========================
    private JLabel agregarCampoConObligatorio(JPanel form, JTextField campo) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel obligatorio = new JLabel("* obligatorio");
        obligatorio.setFont(new Font("Arial", Font.PLAIN, 10));
        obligatorio.setForeground(Color.RED);
        obligatorio.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(campo);
        panel.add(Box.createVerticalStrut(2));
        panel.add(obligatorio);

        form.add(panel);
        return obligatorio;
    }

    private void limpiarFormulario() {
        tfCodigo.setText("");
        tfDireccion.setText("");

        lblReqCodigo.setForeground(Color.RED);
        lblReqDireccion.setForeground(Color.RED);
    }
}
