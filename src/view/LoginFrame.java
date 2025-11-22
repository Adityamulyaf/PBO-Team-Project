package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.LoginController; // Asumsi Anda punya Controller untuk menangani logika login

public class LoginFrame extends JFrame implements ActionListener {

    // --- Deklarasi Komponen GUI ---
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton adminLoginButton;
    private final JButton mahasiswaLoginButton;
    private final LoginController controller;

    // --- Konstruktor ---
    public LoginFrame() {
        // Inisialisasi Controller
        controller = new LoginController();

        // 1. Pengaturan Jendela (Frame)
        setTitle("Aplikasi Peminjaman Ruangan - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Menempatkan frame di tengah layar
        setLayout(new BorderLayout(10, 10)); // Layout utama

        // 2. Panel Tengah (Form)
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // Layout 3 baris, 2 kolom
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Judul
        JLabel titleLabel = new JLabel("Login FITS UNS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Baris 1: Username / NIM
        formPanel.add(new JLabel("  Username/NIM:"));
        usernameField = new JTextField(15);
        formPanel.add(usernameField);

        // Baris 2: Password
        formPanel.add(new JLabel("  Password:"));
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.CENTER);

        // 3. Panel Bawah (Tombol)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        // Tombol Login Admin
        adminLoginButton = new JButton("Login Admin");
        adminLoginButton.setBackground(new Color(255, 100, 100)); // Warna merah muda
        adminLoginButton.setForeground(Color.WHITE);
        adminLoginButton.addActionListener(this);
        buttonPanel.add(adminLoginButton);

        // Tombol Login Mahasiswa
        mahasiswaLoginButton = new JButton("Login Mahasiswa");
        mahasiswaLoginButton.setBackground(new Color(100, 150, 255)); // Warna biru muda
        mahasiswaLoginButton.setForeground(Color.WHITE);
        mahasiswaLoginButton.addActionListener(this);
        buttonPanel.add(mahasiswaLoginButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Tampilkan Jendela
        setVisible(true);
    }

    // --- Metode Penanganan Aksi (Saat Tombol Diklik) ---
    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        // Mengambil password dari JPasswordField.getPassword() dan mengubahnya ke String
        String password = new String(passwordField.getPassword());
        String role = "";

        if (e.getSource() == adminLoginButton) {
            role = "Admin";
        } else if (e.getSource() == mahasiswaLoginButton) {
            role = "Mahasiswa";
        }

        // --- Logika Login Diteruskan ke Controller ---
        boolean success = controller.authenticate(username, password, role);

        if (success) {
            JOptionPane.showMessageDialog(this, "Login " + role + " Berhasil!");
            this.dispose(); // Tutup jendela login
            
            // Buka jendela utama sesuai peran
            if (role.equals("Admin")) {
                 new AdminMenuFrame().setVisible(true); // Ganti dengan kelas AdminMenuFrame Anda
            } else {
                 new MahasiswaMenuFrame().setVisible(true); // Ganti dengan kelas MahasiswaMenuFrame Anda
            }

        } else {
            JOptionPane.showMessageDialog(this, "Login Gagal. Cek kembali Username/NIM dan Password Anda.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Metode Main untuk Menjalankan Aplikasi ---
    public static void main(String[] args) {
        // Runnable untuk memastikan GUI dijalankan di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}