package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// RoomData class tetap sama (tidak perlu diubah)
class RoomData {
    String nama;
    int x, y, width, height;
    boolean isAvailable;
    String type; // Tambahan untuk membedakan type Ruang Kuliah, Lab, atau Lainnya

    public RoomData(String nama, int x, int y, int w, int h, boolean available, String type) {
        this.nama = nama;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.isAvailable = available;
        this.type = type;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}


public class DenahPanel extends JPanel {

    private final String userRole;
    private final List<RoomData> ruanganList;
    private final int margin = 20;

    public DenahPanel(String role) {
        this.userRole = role;
        
        // --- 1. Inisialisasi Data Ruangan FITS Lantai 4 ---
        ruanganList = new ArrayList<>();
        
        // Ukuran Dasar yang Diperkirakan (berdasarkan rasio)
        final int wDosen = 70;
        final int hKls = 150;
        final int hLab = 150;
        
        // --- BLOK KIRI (Area Ruang Kuliah & Dosen) ---
        int currentX = margin;
        
        // R-B.411 & R-B.410 (Ruang Kuliah) - Sisi Paling Kiri
        ruanganList.add(new RoomData("R-B.411", currentX, margin, 70, 80, true, "Kuliah")); // KOSONG (HIJAU)
        ruanganList.add(new RoomData("R-B.410", currentX, margin + 80, 70, 80, false, "Kuliah")); // TERISI (MERAH)
        currentX += 70;
        
        // R-B.409 (Ruang Dosen 3 / Koridor Dosen) - Tengah
        ruanganList.add(new RoomData("R-B.409", currentX, margin + 80, 150, 80, false, "Lain")); // Dosen (ABU-ABU)
        
        // R-B.413b (Ruang Dosen 3) - Tengah
        ruanganList.add(new RoomData("R-B.413b", currentX, margin, 150, 80, false, "Lain")); // Dosen (ABU-ABU)

        // R-B.409 & R-B.402 (Ruang Pimpinan & Dosen 2)
        ruanganList.add(new RoomData("R-B.402", currentX + 150, margin, 70, 80, false, "Lain")); // Pimpinan (ABU-ABU)
        currentX += 150 + 70;
        
        // R-B.413a (Ruang Dosen 2) - Atas
        ruanganList.add(new RoomData("R-B.413a", currentX - 150, margin, 70, 80, false, "Lain")); // Dosen (ABU-ABU)
        
        // R-B.409 - (Area Koridor Dosen/Tengah)
        ruanganList.add(new RoomData("R-B.409", currentX - 220, margin + 80, 220, 80, false, "Lain")); // Dosen (ABU-ABU)
        
        // --- BLOK KANAN (Area Laboratorium & Kuliah) ---
        currentX = 400; // Mulai di sisi Kanan setelah area tengah/tangga
        
        // R-B.401, R-B.407 (Ruang Admin & Dosen 1)
        ruanganList.add(new RoomData("R-B.401", currentX, margin, 70, 80, false, "Lain")); // Admin 1 (ABU-ABU)
        ruanganList.add(new RoomData("R-B.407", currentX, margin + 80, 70, 80, false, "Lain")); // Admin 2 (ABU-ABU)
        currentX += 70;
        
        // R-B.403a & R-B.403b (Ruang Rapat/Dosen 1)
        ruanganList.add(new RoomData("R-B.403a", currentX, margin, 70, 80, false, "Lain")); // Rapat (ABU-ABU)
        ruanganList.add(new RoomData("R-B.403b", currentX, margin + 80, 70, 80, false, "Lain")); // Dosen 1 (ABU-ABU)
        currentX += 70;

        // R-B.404 & R-B.405 (Laboratorium Komputer)
        ruanganList.add(new RoomData("R-B.404", currentX, margin, 100, 80, true, "Lab")); // KOSONG (HIJAU)
        ruanganList.add(new RoomData("R-B.405", currentX + 100, margin, 70, 80, false, "Lab")); // TERISI (MERAH)
        
        // R-B.406, R-B.412, R-B.408 (Lab Penelitian & Ruang Kuliah)
        ruanganList.add(new RoomData("R-B.406", currentX, margin + 80, 70, 80, true, "Kuliah")); // KOSONG (HIJAU)
        ruanganList.add(new RoomData("R-B.408", currentX + 70, margin + 80, 70, 80, false, "Lab")); // TERISI (MERAH)
        ruanganList.add(new RoomData("R-B.412", currentX + 140, margin + 80, 70, 80, true, "Kuliah")); // KOSONG (HIJAU)

        // Penyesuaian Ukuran Panel
        setPreferredSize(new Dimension(550, 350)); 

        // --- 2. Tambahkan Mouse Listener --- (Tetap Sama)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleRoomClick(e.getX(), e.getY());
            }
        });
    }

    // --- 3. Metode untuk Menggambar Komponen ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Latar Belakang (Denah B Lantai 4 FITS UNS)
        g2d.setColor(new Color(240, 240, 240)); 
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Koridor Utama (Area Tengah yang memanjang)
        g2d.setColor(new Color(200, 200, 200)); // Warna Koridor
        g2d.fillRect(0, 160, getWidth(), 60); 
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("KORIDOR UTAMA LANTAI 4", margin + 180, 195);
        
        // Gambar Area Tangga/Toilet (Pusat)
        g2d.setColor(new Color(150, 150, 150));
        g2d.fillRect(300, margin, 100, 140); 
        g2d.setColor(Color.WHITE);
        g2d.drawString("TANGGA/LIFT", 310, margin + 30);
        g2d.drawString("TOILET", 320, margin + 90);

        // Iterasi dan Gambar Setiap Ruangan
        for (RoomData room : ruanganList) {
            
            // --- Logika Penentuan Warna ---
            Color roomColor;
            if (room.type.equals("Lain")) {
                roomColor = new Color(170, 170, 170); // Abu-abu untuk Ruang Admin/Dosen/Pimpinan
            } else if (room.isAvailable) {
                roomColor = new Color(60, 179, 113); // Hijau untuk Kuliah/Lab Tersedia
            } else {
                roomColor = new Color(255, 99, 71); // Merah untuk Kuliah/Lab Terisi
            }
            
            g2d.setColor(roomColor);
            g2d.fill(room.getBounds());
            
            // Border Ruangan
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1)); 
            g2d.draw(room.getBounds());
            
            // Nama Ruangan
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.setColor(room.type.equals("Lain") ? Color.BLACK : Color.WHITE); // Teks hitam jika abu-abu, putih jika berwarna
            
            FontMetrics fm = g2d.getFontMetrics();
            int strX = room.x + (room.width - fm.stringWidth(room.nama)) / 2;
            int strY = room.y + ((room.height - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(room.nama, strX, strY);
        }
    }

    // --- 4. Metode Penanganan Klik Mouse ---
    private void handleRoomClick(int x, int y) {
        
        for (RoomData room : ruanganList) {
            if (room.getBounds().contains(x, y)) {
                // HANYA RUANG KULIAH/LAB YANG BISA DI-KLIK UNTUK DIPINJAM
                if (room.type.equals("Kuliah") || room.type.equals("Lab")) {
                    String roomName = room.nama;
                    
                    if (!room.isAvailable) { 
                        // Ruangan Terisi (Merah)
                        String detail = "Dipakai oleh: Kelas I005 (Algoritma) \nJam: 10:00 - 12:30 \nKeperluan: Perkuliahan Reguler";
                        
                        if (userRole.equals("Admin")) {
                             String[] options = {"Ubah Jadwal", "Lihat Detail", "Tutup"};
                             int choice = JOptionPane.showOptionDialog(this, detail, "Kelola " + roomName, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);
                             if (choice == 0) {
                                 // Panggil Form Ubah Jadwal
                                 JOptionPane.showMessageDialog(this, "Admin membuka Form Ubah Jadwal untuk " + roomName);
                             }
                        } else {
                             // Mahasiswa hanya bisa melihat detail
                             JOptionPane.showMessageDialog(this, detail, "Info Ruangan Terisi", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        // Ruangan Kosong (Hijau)
                        if (userRole.equals("Mahasiswa")) {
                            int response = JOptionPane.showConfirmDialog(this, "Ruangan " + roomName + " tersedia. \nAjukan peminjaman sekarang?", "Pinjam Ruangan", JOptionPane.YES_NO_OPTION);
                            if (response == JOptionPane.YES_OPTION) {
                                // Panggil Form Peminjaman
                                JOptionPane.showMessageDialog(this, "Buka Form Input Peminjaman untuk " + roomName + "...");
                            }
                        } else if (userRole.equals("Admin")) {
                            int response = JOptionPane.showConfirmDialog(this, "Ruangan " + roomName + " kosong. \nBuat jadwal baru (manual)?", "Buat Jadwal Admin", JOptionPane.YES_NO_OPTION);
                            if (response == JOptionPane.YES_OPTION) {
                                // Panggil Form Input Jadwal Admin
                                JOptionPane.showMessageDialog(this, "Admin membuka Form Input Jadwal Baru untuk " + roomName + "...");
                            }
                        }
                    }
                } else {
                    // Ruangan non-Kuliah/Lab yang diklik (Admin/Dosen/Toilet)
                    JOptionPane.showMessageDialog(this, room.nama + " adalah area non-peminjaman.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
                return; 
            }
        }
    }
}