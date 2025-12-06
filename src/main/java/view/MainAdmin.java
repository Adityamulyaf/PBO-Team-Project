// view/MainAdmin.java

package view;

import com.formdev.flatlaf.FlatLightLaf;
import controller.DatabaseManager;
import model.User;
import model.UserRole;

import javax.swing.*;

public class MainAdmin {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseManager.initialize();

        SwingUtilities.invokeLater(() -> {
            User admin = DatabaseManager.authenticateUser("admin", "admin123");
            
            if (admin != null && admin.getRole() == UserRole.ADMIN) {
                AdminFrame frame = new AdminFrame(admin);
                frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                    "Akun admin default tidak ditemukan! Silakan gunakan LoginFrame.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}