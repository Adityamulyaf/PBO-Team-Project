// view/DenahPanel.java

package view;

import controller.BookingController;
import controller.DatabaseManager;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DenahPanel extends JPanel {
    private List<Room> rooms;
    private Map<String, RoomStatus> roomStatuses;
    private LocalDate currentDate;
    private LocalTime currentStartTime;
    private LocalTime currentEndTime;
    private Room hoveredRoom;
    private RoomClickListener clickListener;

    public interface RoomClickListener {
        void onRoomClicked(Room room, RoomStatus status);
    }

    public DenahPanel() {
        setPreferredSize(new Dimension(1100, 300));
        setBackground(new Color(241, 245, 249));
        initializeRooms();
        
        currentDate = LocalDate.now();
        currentStartTime = LocalTime.of(7, 0); // Default: sesi 1 start
        currentEndTime = LocalTime.of(8, 0); // Default: sesi 1 end
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e.getX(), e.getY());
            }
        });
        
        updateStatuses();
    }

    private void initializeRooms() {
        rooms = new ArrayList<>();
        
        // BARIS 1 - dari kiri ke kanan
        rooms.add(new Room("R_B4_11", "R. B4. 11", 50, 30, 120, 100));
        rooms.add(new Room("R_B4_10", "R. B4. 10", 50, 140, 120, 100));
        rooms.add(new Room("R_B4_08", "R. B4. 08", 180, 30, 60, 210));
        
        // Area yang abu abu
        rooms.add(new Room("GRAY_1", "", 250, 30, 90, 100));
        rooms.add(new Room("GRAY_2", "", 250, 140, 90, 100));
        rooms.add(new Room("GRAY_3", "", 350, 30, 90, 100));
        
        // Lobby
        rooms.add(new Room("LOBBY", "Lobby", 350, 140, 90, 100));
        
        // Area abu abu
        rooms.add(new Room("GRAY_4", "", 450, 30, 90, 100));
        rooms.add(new Room("GRAY_5", "", 450, 140, 90, 100));
        rooms.add(new Room("GRAY_6", "", 550, 30, 60, 210));
        rooms.add(new Room("GRAY_7", "", 620, 30, 90, 210));
        
        // Ruangan tengah
        rooms.add(new Room("R_B4_04", "R. B4. 04", 720, 30, 160, 100));
        rooms.add(new Room("R_B4_06", "R. B4. 06", 720, 140, 75, 100));
        rooms.add(new Room("R_B4_12", "R. B4. 12", 805, 140, 75, 100));
        rooms.add(new Room("R_B4_05", "R. B4. 05", 890, 30, 90, 210));
        
        // Lab TIK
        rooms.add(new Room("LAB_TIK_1", "Lab. TIK 1", 990, 30, 100, 100));
        rooms.add(new Room("LAB_TIK_2", "Lab. TIK 2", 1100, 30, 100, 100));
        rooms.add(new Room("LAB_TIK_3", "Lab. TIK 3", 990, 140, 100, 100));
        rooms.add(new Room("LAB_TIK_4", "Lab. TIK 4", 1100, 140, 100, 100));
    }

    public void updateStatuses() {
        roomStatuses = calculateRoomStatuses(currentDate, currentStartTime, currentEndTime);
        repaint();
    }

    private Map<String, RoomStatus> calculateRoomStatuses(LocalDate date, LocalTime startTime, LocalTime endTime) {
        Map<String, RoomStatus> statuses = new HashMap<>();
        String[] roomIds = {"R_B4_11", "R_B4_10", "R_B4_08", "R_B4_04", "R_B4_06", 
                           "R_B4_12", "R_B4_05", "LAB_TIK_1", "LAB_TIK_2", "LAB_TIK_3", "LAB_TIK_4"};
        
        for (String roomId : roomIds) {
            statuses.put(roomId, BookingController.getRoomStatus(roomId, date, startTime, endTime));
        }
        
        return statuses;
    }

    public void setDate(LocalDate date) {
        this.currentDate = date;
        updateStatuses();
    }

    public void setDateAndTime(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.currentDate = date;
        this.currentStartTime = startTime;
        this.currentEndTime = endTime;
        updateStatuses();
    }

    public void setRoomClickListener(RoomClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rooms
        for (Room room : rooms) {
            Color fillColor = getRoomColor(room);
            Color borderColor = UIConstants.COLOR_BORDER;
            
            if (room == hoveredRoom && !room.getId().startsWith("GRAY") && !room.getId().equals("LOBBY")) {
                borderColor = new Color(59, 130, 246);
                g2d.setStroke(new BasicStroke(3));
            } else {
                g2d.setStroke(new BasicStroke(2));
            }

            // Fill
            g2d.setColor(fillColor);
            g2d.fillRoundRect(room.getX(), room.getY(), room.getWidth(), room.getHeight(), 10, 10);

            // Border
            g2d.setColor(borderColor);
            g2d.drawRoundRect(room.getX(), room.getY(), room.getWidth(), room.getHeight(), 10, 10);

            // Text
            if (!room.getName().isEmpty()) {
                g2d.setColor(room.getId().equals("LOBBY") ? Color.WHITE : UIConstants.COLOR_TEXT);
                g2d.setFont(UIConstants.ROOM_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                
                String[] lines = room.getName().split(" ");
                int totalHeight = lines.length * fm.getHeight();
                int startY = room.getY() + (room.getHeight() - totalHeight) / 2 + fm.getAscent();
                
                for (String line : lines) {
                    int textWidth = fm.stringWidth(line);
                    int textX = room.getX() + (room.getWidth() - textWidth) / 2;
                    g2d.drawString(line, textX, startY);
                    startY += fm.getHeight();
                }
            }
        }
        
        // Draw legend
        drawLegend(g2d);
    }
    
    private void drawLegend(Graphics2D g2d) {
        int legendX = 50;
        int legendY = 260;
        int boxSize = 20;
        
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        // Legend items
        String[] labels = {"Tersedia", "Pending", "Dijadwalkan", "Lobby"};
        Color[] colors = {UIConstants.COLOR_AVAILABLE, UIConstants.COLOR_PENDING, 
                         UIConstants.COLOR_APPROVED, UIConstants.COLOR_LOBBY};
        
        for (int i = 0; i < labels.length; i++) {
            int x = legendX + i * 120;
            
            // Color box
            g2d.setColor(colors[i]);
            g2d.fillRoundRect(x, legendY, boxSize, boxSize, 5, 5);
            g2d.setColor(UIConstants.COLOR_BORDER);
            g2d.drawRoundRect(x, legendY, boxSize, boxSize, 5, 5);
            
            // Label
            g2d.setColor(UIConstants.COLOR_TEXT);
            g2d.drawString(labels[i], x + boxSize + 5, legendY + boxSize - 5);
        }
    }

    private Color getRoomColor(Room room) {
        if (room.getId().equals("LOBBY")) {
            return UIConstants.COLOR_LOBBY;
        }
        
        if (room.getId().startsWith("GRAY")) {
            return UIConstants.COLOR_UNAVAILABLE;
        }

        RoomStatus status = roomStatuses.get(room.getId());
        if (status == null) status = RoomStatus.AVAILABLE;

        return switch (status) {
            case AVAILABLE -> UIConstants.COLOR_AVAILABLE;
            case PENDING -> UIConstants.COLOR_PENDING;
            case APPROVED -> UIConstants.COLOR_APPROVED;
            case REJECTED -> UIConstants.COLOR_REJECTED;
        };
    }

    private void handleMouseClick(int x, int y) {
        for (Room room : rooms) {
            if (room.contains(x, y)) {
                if (!room.getId().startsWith("GRAY") && !room.getId().equals("LOBBY")) {
                    RoomStatus status = roomStatuses.get(room.getId());
                    if (clickListener != null) {
                        clickListener.onRoomClicked(room, status);
                    }
                }
                return;
            }
        }
    }

    private void handleMouseMove(int x, int y) {
        Room newHovered = null;
        for (Room room : rooms) {
            if (room.contains(x, y)) {
                if (!room.getId().startsWith("GRAY") && !room.getId().equals("LOBBY")) {
                    newHovered = room;
                }
                break;
            }
        }

        if (newHovered != hoveredRoom) {
            hoveredRoom = newHovered;
            setCursor(hoveredRoom != null ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) 
                                          : Cursor.getDefaultCursor());
            repaint();
        }
    }
}