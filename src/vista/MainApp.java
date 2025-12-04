package vista;

import javax.swing.*;
import java.awt.event.*;


public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {}

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

}
