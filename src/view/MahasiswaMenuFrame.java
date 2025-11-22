
package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MahasiswaMenuFrame extends JFrame implements ActionListener {

    private final JButton pinjamButton;
    private final JButton logoutButton;
    private final DenahPanel denahPanel; // Panel yang akan menampilkan denah

    public MahasiswaMenuFrame() {
        // --- 1. Pengaturan Jendela ---
        setTitle("Menu Mahasiswa - Peminjaman Ruangan FITS UNS");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 2. Panel Denah ---
        // Asumsi DenahPanel sudah ada dan akan menangani gambar denah
        denahPanel = new DenahPanel("Mahasiswa"); 
        JScrollPane scrollPane = new JScrollPane(denahPanel);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Panel Kontrol (Utara) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // Tombol Pinjam Ruangan
        pinjamButton = new JButton("Ajukan Peminjaman");
        pinjamButton.setBackground(Color.GREEN.darker());
        pinjamButton.setForeground(Color.WHITE);
        pinjamButton.addActionListener(this);
        controlPanel.add(pinjamButton);
        
        // Tombol Logout
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        controlPanel.add(logoutButton);
        
        add(controlPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pinjamButton) {
            JOptionPane.showMessageDialog(this, "Aksi: Membuka Form Pengajuan Peminjaman.");
            // Logika: 
            // 1. Meminta input jam/tanggal peminjaman global.
            // 2. Atau menunggu mahasiswa mengklik ruangan berwarna Hijau di DenahPanel.
            // denahPanel.setPinjamMode(true); // Contoh mengaktifkan mode klik pinjam
        
        } else if (e.getSource() == logoutButton) {
            JOptionPane.showMessageDialog(this, "Anda telah Logout.");
            this.dispose(); // Tutup frame ini
            new LoginFrame().setVisible(true); // Kembali ke Login
        }
    }
    
    // Metode main untuk testing (Opsional, hapus jika sudah terintegrasi)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MahasiswaMenuFrame::new);
    }
}