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

        // RUT fields
        tfRutNumero = new JTextField(10);
        tfRutDV = new JTextField(2);

        // ---------- NOMBRES ----------
        form.add(new JLabel("Nombre(s):"));
        lblReqNombres = agregarCampoConObligatorio(form, tfNombres);

        form.add(new JLabel("Apellido paterno:"));
        lblReqApPat = agregarCampoConObligatorio(form, tfApellidoPaterno);

        form.add(new JLabel("Apellido materno:"));
        lblReqApMat = agregarCampoConObligatorio(form, tfApellidoMaterno);

        // ---------- RUT ----------
        form.add(new JLabel("RUT:"));
        lblReqRut = agregarRutConObligatorio(form);

        JButton btnCrear = new JButton("Crear Cliente");
        btnCrear.addActionListener(this::onCrearCliente);

        form.add(new JLabel());
        form.add(btnCrear);

        add(form, BorderLayout.NORTH);

        // =========================
        // LISTA DE CLIENTES
        // =========================
        listModel = new DefaultListModel<>();
        listaClientes = new JList<>(listModel);
        JScrollPane scroll = new JScrollPane(listaClientes);
        scroll.setBorder(BorderFactory.createTitledBorder("Clientes"));
        add(scroll, BorderLayout.CENTER);

        // =========================
        // LISTENERS
        // =========================
        addDocumentListener(tfNombres, lblReqNombres);
        addDocumentListener(tfApellidoPaterno, lblReqApPat);
        addDocumentListener(tfApellidoMaterno, lblReqApMat);

        // IMPORTANTE: ambos campos del RUT
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
                        tfRutDV.getText().trim();

        Cliente c = controlador.crearCliente(nombreCompleto, rutCompleto);

        JOptionPane.showMessageDialog(this,
                "Cliente creado:\n" + c.getNombre());

        limpiarFormulario();
        cargarClientes();
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
    private boolean validarFormatoRut() {

        String rutNum = tfRutNumero.getText().trim();
        String rutDv = tfRutDV.getText().trim();

        // Número del RUT: solo dígitos
        if (!rutNum.matches("\\d+")) {
            JOptionPane.showMessageDialog(
                    this,
                    "El número del RUT debe contener solo dígitos.",
                    "RUT inválido",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        // DV: un solo carácter, número o K
        if (!rutDv.matches("[0-9kK]")) {
            JOptionPane.showMessageDialog(
                    this,
                    "El dígito verificador debe ser un número o la letra K.",
                    "RUT inválido",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }


    private boolean validarRut() {
        boolean ok = !tfRutNumero.getText().trim().isEmpty()
                && !tfRutDV.getText().trim().isEmpty();

        lblReqRut.setForeground(ok ? Color.GRAY : Color.RED);
        return ok;
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
        obligatorio.setForeground(Color.RED); // Empezamos en rojo
        obligatorio.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        obligatorio.setForeground(Color.RED); // Empezamos en rojo

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

        lblReqNombres.setForeground(Color.RED); // Iniciar en rojo
        lblReqApPat.setForeground(Color.RED);
        lblReqApMat.setForeground(Color.RED);
        lblReqRut.setForeground(Color.RED);
    }

    void cargarClientes() {
        listModel.clear();

        List<Cliente> clientes = controlador.getClientesActualizados();
        if (clientes == null) return;

        for (Cliente c : clientes) {
            listModel.addElement(c.getNombre() + " - " + c.getRut());
        }
    }
}
