// view/AdminFrame.java

package view;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import controller.DatabaseManager;
import model.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminFrame extends JFrame {
    private User currentUser;
    private DenahPanel denahPanel;
    private DatePicker datePicker;
    private JComboBox<String> sessionCombo;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTable bookingTable;
    private DefaultTableModel tableModel;

    private static final Object[][] SESSIONS = {
        {"Sesi 1", "07:30", "08:20"},
        {"Sesi 2", "08:25", "09:15"},
        {"Sesi 3", "09:20", "10:00"},
        {"Sesi 4", "10:15", "11:05"},
        {"Sesi 5", "11:10", "12:00"},
        {"Sesi 6", "13:00", "13:50"},
        {"Sesi 7", "13:55", "14:45"},
        {"Sesi 8", "15:30", "16:20"},
        {"Sesi 9", "16:25", "17:15"},
        {"Sesi 10", "18:10", "19:00"}
    };

    public AdminFrame(User user) {
        this.currentUser = user;
        
        setTitle("Panggon FATISDA - Admin");
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(UIConstants.MIN_WIDTH, UIConstants.MIN_HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.LABEL_FONT);

        // Tab 1: Denah
        JPanel denahTab = createDenahTab();
        tabbedPane.addTab("Denah / Ubah Jadwal", denahTab);

        // Tab 2: Ajuan Peminjaman
        JPanel ajuanTab = createAjuanTab();
        tabbedPane.addTab("Ajuan Peminjaman", ajuanTab);

        // Tab 3: Manajemen Akun
        AccountManagementPanel accountPanel = new AccountManagementPanel();
        tabbedPane.addTab("Manajemen Akun", accountPanel);

        // Tab 4: Jadwal Kelas
        ClassSchedulePanel schedulePanel = new ClassSchedulePanel(true);
        tabbedPane.addTab("Jadwal Kelas", schedulePanel);

        add(tabbedPane, BorderLayout.CENTER);

        refreshBookingTable();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Admin");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Admin: " + currentUser.getFullname());
        userLabel.setFont(UIConstants.LABEL_FONT);
        userLabel.setForeground(Color.WHITE);
        rightPanel.add(userLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(UIConstants.BUTTON_FONT);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> handleLogout());
        rightPanel.add(logoutButton);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createDenahTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_LIGHT);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(900);
        splitPane.setResizeWeight(0.7);

        // Kiri: Denah
        JPanel denahContainer = new JPanel(new BorderLayout());
        denahContainer.setBackground(UIConstants.BG_LIGHT);
        denahContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        denahPanel = new DenahPanel();
        denahPanel.setRoomClickListener(this::handleAdminRoomClick);
        
        JScrollPane denahScroll = new JScrollPane(denahPanel);
        denahScroll.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1));
        denahContainer.add(denahScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(denahContainer);

        // Kanan: Control Panel
        JPanel controlPanel = createDenahControlPanel();
        splitPane.setRightComponent(controlPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDenahControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Judul
        JLabel titleLabel = new JLabel("Pilih Tanggal & Sesi");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Date Picker
        JLabel dateLabel = new JLabel("Tanggal:");
        dateLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(dateLabel, gbc);

        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd-MM-yyyy");
        dateSettings.setAllowEmptyDates(false);
        datePicker = new DatePicker(dateSettings);
        datePicker.setDate(LocalDate.now());
        datePicker.getComponentDateTextField().setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(datePicker, gbc);

        // Dropdown sesi
        JLabel sessionLabel = new JLabel("Pilih Sesi:");
        sessionLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(sessionLabel, gbc);

        String[] sessionNames = new String[SESSIONS.length];
        for (int i = 0; i < SESSIONS.length; i++) {
            sessionNames[i] = (String) SESSIONS[i][0];
        }
        sessionCombo = new JComboBox<>(sessionNames);
        sessionCombo.setFont(UIConstants.LABEL_FONT);
        sessionCombo.addActionListener(e -> updateTimeFields());
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(sessionCombo, gbc);

        // Start Time (read only)
        JLabel startLabel = new JLabel("Jam Mulai:");
        startLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(startLabel, gbc);

        startTimeField = new JTextField();
        startTimeField.setFont(UIConstants.LABEL_FONT);
        startTimeField.setEditable(false);
        startTimeField.setBackground(new Color(241, 245, 249));
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(startTimeField, gbc);

        // End Time (read only)
        JLabel endLabel = new JLabel("Jam Selesai:");
        endLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(endLabel, gbc);

        endTimeField = new JTextField();
        endTimeField.setFont(UIConstants.LABEL_FONT);
        endTimeField.setEditable(false);
        endTimeField.setBackground(new Color(241, 245, 249));
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(endTimeField, gbc);

        updateTimeFields();

        // Update button
        JButton updateButton = new JButton("Perbarui Denah");
        updateButton.setFont(UIConstants.BUTTON_FONT);
        updateButton.setBackground(new Color(59, 130, 246));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.addActionListener(e -> updateDenah());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(updateButton, gbc);

        // Instruksi
        JPanel instructionPanel = new JPanel(new BorderLayout());
        instructionPanel.setBackground(new Color(219, 234, 254));
        instructionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(147, 197, 253), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel instructionLabel = new JLabel("<html><b>Petunjuk:</b><br/>" +
            "1. Pilih tanggal dan sesi<br/>" +
            "2. Klik 'Perbarui Denah'<br/>" +
            "3. Klik ruangan untuk melihat jadwal<br/>" +
            "4. Pilih sesi yang kosong untuk input jadwal</html>");
        instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        instructionPanel.add(instructionLabel, BorderLayout.CENTER);

        gbc.gridy = 6;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(instructionPanel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        return panel;
    }

    private void updateTimeFields() {
        int selectedIndex = sessionCombo.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < SESSIONS.length) {
            startTimeField.setText((String) SESSIONS[selectedIndex][1]);
            endTimeField.setText((String) SESSIONS[selectedIndex][2]);
        }
    }

    private JPanel createAjuanTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Judul
        JLabel titleLabel = new JLabel("Daftar Ajuan Peminjaman");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Tabel
        String[] columns = {"ID", "Ruangan", "Tanggal", "Jam", "Keperluan", "Peminjam", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingTable = new JTable(tableModel);
        bookingTable.setFont(UIConstants.LABEL_FONT);
        bookingTable.setRowHeight(30);
        bookingTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(UIConstants.BUTTON_FONT);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshBookingTable());
        buttonPanel.add(refreshButton);

        JButton approveButton = new JButton("Setujui");
        approveButton.setFont(UIConstants.BUTTON_FONT);
        approveButton.setBackground(new Color(34, 197, 94));
        approveButton.setForeground(Color.WHITE);
        approveButton.setFocusPainted(false);
        approveButton.addActionListener(e -> handleApprove());
        buttonPanel.add(approveButton);

        JButton rejectButton = new JButton("Tolak");
        rejectButton.setFont(UIConstants.BUTTON_FONT);
        rejectButton.setBackground(new Color(239, 68, 68));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setFocusPainted(false);
        rejectButton.addActionListener(e -> handleReject());
        buttonPanel.add(rejectButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateDenah() {
        LocalDate date = datePicker.getDate();

        if (date == null) {
            JOptionPane.showMessageDialog(this, 
                "Tanggal harus diisi!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Time range dipilih
        String startTimeStr = startTimeField.getText();
        String endTimeStr = endTimeField.getText();
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);

        // Update denah pake tanggal sama time range
        denahPanel.setDateAndTime(date, startTime, endTime);
    }

    private void handleAdminRoomClick(Room room, RoomStatus status) {
        LocalDate date = datePicker.getDate();

        if (date == null) {
            JOptionPane.showMessageDialog(this, 
                "Silakan pilih tanggal terlebih dahulu!", 
                "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Sesi dipilih
        String startTimeStr = startTimeField.getText();
        String endTimeStr = endTimeField.getText();
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);

        RoomScheduleDialog dialog = new RoomScheduleDialog(this, room, date, startTime, endTime, true);
        dialog.setVisible(true);
        
        if (dialog.isScheduleSelected()) {
            BookingFormDialog bookingDialog = new BookingFormDialog(this, room, date, 
                dialog.getSelectedStartTime(), dialog.getSelectedEndTime(), true);
            bookingDialog.setVisible(true);
            
            if (bookingDialog.isSubmitted()) {
                updateDenah();
                refreshBookingTable();
            }
        }
    }

    private void refreshBookingTable() {
        tableModel.setRowCount(0);
        List<Booking> bookings = DatabaseManager.getPendingBookings();
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Booking booking : bookings) {
            String roomName = DatabaseManager.getRoomName(booking.getRoomId());
            String dateStr = booking.getDate().format(dateFormatter);
            String timeStr = booking.getStartTime().format(timeFormatter) + " - " + 
                           booking.getEndTime().format(timeFormatter);

            tableModel.addRow(new Object[]{
                booking.getId(),
                roomName,
                dateStr,
                timeStr,
                booking.getPurpose(),
                booking.getBorrower(),
                booking.getStatus().toString()
            });
        }
    }

    private void handleApprove() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Silakan pilih ajuan yang akan disetujui!",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        String roomName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Setujui peminjaman untuk " + roomName + "?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseManager.updateBookingStatus(bookingId, BookingStatus.APPROVED);
            JOptionPane.showMessageDialog(this,
                "Peminjaman berhasil disetujui!",
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
            refreshBookingTable();
            updateDenah();
        }
    }

    private void handleReject() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Silakan pilih ajuan yang akan ditolak!",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        String roomName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Tolak peminjaman untuk " + roomName + "?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseManager.updateBookingStatus(bookingId, BookingStatus.REJECTED);
            JOptionPane.showMessageDialog(this,
                "Peminjaman berhasil ditolak!",
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
            refreshBookingTable();
            updateDenah();
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah Anda yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
