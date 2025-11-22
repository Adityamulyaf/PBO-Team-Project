package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminMenuFrame extends JFrame implements ActionListener {

    private final JButton kelolaJadwalButton;
    private final JButton logoutButton;
    private final DenahPanel denahPanel; // Panel yang akan menampilkan denah

    public AdminMenuFrame() {
        // --- 1. Pengaturan Jendela ---
        setTitle("Menu Admin - Peminjaman Ruangan FITS UNS");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 2. Panel Denah ---
        denahPanel = new DenahPanel("Admin"); 
        JScrollPane scrollPane = new JScrollPane(denahPanel);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Panel Kontrol (Utara) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // Tombol Kelola Jadwal (Hak Akses Admin)
        kelolaJadwalButton = new JButton("Kelola Jadwal Ruangan");
        kelolaJadwalButton.setBackground(Color.RED.darker());
        kelolaJadwalButton.setForeground(Color.WHITE);
        kelolaJadwalButton.addActionListener(this);
        controlPanel.add(kelolaJadwalButton);
        
        // Tombol Logout
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        controlPanel.add(logoutButton);
        
        add(controlPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == kelolaJadwalButton) {
            JOptionPane.showMessageDialog(this, "Aksi: Membuka Fitur Input/Ubah Jadwal Admin.");
            // Logika: 
            // 1. Admin mungkin diminta input tanggal/jam untuk melihat status spesifik.
            // 2. Atau mengaktifkan mode klik di DenahPanel, di mana klik ruangan akan membuka form Kelola Jadwal.
            // denahPanel.setKelolaMode(true);
            
        } else if (e.getSource() == logoutButton) {
            JOptionPane.showMessageDialog(this, "Anda telah Logout.");
            this.dispose(); 
            new LoginFrame().setVisible(true); // Kembali ke Login
        }
    }
}