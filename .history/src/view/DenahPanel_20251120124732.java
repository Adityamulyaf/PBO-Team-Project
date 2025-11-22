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
    String type; // "Peminjaman", "Non-Peminjaman", "Lobby"

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
        
        // --- Ukuran Dasar Ruangan (Disatukan untuk kerapian) ---
        int baseRoomWidth = 90;
        int baseRoomHeight = 70;
        
        // --- Koordinat Awal ---
        int currentX = margin;
        int currentY = margin;
        
        // --- Sisi Kiri Denah ---
        // R.B4.11
        ruanganList.add(new RoomData("R.B4.11", currentX, currentY, baseRoomWidth, baseRoomHeight, true, "Peminjaman"));
        // R.B4.10
        ruanganList.add(new RoomData("R.B4.10", currentX, currentY + baseRoomHeight + roomGap, baseRoomWidth, baseRoomHeight, false, "Peminjaman"));
        
        currentX += baseRoomWidth + roomGap;
        
        // R.B4.08 (Tegak)
        ruanganList.add(new RoomData("R.B4.08", currentX, currentY, baseRoomHeight, (baseRoomHeight * 2) + roomGap, true, "Peminjaman"));
        
        currentX += baseRoomHeight + roomGap; // Pindah setelah R.B4.08
        
        // --- Area Tengah (Abu-abu & Lobby) ---
        // Blok Abu-abu Kiri
        ruanganList.add(new RoomData("", currentX, currentY, baseRoomWidth, baseRoomHeight, false, "Non-Peminjaman"));
        ruanganList.add(new RoomData("", currentX, currentY + baseRoomHeight + roomGap, baseRoomWidth, baseRoomHeight, false, "Non-Peminjaman"));
        
        currentX += baseRoomWidth + roomGap;
        
        // Blok Abu-abu Tengah Kiri
        ruanganList.add(new RoomData("", currentX, currentY, baseRoomWidth, baseRoomHeight, false, "Non-Peminjaman"));
        ruanganList.add(new RoomData("", currentX, currentY + baseRoomHeight + roomGap, baseRoomWidth, baseRoomHeight, false, "Non-Peminjaman"));
        
        currentX += baseRoomWidth + roomGap;
        
        // Lobby (Hitam)
        ruanganList.add(new RoomData("Lobby", currentX, currentY, baseRoomWidth, (baseRoomHeight * 2) + roomGap, false, "Lobby"));
        
        currentX += baseRoomWidth + roomGap;
        
        // Blok Abu-abu Tengah Kanan
        ruanganList.add(new RoomData("", currentX, currentY, baseRoomWidth / 2, baseRoomHeight, false, "Non-Peminjaman")); // Kecil atas
        ruanganList.add(new RoomData("", currentX, currentY + baseRoomHeight + roomGap, baseRoomWidth / 2, baseRoomHeight / 2, false, "Non-Peminjaman")); // Kecil bawah
        
        currentX += (baseRoomWidth / 2) + roomGap;
        
        // Blok Abu-abu Kanan Kecil
        ruanganList.add(new RoomData("", currentX, currentY, baseRoomWidth / 2, baseRoomHeight / 2, false, "Non-Peminjaman"));
        ruanganList.add(new RoomData("", currentX, currentY + (baseRoomHeight / 2) + roomGap / 2, baseRoomWidth / 2, baseRoomHeight / 2, false, "Non-Peminjaman"));
        ruanganList.add(new RoomData("", currentX, currentY + baseRoomHeight + roomGap, baseRoomWidth / 2, baseRoomHeight, false, "Non-Peminjaman"));
        
        currentX += (baseRoomWidth / 2) + roomGap;
        
        // --- Sisi Kanan Denah ---
        // R.B4.04
        ruanganList.add(new RoomData("R.B4.04", currentX, currentY, baseRoomWidth, baseRoomHeight, true, "Peminjaman"));
        // R.B4.06
        ruanganList.add(new RoomData("R.B4.06", currentX, currentY + baseRoomHeight + roomGap, baseRoomWidth / 2, baseRoomHeight, true, "Peminjaman"));
        
        currentX += baseRoomWidth + roomGap;
        
        // R.B4.05
        ruanganList.add(new RoomData("R.B4.05", currentX - (baseRoomWidth / 2) - roomGap, currentY + baseRoomHeight + roomGap, baseRoomWidth / 2, baseRoomHeight, false, "Peminjaman"));
        ruanganList.add(new RoomData("R.B4.12", currentX - (baseRoomWidth / 2), currentY + baseRoomHeight + roomGap, baseRoomWidth / 2, baseRoomHeight, true, "Peminjaman"));
        
        // R.B4.05 (sambungan vertikal)
        ruanganList.add(new RoomData("R.B4.05", currentX - baseRoomWidth - roomGap, currentY, baseRoomWidth, baseRoomHeight, false, "Peminjaman"));
        
        currentX += (baseRoomWidth / 2) + roomGap;

        // --- Area Lab TIK (Pojok Kanan) ---
        int labTikX = currentX + 50; // Jarak dari ruangan utama
        int labTikY = margin;
        
        ruanganList.add(new RoomData("Lab TIK 1", labTikX, labTikY, baseRoomWidth, baseRoomHeight / 2, true, "Peminjaman"));
        ruanganList.add(new RoomData("Lab TIK 2", labTikX + baseRoomWidth + roomGap, labTikY, baseRoomWidth, baseRoomHeight / 2, false, "Peminjaman"));
        
        labTikY += (baseRoomHeight / 2) + roomGap;
        
        ruanganList.add(new RoomData("Lab TIK 3", labTikX, labTikY, baseRoomWidth, baseRoomHeight / 2, true, "Peminjaman"));
        ruanganList.add(new RoomData("Lab TIK 4", labTikX + baseRoomWidth + roomGap, labTikY, baseRoomWidth, baseRoomHeight / 2, false, "Peminjaman"));
        
        // Menyesuaikan ukuran panel agar semua ruangan terlihat
        setPreferredSize(new Dimension(labTikX + (baseRoomWidth * 2) + (roomGap * 2) + margin, (baseRoomHeight * 2) + (roomGap * 2) + margin)); 

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
        g2d.setStroke(new BasicStroke(2)); // Ketebalan garis border default

        // Latar Belakang Putih
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Gambar Setiap Ruangan
        for (RoomData room : ruanganList) {
            
            Color roomColor;
            Color textColor = Color.WHITE; // Default teks putih

            switch (room.type) {
                case "Lobby":
                    roomColor = Color.BLACK;
                    break;
                case "Non-Peminjaman":
                    roomColor = Color.GRAY;
                    textColor = Color.BLACK; // Teks hitam untuk ruangan abu-abu
                    break;
                case "Peminjaman":
                    if (room.isAvailable) {
                        roomColor = new Color(85, 172, 79); // Hijau cerah
                    } else {
                        roomColor = new Color(220, 50, 50); // Merah cerah
                    }
                    break;
                default:
                    roomColor = Color.LIGHT_GRAY; // Fallback
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
    @Override
    private void handleRoomClick(int x, int y) {
        
        for (RoomData room : ruanganList) {
            if (room.getBounds().contains(x, y)) {
                // Hanya ruangan "Peminjaman" yang bisa berinteraksi
                if (room.type.equals("Peminjaman")) {
                    String roomName = room.nama;
                    
                    if (!room.isAvailable) { 
                        // Ruangan Terisi (Merah)
                        String detail = "Dipakai oleh: Kelas I005 (Algoritma) \nJam: 10:00 - 12:30 \nKeperluan: Perkuliahan Reguler";
                        
                        if (userRole.equals("Admin")) {
                             String[] options = {"Ubah Jadwal", "Lihat Detail", "Tutup"};
                             int choice = JOptionPane.showOptionDialog(this, detail, "Kelola " + roomName, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);
                             if (choice == 0) {
                                 JOptionPane.showMessageDialog(this, "Admin membuka Form Ubah Jadwal untuk " + roomName);
                             }
                        } else {
                             JOptionPane.showMessageDialog(this, detail, "Info Ruangan Terisi", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        // Ruangan Kosong (Hijau)
                        if (userRole.equals("Mahasiswa")) {
                            int response = JOptionPane.showConfirmDialog(this, "Ruangan " + roomName + " tersedia. \nAjukan peminjaman sekarang?", "Pinjam Ruangan", JOptionPane.YES_NO_OPTION);
                            if (response == JOptionPane.YES_OPTION) {
                                JOptionPane.showMessageDialog(this, "Buka Form Input Peminjaman untuk " + roomName + "...");
                            }
                        } else if (userRole.equals("Admin")) {
                            int response = JOptionPane.showConfirmDialog(this, "Ruangan " + roomName + " kosong. \nBuat jadwal baru (manual)?", "Buat Jadwal Admin", JOptionPane.YES_NO_OPTION);
                            if (response == JOptionPane.YES_OPTION) {
                                JOptionPane.showMessageDialog(this, "Admin membuka Form Input Jadwal Baru untuk " + roomName + "...");
                            }
                        }
                    }
                } else if (room.type.equals("Lobby")) {
                    JOptionPane.showMessageDialog(this, "Ini adalah area Lobby.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                } else if (room.type.equals("Non-Peminjaman")) {
                    JOptionPane.showMessageDialog(this, "Ini adalah area non-peminjaman.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
                return; 
            }
        }
    }
}