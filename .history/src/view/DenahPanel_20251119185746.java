package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// Asumsi: Kita akan menggunakan kelas ini untuk merepresentasikan data ruangan
class RoomData {
    String nama;
    int x;
    int y;
    int width;
    int height;
    // Status bisa diambil dari Controller/Model secara real-time
    boolean isAvailable; 

    public RoomData(String nama, int x, int y, int w, int h, boolean available) {
        this.nama = nama;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.isAvailable = available;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}


public class DenahPanel extends JPanel {

    private final String userRole;
    private final List<RoomData> ruanganList;
    private final int roomSpacing = 10;
    private final int margin = 30;

    public DenahPanel(String role) {
        this.userRole = role;
        
        // --- 1. Inisialisasi Data Ruangan Dummy ---
        ruanganList = new ArrayList<>();
        int currentX = margin;
        int currentY = margin;
        int roomSize = 100;
        
        // Baris 1 Ruangan (Contoh Lab Komputer dan Ruang Kelas)
        ruanganList.add(new RoomData("Lab A1", currentX, currentY, 150, roomSize, false)); // Terisi (Merah)
        currentX += 150 + roomSpacing;
        ruanganList.add(new RoomData("R. Kuliah B1", currentX, currentY, roomSize, roomSize, true)); // Kosong (Hijau)
        currentX += roomSize + roomSpacing;
        ruanganList.add(new RoomData("R. Kuliah B2", currentX, currentY, roomSize, roomSize, true)); // Kosong (Hijau)
        currentX += roomSize + roomSpacing;
        ruanganList.add(new RoomData("R. Dosen", currentX, currentY, 120, roomSize, false)); // Terisi (Merah)
        
        // Pindah ke Baris 2
        currentX = margin;
        currentY += roomSize + roomSpacing + 20; // Tambah jarak antar lantai/koridor
        
        ruanganList.add(new RoomData("R. Sidang C1", currentX, currentY, 150, 90, true)); // Kosong (Hijau)
        currentX += 150 + roomSpacing;
        ruanganList.add(new RoomData("Lab B3", currentX, currentY, 150, 90, false)); // Terisi (Merah)
        currentX += 150 + roomSpacing;
        ruanganList.add(new RoomData("R. Kuliah C2", currentX, currentY, roomSize, 90, false)); // Terisi (Merah)
        
        // Atur ukuran panel berdasarkan ruangan yang digambar
        setPreferredSize(new Dimension(currentX + margin, currentY + 90 + margin)); 

        // --- 2. Tambahkan Mouse Listener ---
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
        
        // Garis Koridor/Lantai
        g2d.setColor(Color.LIGHT_GRAY.darker());
        g2d.fillRect(0, 0, getWidth(), margin + 150);
        g2d.fillRect(0, margin + 150 + roomSpacing + 20, getWidth(), 150);
        g2d.setColor(Color.WHITE);
        g2d.drawString("KORIDOR LANTAI 1", margin, margin / 2);

        // Iterasi dan Gambar Setiap Ruangan
        for (RoomData room : ruanganList) {
            // Tentukan Warna Berdasarkan Ketersediaan
            if (room.isAvailable) {
                g2d.setColor(new Color(60, 179, 113)); // Medium Sea Green (Hijau Terang)
            } else {
                g2d.setColor(new Color(255, 99, 71)); // Tomato (Merah Terisi)
            }
            
            // Isi Ruangan
            g2d.fill(room.getBounds());
            
            // Border Ruangan
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2)); // Ketebalan garis
            g2d.draw(room.getBounds());
            
            // Nama Ruangan
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(Color.WHITE); 
            // Posisi teks di tengah ruangan
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
                // Ruangan ditemukan berdasarkan koordinat klik
                String roomName = room.nama;
                
                // --- Aksi Berdasarkan Status Ruangan dan Peran Pengguna ---
                if (!room.isAvailable) { 
                    // Ruangan Terisi (Merah)
                    String detail = "Dipakai oleh: Kelas I005 (Algoritma) \nJam: 10:00 - 12:30 \nKeperluan: Perkuliahan Reguler";
                    
                    if (userRole.equals("Admin")) {
                        // Admin punya opsi untuk mengubah jadwal yang terisi
                        String[] options = {"Ubah Jadwal", "Lihat Detail", "Tutup"};
                        int choice = JOptionPane.showOptionDialog(this, detail, "Kelola " + roomName, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);
                        
                        if (choice == 0) {
                            JOptionPane.showMessageDialog(this, "Admin membuka Form Ubah Jadwal untuk " + roomName);
                        }
                    } else {
                        // Mahasiswa hanya bisa melihat detail
                        JOptionPane.showMessageDialog(this, detail, "Info Ruangan Terisi", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    // Ruangan Kosong (Hijau)
                    if (userRole.equals("Mahasiswa")) {
                        // Mahasiswa: Pop-up Konfirmasi Pinjam
                        int response = JOptionPane.showConfirmDialog(this, "Ruangan " + roomName + " tersedia. \nAjukan peminjaman sekarang?", "Pinjam Ruangan", JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            // Lanjutkan ke Form Peminjaman (Tanggal, Jam, Keperluan)
                            // Anda harus memanggil form/controller di sini
                            JOptionPane.showMessageDialog(this, "Buka Form Input Peminjaman untuk " + roomName + "...");
                        }
                    } else if (userRole.equals("Admin")) {
                        // Admin: Pop-up untuk membuat jadwal baru
                        int response = JOptionPane.showConfirmDialog(this, "Ruangan " + roomName + " kosong. \nBuat jadwal baru (manual)?", "Buat Jadwal Admin", JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            JOptionPane.showMessageDialog(this, "Admin membuka Form Input Jadwal Baru untuk " + roomName + "...");
                        }
                    }
                }
                // Hentikan perulangan setelah menemukan ruangan yang diklik
                return; 
            }
        }
    }
}
