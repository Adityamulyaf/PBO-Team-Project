// view/MainMahasiswa.java

package view;

import com.formdev.flatlaf.FlatLightLaf;
import controller.DatabaseManager;
import model.User;
import model.UserRole;

import javax.swing.*;

public class MainMahasiswa {
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
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            
            JLabel titleLabel = new JLabel("Login Mahasiswa");
            titleLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));
            titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            panel.add(titleLabel);
            panel.add(Box.createVerticalStrut(15));
            
            JPanel userPanel = new JPanel();
            JTextField userField = new JTextField(15);
            userField.setText("mhs");
            userPanel.add(new JLabel("Username:"));
            userPanel.add(userField);
            panel.add(userPanel);
            
            JPanel passPanel = new JPanel();
            JPasswordField passField = new JPasswordField(15);
            passField.setText("mhs123");
            passPanel.add(new JLabel("Password: "));
            passPanel.add(passField);
            panel.add(passPanel);
            
            int result = JOptionPane.showConfirmDialog(null, panel, 
                "Booking Kelas FATISDA", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                String username = userField.getText().trim();
                String password = new String(passField.getPassword());
                
                User user = DatabaseManager.authenticateUser(username, password);
                
                if (user != null && user.getRole() == UserRole.MAHASISWA) {
                    MahasiswaFrame frame = new MahasiswaFrame(user);
                    frame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null,
                        "Login gagal! Username atau password salah.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            } else {
                System.exit(0);
            }
        });
    }
}