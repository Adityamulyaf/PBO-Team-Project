package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// RoomData class DENGAN penambahan field 'type' untuk membedakan Peminjaman/Non-Peminjaman/Lobby
class RoomData {
    String nama;
    int x, y, width, height;
    boolean isAvailable;
    String type; 

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
    private final int roomGap = 5; // Jarak antar ruangan
    private final int margin = 20;

    public DenahPanel(String role) {
        this.userRole = role;
        
        ruanganList = new ArrayList<>();
        
        // --- Ukuran Dasar Ruangan ---
        int baseRoomWidth = 100;
        int baseRoomHeight = 100;
        
        // Hitung total lebar untuk centering
        int totalWidth = 1200;
        int totalHeight = 350;
        
        // Offset untuk centering
        int startX = 50;
        int startY = 100;
        
        // --- Koordinat Awal ---
        int currentX = startX;
        int currentY = startY;
        
        // --- Sisi Kiri Denah ---
        // R.B4.11 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.11", currentX, currentY, baseRoomWidth, baseRoomHeight, true, "Peminjaman"));
        // R.B4.10 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.10", currentX, currentY + baseRoomHeight + roomGap, baseRoomWidth, baseRoomHeight, true, "Peminjaman"));
        
        currentX += baseRoomWidth + roomGap;
        
        // R.B4.08 (Tegak) (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.08", currentX, currentY, 60, (baseRoomHeight * 2) + roomGap, true, "Peminjaman"));
        
        currentX += 60 + roomGap; 
        
        // --- Area Tengah (Abu-abu & Lobby) ---
        // Blok Abu-abu Kiri (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, baseRoomWidth, baseRoomHeight, false, "Non-Peminjaman"));
        ruanganList.add(new RoomData("", currentX, currentY + baseRoomHeight + roomGap, baseRoomWidth, baseRoomHeight, false, "Non-Peminjaman"));
        
        currentX += baseRoomWidth + roomGap;
        
        // Blok Abu-abu Tengah Kiri (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, baseRoomWidth, baseRoomHeight, false, "Non-Peminjaman"));
        ruanganList.add(new RoomData("", currentX, currentY + baseRoomHeight + roomGap, baseRoomWidth, baseRoomHeight, false, "Non-Peminjaman"));
        
        currentX += baseRoomWidth + roomGap;
        
        // Lobby (Hitam)
        ruanganList.add(new RoomData("Lobby", currentX, currentY, baseRoomWidth, (baseRoomHeight * 2) + roomGap, false, "Lobby"));
        
        currentX += baseRoomWidth + roomGap;
        
        // Blok Abu-abu Tengah Kanan (Non-Peminjaman)
        int tempX = currentX;
        ruanganList.add(new RoomData("", tempX, currentY, 45, 48, false, "Non-Peminjaman")); 
        ruanganList.add(new RoomData("", tempX, currentY + 48 + roomGap, 45, 48, false, "Non-Peminjaman")); 
        ruanganList.add(new RoomData("ii", tempX, currentY + baseRoomHeight + roomGap, 45, baseRoomHeight, false, "Non-Peminjaman"));
        
        tempX += 45 + roomGap;
        
        // Blok Abu-abu Kanan Kecil (Non-Peminjaman)
        ruanganList.add(new RoomData("ff", tempX, currentY, 45, baseRoomHeight, false, "Non-Peminjaman")); 
        ruanganList.add(new RoomData("hh", tempX, currentY + baseRoomHeight + roomGap, 45, baseRoomHeight, false, "Non-Peminjaman"));
        
        currentX = tempX + 45 + roomGap;
        
        // --- Sisi Kanan Denah ---
        // R.B4.04 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.04", currentX, currentY, baseRoomWidth, baseRoomHeight, true, "Peminjaman"));
        
        // R.B4.06 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.06", currentX, currentY + baseRoomHeight + roomGap, 47, baseRoomHeight, true, "Peminjaman"));
        
        // R.B4.12 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.12", currentX + 47 + roomGap, currentY + baseRoomHeight + roomGap, 48, baseRoomHeight, true, "Peminjaman"));
        
        currentX += baseRoomWidth + roomGap;

        // R.B4.05 (Tegak) (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.05", currentX, currentY, 100, (baseRoomHeight * 2) + roomGap, true, "Peminjaman"));
        
        int labTikX = currentX + 100 + roomGap + 20;
        int labTikY = currentY;
        
        // --- Area Lab TIK (Pojok Kanan) ---
        // Lab TIK 1 (Hijau/Available)
        ruanganList.add(new RoomData("Lab TIK 1", labTikX, labTikY, baseRoomWidth, baseRoomHeight, true, "Peminjaman"));
        // Lab TIK 2 (Hijau/Available)
        ruanganList.add(new RoomData("Lab TIK 2", labTikX + baseRoomWidth + roomGap, labTikY, baseRoomWidth, baseRoomHeight, true, "Peminjaman"));
        
        labTikY += baseRoomHeight + roomGap;
        
        // Lab TIK 3 (Hijau/Available)
        ruanganList.add(new RoomData("Lab TIK 3", labTikX, labTikY, baseRoomWidth, baseRoomHeight, true, "Peminjaman"));
        // Lab TIK 4 (Hijau/Available)
        ruanganList.add(new RoomData("Lab TIK 4", labTikX + baseRoomWidth + roomGap, labTikY, baseRoomHeight, baseRoomHeight, true, "Peminjaman"));
        
        // Menyesuaikan ukuran panel agar semua ruangan terlihat
        setPreferredSize(new Dimension(totalWidth, totalHeight)); 

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
        g2d.setStroke(new BasicStroke(2)); 

        // Latar Belakang Putih
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Gambar Setiap Ruangan
        for (RoomData room : ruanganList) {shutd
            
            Color roomColor;
            Color textColor = Color.WHITE; 

            switch (room.type) {
                case "Lobby":
                    roomColor = Color.BLACK;
                    break;
                case "Non-Peminjaman":
                    roomColor = Color.GRAY;
                    textColor = Color.BLACK; 
                    break;
                case "Peminjaman":
                    if (room.isAvailable) {
                        roomColor = new Color(0, 204, 0); // Hijau cerah
                    } else {
                        roomColor = new Color(255, 51, 51); // Merah cerah
                    }
                    break;
                default:
                    roomColor = Color.LIGHT_GRAY; 
                    break;
            }
            
            g2d.setColor(roomColor);
            g2d.fill(room.getBounds());
            
            // Border Ruangan
            g2d.setColor(Color.BLACK);
            g2d.draw(room.getBounds());
            
            // Nama Ruangan (jika ada)
            if (!room.nama.isEmpty()) {
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.setColor(textColor); 
                
                FontMetrics fm = g2d.getFontMetrics();
                int strX = room.x + (room.width - fm.stringWidth(room.nama)) / 2;
                int strY = room.y + ((room.height - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(room.nama, strX, strY);
            }
        }
    }

    // --- 4. Metode Penanganan Klik Mouse ---
    private void handleRoomClick(int x, int y) {
        
        for (RoomData room : ruanganList) {
            if (room.getBounds().contains(x, y)) {
                // Hanya ruangan "Peminjaman" yang bisa berinteraksi untuk peminjaman/jadwal
                if (room.type.equals("Peminjaman")) {
                    String roomName = room.nama;
                    
                    if (!room.isAvailable) { 
                        // Ruangan Terisi (Merah)
                        // Data detail ini harus diambil dari database
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
                } else if (room.type.equals("Lobby")) {
                    JOptionPane.showMessageDialog(this, "Ini adalah area Lobby.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                } else if (room.type.equals("Non-Peminjaman")) {
                    JOptionPane.showMessageDialog(this, "Ini adalah area non-peminjaman (Dosen/Administrasi).", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
                return; 
            }
        }
    }
}