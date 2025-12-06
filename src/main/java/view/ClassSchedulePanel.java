// view/ClassSchedulePanel.java

package view;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.DatabaseManager;
import model.ClassSchedule;

public class ClassSchedulePanel extends JPanel {
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private boolean isAdminMode;

    public ClassSchedulePanel(boolean isAdminMode) {
        this.isAdminMode = isAdminMode;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadScheduleData();
    }

    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("Jadwal Kelas Mingguan");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Hari", "Sesi", "Jam", "Mata Kuliah", "Kode MK", "Semester", "SKS", "Dosen", "Ruangan", "Kelas"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel);
        scheduleTable.setFont(UIConstants.LABEL_FONT);
        scheduleTable.setRowHeight(30);
        scheduleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set column widths
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(70);  // Hari
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(50);  // Sesi
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Jam
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Mata Kuliah
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Kode MK
        scheduleTable.getColumnModel().getColumn(6).setPreferredWidth(70);  // Semester
        scheduleTable.getColumnModel().getColumn(7).setPreferredWidth(50);  // SKS
        scheduleTable.getColumnModel().getColumn(8).setPreferredWidth(150); // Dosen
        scheduleTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Ruangan
        scheduleTable.getColumnModel().getColumn(10).setPreferredWidth(60); // Kelas

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons (buat admin)
        if (isAdminMode) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
            buttonPanel.setBackground(Color.WHITE);

            JButton refreshButton = new JButton("Refresh");
            refreshButton.setFont(UIConstants.BUTTON_FONT);
            refreshButton.setFocusPainted(false);
            refreshButton.addActionListener(e -> loadScheduleData());
            buttonPanel.add(refreshButton);

            JButton addButton = new JButton("Tambah Jadwal");
            addButton.setFont(UIConstants.BUTTON_FONT);
            addButton.setBackground(new Color(34, 197, 94));
            addButton.setForeground(Color.WHITE);
            addButton.setFocusPainted(false);
            addButton.addActionListener(e -> handleAddSchedule());
            buttonPanel.add(addButton);

            JButton deleteButton = new JButton("Hapus");
            deleteButton.setFont(UIConstants.BUTTON_FONT);
            deleteButton.setBackground(new Color(239, 68, 68));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.addActionListener(e -> handleDeleteSchedule());
            buttonPanel.add(deleteButton);

