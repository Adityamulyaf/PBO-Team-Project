// view/BookingFromDialog.java

package view;

import controller.BookingController;
import controller.DatabaseManager;
import model.Room;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BookingFormDialog extends JDialog {
    private JTextField roomField;
    private JTextField dateField;
    private JTextField timeField;
    private JTextField purposeField;
    private JTextField borrowerField;
    
    private String roomId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean submitted = false;
    private boolean isAdminMode;

    public BookingFormDialog(Frame parent, Room room, LocalDate date, LocalTime startTime, LocalTime endTime, boolean isAdminMode) {
        super(parent, isAdminMode ? "Input Jadwal Kelas" : "Form Peminjaman Ruangan", true);
        
        this.roomId = room.getId();
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAdminMode = isAdminMode;
        
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        initComponents(room);
    }

    private void initComponents(Room room) {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(Color.WHITE);

        // Title
        String titleText = isAdminMode ? "Input Jadwal Kelas" : "Pinjam Ruangan";
        JLabel titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 41, 59));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Ruangan (read only)
        addFormField(formPanel, gbc, 0, "Ruang:", 
            roomField = createReadOnlyField(DatabaseManager.getRoomName(roomId)));

        // Tanggal (read only)
        addFormField(formPanel, gbc, 1, "Tanggal:", 
            dateField = createReadOnlyField(date.format(dateFormatter)));

        // Jam (read-only)
        addFormField(formPanel, gbc, 2, "Jam:", 
            timeField = createReadOnlyField(startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter)));

        // Keperluan
        String keperluanLabel = isAdminMode ? "Mata Kuliah:" : "Keperluan:";
        addFormField(formPanel, gbc, 3, keperluanLabel, 
            purposeField = new JTextField(20));

        // Peminjam
        String peminjamLabel = isAdminMode ? "Dosen/Kelas:" : "Peminjam:";
        addFormField(formPanel, gbc, 4, peminjamLabel, 
            borrowerField = new JTextField(20));

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        String submitText = isAdminMode ? "Simpan" : "Submit";
        JButton submitButton = new JButton(submitText);
        submitButton.setFont(UIConstants.BUTTON_FONT);
        submitButton.setPreferredSize(new Dimension(120, 40));
        submitButton.setBackground(new Color(34, 197, 94));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> handleSubmit());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(UIConstants.BUTTON_FONT);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        field.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private JTextField createReadOnlyField(String text) {
        JTextField field = new JTextField(text);
        field.setEditable(false);
        field.setBackground(new Color(241, 245, 249));
        return field;
    }

    private void handleSubmit() {
        String purpose = purposeField.getText().trim();
        String borrower = borrowerField.getText().trim();

        // Validasi
        if (purpose.isEmpty()) {
            String fieldName = isAdminMode ? "Mata kuliah" : "Keperluan";
            JOptionPane.showMessageDialog(this, 
                fieldName + " tidak boleh kosong!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            purposeField.requestFocus();
            return;
        }

        if (borrower.isEmpty()) {
            String fieldName = isAdminMode ? "Dosen/Kelas" : "Peminjam";
            JOptionPane.showMessageDialog(this, 
                fieldName + " tidak boleh kosong!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            borrowerField.requestFocus();
            return;
        }

        // Submit booking
        int bookingId;
        if (isAdminMode) {
            // Admin langsung APPROVED
            bookingId = BookingController.submitBookingAsAdmin(roomId, date, startTime, endTime, purpose, borrower);
        } else {
            // Mahasiswa PENDING dulu
            bookingId = BookingController.submitBooking(roomId, date, startTime, endTime, purpose, borrower);
        }

        if (bookingId > 0) {
            submitted = true;
            String message = isAdminMode 
                ? "Jadwal kelas berhasil disimpan!"
                : "Peminjaman berhasil diajukan!\nMenunggu persetujuan admin.";

            JOptionPane.showMessageDialog(this, 
                message, 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } else if (bookingId == -2) {
            JOptionPane.showMessageDialog(this, 
                "Gagal mengajukan peminjaman. Ruangan sudah tidak tersedia untuk waktu tersebut.", 
                "Ruangan Tidak Tersedia", 
                JOptionPane.ERROR_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(this, 
                "Gagal menyimpan data. Pastikan semua input sudah benar.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }
}