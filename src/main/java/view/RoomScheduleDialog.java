// view/RoomScheduleDialog.java

package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.DatabaseManager;
import model.Booking;
import model.ClassSchedule;
import model.Room;

public class RoomScheduleDialog extends JDialog {
    private Room room;
    private LocalDate currentDate;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private boolean isAdminMode;
    private boolean scheduleSelected = false;
    private LocalTime selectedStartTime;
    private LocalTime selectedEndTime;

    public RoomScheduleDialog(Frame parent, Room room, LocalDate date, LocalTime startTime, LocalTime endTime, boolean isAdminMode) {
        super(parent, "Jadwal - " + room.getName(), true);
        
        this.room = room;
        this.currentDate = date;
        this.isAdminMode = isAdminMode;
        
        setSize(1000, 550);
        setLocationRelativeTo(parent);
        
        initComponents();
        loadSchedule();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Jadwal Ruangan: " + room.getName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 41, 59));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String dayName = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBackground(new Color(219, 234, 254));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(147, 197, 253), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel dateLabel = new JLabel("Tanggal: " + currentDate.format(dateFormatter) + " (" + dayName + ")");
        JLabel instructionLabel = new JLabel("Klik pada baris hijau (TERSEDIA) untuk memilih sesi tersebut");
        
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        instructionLabel.setForeground(new Color(59, 130, 246));
        
        infoPanel.add(dateLabel);
        infoPanel.add(instructionLabel);
        
        headerPanel.add(infoPanel, BorderLayout.SOUTH);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Sesi", "Jam Mulai", "Jam Selesai", "Keperluan", "Peminjam/Dosen", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setFont(UIConstants.LABEL_FONT);
        scheduleTable.setRowHeight(35);
        scheduleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Custom renderer for status column
        scheduleTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                          boolean isSelected, boolean hasFocus, 
                                                          int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = value.toString();
                if (!isSelected) {
                    switch (status) {
                        case "TERSEDIA":
                        case "AVAILABLE":
                            c.setBackground(UIConstants.COLOR_AVAILABLE);
                            c.setForeground(new Color(22, 101, 52));
                            break;
                        case "PENDING":
                            c.setBackground(UIConstants.COLOR_PENDING);
                            c.setForeground(new Color(113, 63, 18));
                            break;
                        case "JADWAL KELAS":
                        case "APPROVED":
                        case "DIJADWALKAN":
                            c.setBackground(UIConstants.COLOR_APPROVED);
                            c.setForeground(new Color(127, 29, 29));
                            break;
                        case "LOBBY":
                            c.setBackground(UIConstants.COLOR_UNAVAILABLE);
                            c.setForeground(UIConstants.COLOR_TEXT);
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                    }
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                          boolean isSelected, boolean hasFocus, 
                                                          int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) table.getValueAt(row, 5);
                if (!isSelected) {
                    switch (status) {
                        case "TERSEDIA":
                        case "AVAILABLE":
                            c.setBackground(UIConstants.COLOR_AVAILABLE);
                            c.setForeground(UIConstants.COLOR_TEXT);
                            break;
                        case "PENDING":
                            c.setBackground(UIConstants.COLOR_PENDING);
                            c.setForeground(UIConstants.COLOR_TEXT);
                            break;
                        case "JADWAL KELAS":
                        case "APPROVED":
                        case "DIJADWALKAN":
                            c.setBackground(UIConstants.COLOR_APPROVED);
                            c.setForeground(UIConstants.COLOR_TEXT);
                            break;
                        case "LOBBY":
                            c.setBackground(UIConstants.COLOR_UNAVAILABLE);
                            c.setForeground(UIConstants.COLOR_TEXT);
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(UIConstants.COLOR_TEXT);
                    }
                }
                return c;
            }
        };
        
        for (int i = 0; i < 5; i++) {
            scheduleTable.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);
        }
        
        // MouseListener buat yang tersedia dan diklik
        scheduleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = scheduleTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String status = (String) tableModel.getValueAt(row, 5);
                    if ("TERSEDIA".equals(status)) {
                        handleAvailableSlotClick(row);
                    } else {
                        JOptionPane.showMessageDialog(RoomScheduleDialog.this,
                            "Sesi ini tidak tersedia untuk dipinjam.",
                            "Tidak Tersedia",
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        
        // Hover
        scheduleTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = scheduleTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String status = (String) tableModel.getValueAt(row, 5);
                    if ("TERSEDIA".equals(status)) {
                        scheduleTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else {
                        scheduleTable.setCursor(Cursor.getDefaultCursor());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        legendPanel.setBackground(Color.WHITE);
        
        addLegendItem(legendPanel, "Tersedia (Klik untuk pilih)", new Color(134, 239, 172));
        addLegendItem(legendPanel, "Pending", new Color(253, 224, 71));
        addLegendItem(legendPanel, "Dijadwalkan/Kelas", new Color(252, 165, 165));
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton closeButton = new JButton("Tutup");
        closeButton.setFont(UIConstants.BUTTON_FONT);
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.setBackground(new Color(148, 163, 184));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(closeButton);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(legendPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    
    private void addLegendItem(JPanel panel, String label, Color color) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        itemPanel.setBackground(Color.WHITE);
        
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER));
        
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        itemPanel.add(colorBox);
        itemPanel.add(textLabel);
        panel.add(itemPanel);
    }

    private void loadSchedule() {
        tableModel.setRowCount(0);

        Object[][] sessions = {
            {"Sesi 1", LocalTime.of(7, 30), LocalTime.of(8, 20)},
            {"Sesi 2", LocalTime.of(8, 25), LocalTime.of(9, 15)},
            {"Sesi 3", LocalTime.of(9, 20), LocalTime.of(10, 0)},
            {"Sesi 4", LocalTime.of(10, 15), LocalTime.of(11, 5)},
            {"Sesi 5", LocalTime.of(11, 10), LocalTime.of(12, 0)},
            {"Sesi 6", LocalTime.of(13, 0), LocalTime.of(13, 50)},
            {"Sesi 7", LocalTime.of(13, 55), LocalTime.of(14, 45)},
            {"Sesi 8", LocalTime.of(15, 30), LocalTime.of(16, 20)},
            {"Sesi 9", LocalTime.of(16, 25), LocalTime.of(17, 15)},
            {"Sesi 10", LocalTime.of(18, 10), LocalTime.of(19, 0)}
        };

        
        // Jadwal kelas untuk ruangan dan tanggal tersebut
        String dayName = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
        List<ClassSchedule> classSchedules = DatabaseManager.getSchedulesByRoomAndDay(room.getId(), dayName);
        
        // List booking di tanggal tersebut
        List<Booking> bookings = DatabaseManager.getBookingsByRoomAndDate(room.getId(), currentDate);
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Object[] session : sessions) {
            String sessionName = (String) session[0];
            LocalTime slotStart = (LocalTime) session[1];
            LocalTime slotEnd = (LocalTime) session[2];
            
            String startStr = slotStart.format(timeFormatter);
            String endStr = slotEnd.format(timeFormatter);
            
            String purpose = "-";
            String borrower = "-";
            String status = "TERSEDIA";

            ClassSchedule classSchedule = findClassScheduleForSlot(classSchedules, sessionName);
                if (classSchedule != null) {
                    purpose = classSchedule.getCourseName() + " (" + classSchedule.getCourseCode() + ")";
                    borrower = classSchedule.getLecturer() + " - Kelas " + classSchedule.getClassName();
                    status = "JADWAL KELAS";
                } else {
                    Booking booking = findBookingForSlot(bookings, slotStart, slotEnd);
                    if (booking != null) {
                        purpose = booking.getPurpose();
                        borrower = booking.getBorrower();
                        status = booking.getStatus().toString();
                    }
                }
                
            Object[] rowData = {sessionName, startStr, endStr, purpose, borrower, status};
            tableModel.addRow(rowData);
            }        
    }

    private ClassSchedule findClassScheduleForSlot(List<ClassSchedule> schedules, String sessionName) {
        for (ClassSchedule schedule : schedules) {
            if (schedule.getSession().equals(sessionName.replace("Sesi ", ""))) {
                return schedule;
            }
        }
        return null;
    }
    
    private Booking findBookingForSlot(List<Booking> bookings, LocalTime slotStart, LocalTime slotEnd) {
        for (Booking booking : bookings) {
            if (!(booking.getEndTime().isBefore(slotStart) || booking.getEndTime().equals(slotStart) ||
                  booking.getStartTime().isAfter(slotEnd) || booking.getStartTime().equals(slotEnd))) {
                return booking;
            }
        }
        return null;
    }
    
    private void handleAvailableSlotClick(int row) {
        String sessionName = (String) tableModel.getValueAt(row, 0);
        String startTimeStr = (String) tableModel.getValueAt(row, 1);
        String endTimeStr = (String) tableModel.getValueAt(row, 2);
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        selectedStartTime = LocalTime.parse(startTimeStr, timeFormatter);
        selectedEndTime = LocalTime.parse(endTimeStr, timeFormatter);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Anda memilih " + sessionName + " (" + startTimeStr + " - " + endTimeStr + ")\n" +
            "di ruangan " + room.getName() + "\n\n" +
            "Lanjutkan ke form peminjaman?",
            "Konfirmasi Pemilihan Sesi",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            scheduleSelected = true;
            dispose();
        }
    }
    
    public boolean isScheduleSelected() {
        return scheduleSelected;
    }
    
    public LocalTime getSelectedStartTime() {
        return selectedStartTime;
    }
    
    public LocalTime getSelectedEndTime() {
        return selectedEndTime;
    }
}