            add(buttonPanel, BorderLayout.SOUTH);
        } else {
            // Mahasiswa (refresh button doang)
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
            buttonPanel.setBackground(Color.WHITE);

            JButton refreshButton = new JButton("Refresh");
            refreshButton.setFont(UIConstants.BUTTON_FONT);
            refreshButton.setFocusPainted(false);
            refreshButton.addActionListener(e -> loadScheduleData());
            buttonPanel.add(refreshButton);

            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private void loadScheduleData() {
        tableModel.setRowCount(0);
        List<ClassSchedule> schedules = DatabaseManager.getAllClassSchedules();

        for (ClassSchedule schedule : schedules) {
            String roomName = DatabaseManager.getRoomName(schedule.getRoomId());
            tableModel.addRow(new Object[]{
                schedule.getId(),
                schedule.getDay(),
                schedule.getSession(),
                schedule.getTime(),
                schedule.getCourseName(),
                schedule.getCourseCode(),
                schedule.getSemester(),
                schedule.getSks(),
                schedule.getLecturer(),
                roomName,
                schedule.getClassName()
            });
        }
    }

    private void handleAddSchedule() {
        AddScheduleDialog dialog = new AddScheduleDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isScheduleAdded()) {
            loadScheduleData();
        }
    }

    private void handleDeleteSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Silakan pilih jadwal yang akan dihapus!",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int scheduleId = (int) tableModel.getValueAt(selectedRow, 0);
        String courseName = (String) tableModel.getValueAt(selectedRow, 4);
        String day = (String) tableModel.getValueAt(selectedRow, 1);
        String time = (String) tableModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus jadwal?\n\n" +
            "Mata Kuliah: " + courseName + "\n" +
            "Hari: " + day + "\n" +
            "Jam: " + time,
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseManager.deleteClassSchedule(scheduleId)) {
                JOptionPane.showMessageDialog(this,
                    "Jadwal berhasil dihapus!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                loadScheduleData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Gagal menghapus jadwal!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Add Schedule Dialog
    private class AddScheduleDialog extends JDialog {
        private JComboBox<String> dayCombo;
        private JTextField sessionField;
        private JTextField timeField;
        private JTextField courseNameField;
        private JTextField courseCodeField;
        private JTextField semesterField;
        private JTextField sksField;
        private JTextField lecturerField;
        private JComboBox<String> roomCombo;
        private JTextField classNameField;
        private boolean scheduleAdded = false;

        public AddScheduleDialog(Frame parent) {
            super(parent, "Tambah Jadwal Kelas", true);
            setSize(500, 600);
            setLocationRelativeTo(parent);
            setResizable(false);

            initComponents();
        }

        private void initComponents() {
            JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
            mainPanel.setBackground(Color.WHITE);

            // Title
            JLabel titleLabel = new JLabel("Tambah Jadwal Kelas Baru", SwingConstants.CENTER);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            titleLabel.setForeground(new Color(30, 41, 59));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            // Form
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Day
            String[] days = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat"};
            dayCombo = new JComboBox<>(days);
            dayCombo.setFont(UIConstants.LABEL_FONT);
            addFormField(formPanel, gbc, 0, "Hari:", dayCombo);

            // Session
            sessionField = new JTextField(20);
            sessionField.setFont(UIConstants.LABEL_FONT);
            addFormField(formPanel, gbc, 1, "Sesi:", sessionField);

            // Time
            timeField = new JTextField(20);
            timeField.setFont(UIConstants.LABEL_FONT);
            addFormField(formPanel, gbc, 2, "Jam (HH:mm - HH:mm):", timeField);

            // Course Name
            courseNameField = new JTextField(20);
            courseNameField.setFont(UIConstants.LABEL_FONT);
            addFormField(formPanel, gbc, 3, "Mata Kuliah:", courseNameField);

            // Course Code
            courseCodeField = new JTextField(20);
            courseCodeField.setFont(UIConstants.LABEL_FONT);
            addFormField(formPanel, gbc, 4, "Kode MK:", courseCodeField);

            // Semester
            semesterField = new JTextField(20);
            semesterField.setFont(UIConstants.LABEL_FONT);
            addFormField(formPanel, gbc, 5, "Semester:", semesterField);

            // SKS
            sksField = new JTextField(20);
            sksField.setFont(UIConstants.LABEL_FONT);
            addFormField(formPanel, gbc, 6, "SKS:", sksField);

            // Lecturer
            lecturerField = new JTextField(20);
            lecturerField.setFont(UIConstants.LABEL_FONT);
            addFormField(formPanel, gbc, 7, "Dosen:", lecturerField);

            // Room
            String[] rooms = {"R_B4_11", "R_B4_10", "R_B4_08", "R_B4_04", "R_B4_06", 
                            "R_B4_12", "R_B4_05", "LAB_TIK_1", "LAB_TIK_2", "LAB_TIK_3", "LAB_TIK_4"};
            roomCombo = new JComboBox<>(rooms);
            roomCombo.setFont(UIConstants.LABEL_FONT);
            addFormField(formPanel, gbc, 8, "Ruangan:", roomCombo);

            // Class Name
            classNameField = new JTextField(20);
            classNameField.setFont(UIConstants.LABEL_FONT);
            addFormField(formPanel, gbc, 9, "Kelas:", classNameField);

            mainPanel.add(formPanel, BorderLayout.CENTER);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            buttonPanel.setBackground(Color.WHITE);

            JButton saveButton = new JButton("Simpan");
            saveButton.setFont(UIConstants.BUTTON_FONT);
            saveButton.setPreferredSize(new Dimension(120, 40));
            saveButton.setBackground(new Color(34, 197, 94));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFocusPainted(false);
            saveButton.addActionListener(e -> handleSave());

            JButton cancelButton = new JButton("Batal");
            cancelButton.setFont(UIConstants.BUTTON_FONT);
            cancelButton.setPreferredSize(new Dimension(120, 40));
            cancelButton.addActionListener(e -> dispose());

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);

            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            add(mainPanel);
        }

        private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
            JLabel label = new JLabel(labelText);
            label.setFont(UIConstants.LABEL_FONT);
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            panel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            panel.add(field, gbc);
        }

        private void handleSave() {
            String day = (String) dayCombo.getSelectedItem();
            String session = sessionField.getText().trim();
            String time = timeField.getText().trim();
            String courseName = courseNameField.getText().trim();
            String courseCode = courseCodeField.getText().trim();
            String semester = semesterField.getText().trim();
            String sks = sksField.getText().trim();
            String lecturer = lecturerField.getText().trim();
            String roomId = (String) roomCombo.getSelectedItem();
            String className = classNameField.getText().trim();

            // Validasi
            if (session.isEmpty() || time.isEmpty() || courseName.isEmpty() || 
                courseCode.isEmpty() || semester.isEmpty() || sks.isEmpty() || 
                lecturer.isEmpty() || className.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Semua field harus diisi!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create schedule
            int scheduleId = DatabaseManager.createClassSchedule(day, session, time, courseName,
                courseCode, semester, sks, lecturer, roomId, className);

            if (scheduleId > 0) {
                scheduleAdded = true;
                JOptionPane.showMessageDialog(this,
                    "Jadwal kelas berhasil ditambahkan!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Gagal menambahkan jadwal!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isScheduleAdded() {
            return scheduleAdded;
        }
    }
}