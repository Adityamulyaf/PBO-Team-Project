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
        int roomWidth = 100;
        int roomHeight = 80;
        int tallRoomHeight = roomHeight * 2 + roomGap;
        
        // --- Koordinat Awal ---
        int currentX = margin;
        int currentY = margin;
        
        // --- BARIS ATAS ---
        // R.B4.11 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.11", currentX, currentY, roomWidth, roomHeight, true, "Peminjaman"));
        
        currentX += roomWidth + roomGap;
        
        // R.B4.08 (Tegak, Hijau/Available)
        ruanganList.add(new RoomData("R.B4.08", currentX, currentY, roomWidth / 2, tallRoomHeight, true, "Peminjaman"));
        
        currentX += (roomWidth / 2) + roomGap;
        
        // Blok Abu-abu 1 (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, roomWidth, roomHeight, false, "Non-Peminjaman"));
        
        currentX += roomWidth + roomGap;
        
        // Blok Abu-abu 2 (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, roomWidth, roomHeight, false, "Non-Peminjaman"));
        
        currentX += roomWidth + roomGap;
        
        // Lobby (Hitam, Tegak)
        ruanganList.add(new RoomData("Lobby", currentX, currentY, roomWidth, tallRoomHeight, false, "Lobby"));
        
        currentX += roomWidth + roomGap;
        
        // Blok Abu-abu 3 (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, roomWidth / 2, roomHeight, false, "Non-Peminjaman"));
        
        currentX += (roomWidth / 2) + roomGap;
        
        // Blok Abu-abu 4 (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, roomWidth / 2, roomHeight, false, "Non-Peminjaman"));
        
        currentX += (roomWidth / 2) + roomGap;
        
        // R.B4.04 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.04", currentX, currentY, roomWidth, roomHeight, true, "Peminjaman"));
        
        currentX += roomWidth + roomGap;
        
        // R.B4.05 (Tegak, Hijau/Available)
        int r405X = currentX;
        ruanganList.add(new RoomData("R.B4.05", currentX, currentY, roomWidth / 2, tallRoomHeight, true, "Peminjaman"));
        
        currentX += (roomWidth / 2) + margin * 2;
        
        // Lab TIK 1 (Hijau/Available)
        ruanganList.add(new RoomData("Lab TIK 1", currentX, currentY, roomWidth, roomHeight, true, "Peminjaman"));
        
        currentX += roomWidth + roomGap;
        
        // Lab TIK 2 (Hijau/Available)
        ruanganList.add(new RoomData("Lab TIK 2", currentX, currentY, roomWidth, roomHeight, true, "Peminjaman"));
        
        // --- BARIS BAWAH ---
        currentX = margin;
        currentY += roomHeight + roomGap;
        
        // R.B4.10 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.10", currentX, currentY, roomWidth, roomHeight, true, "Peminjaman"));
        
        currentX += roomWidth + roomGap;
        
        // Skip R.B4.08 (sudah tegak)
        currentX += (roomWidth / 2) + roomGap;
        
        // Blok Abu-abu 5 (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, roomWidth, roomHeight, false, "Non-Peminjaman"));
        
        currentX += roomWidth + roomGap;
        
        // Blok Abu-abu 6 (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, roomWidth, roomHeight, false, "Non-Peminjaman"));
        
        currentX += roomWidth + roomGap;
        
        // Skip Lobby (sudah tegak)
        currentX += roomWidth + roomGap;
        
        // Blok Abu-abu 7 (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, roomWidth / 2, roomHeight / 2, false, "Non-Peminjaman"));
        
        // Blok Abu-abu 8 (Non-Peminjaman) - di bawah abu 7
        ruanganList.add(new RoomData("", currentX, currentY + (roomHeight / 2) + roomGap / 2, roomWidth / 2, roomHeight / 2 - roomGap / 2, false, "Non-Peminjaman"));
        
        currentX += (roomWidth / 2) + roomGap;
        
        // Blok Abu-abu 9 (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, roomWidth / 2, roomHeight / 2, false, "Non-Peminjaman"));
        
        currentX += (roomWidth / 2) + roomGap;
        
        // R.B4.06 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.06", currentX, currentY, roomWidth / 2, roomHeight, true, "Peminjaman"));
        
        currentX += (roomWidth / 2) + roomGap;
        
        // R.B4.12 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.12", currentX, currentY, roomWidth / 2, roomHeight, true, "Peminjaman"));
        
        currentX = r405X + (roomWidth / 2) + margin * 2;
        
        // Lab TIK 3 (Hijau/Available)
        ruanganList.add(new RoomData("Lab TIK 3", currentX, currentY, roomWidth, roomHeight, true, "Peminjaman"));
        
        currentX += roomWidth + roomGap;
        
        // Lab TIK 4 (Hijau/Available)
        ruanganList.add(new RoomData("Lab TIK 4", currentX, currentY, roomWidth, roomHeight, true, "Peminjaman"));
        
        // Menyesuaikan ukuran panel
        setPreferredSize(new Dimension(currentX + roomWidth + margin, currentY + roomHeight + margin)); 

        // --- Mouse Listener --- 
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleRoomClick(e.getX(), e.getY());
            }
        });
    }

    // --- Metode untuk Menggambar Komponen ---
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
        for (RoomData room : ruanganList) {
            
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

    // --- Metode Penanganan Klik Mouse ---
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