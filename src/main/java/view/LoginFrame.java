// view/LoginFrame.java

package view;

import com.formdev.flatlaf.FlatLightLaf;
import controller.DatabaseManager;
import model.User;
import model.UserRole;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JRadioButton adminRadio;
    private JRadioButton mahasiswaRadio;

    public LoginFrame() {
        setTitle("Panggon FATISDA - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Booking Kelas FATISDA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Role Selection
        JLabel roleLabel = new JLabel("Login sebagai:");
        roleLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(roleLabel, gbc);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        rolePanel.setBackground(Color.WHITE);
        adminRadio = new JRadioButton("Admin");
        mahasiswaRadio = new JRadioButton("Mahasiswa", true);
        adminRadio.setFont(UIConstants.LABEL_FONT);
        mahasiswaRadio.setFont(UIConstants.LABEL_FONT);
        adminRadio.setBackground(Color.WHITE);
        mahasiswaRadio.setBackground(Color.WHITE);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(adminRadio);
        roleGroup.add(mahasiswaRadio);
        rolePanel.add(adminRadio);
        rolePanel.add(mahasiswaRadio);

        gbc.gridy = 1;
        formPanel.add(rolePanel, gbc);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        formPanel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(usernameField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(UIConstants.BUTTON_FONT);
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBackground(new Color(59, 130, 246));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> handleLogin());

        JButton cancelButton = new JButton("Batal");
        cancelButton.setFont(UIConstants.BUTTON_FONT);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Enter key support
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Username dan password tidak boleh kosong!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = DatabaseManager.authenticateUser(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, 
                "Username atau password salah!", 
                "Login Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Success
        dispose();
        
        if (user.getRole() == UserRole.ADMIN) {
            SwingUtilities.invokeLater(() -> new AdminFrame(user).setVisible(true));
        } else {
            SwingUtilities.invokeLater(() -> new MahasiswaFrame(user).setVisible(true));
        }
    }

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
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}