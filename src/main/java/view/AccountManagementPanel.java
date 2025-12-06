// view/AccountManagementPanel.java

package view;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

import controller.DatabaseManager;
import model.User;

public class AccountManagementPanel extends JPanel {
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullnameField;

    public AccountManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadAccountData();
    }

    private void initComponents() {
        // Judul
        JLabel titleLabel = new JLabel("Manajemen Akun Mahasiswa");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);
        splitPane.setResizeWeight(0.6);

        // Tabel kiri
        JPanel tablePanel = createTablePanel();
        splitPane.setLeftComponent(tablePanel);

        // Form kanan
        JPanel formPanel = createFormPanel();
        splitPane.setRightComponent(formPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel tableLabel = new JLabel("Daftar Akun Mahasiswa");
        tableLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        tableLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(tableLabel, BorderLayout.NORTH);

        // Tabel
        String[] columns = {"ID", "Username", "Nama Lengkap", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        accountTable = new JTable(tableModel);
        accountTable.setFont(UIConstants.LABEL_FONT);
        accountTable.setRowHeight(28);
        accountTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(accountTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(UIConstants.BUTTON_FONT);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadAccountData());
        buttonPanel.add(refreshButton);

        JButton deleteButton = new JButton("Hapus");
        deleteButton.setFont(UIConstants.BUTTON_FONT);
        deleteButton.setBackground(new Color(239, 68, 68));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> handleDelete());
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Judul
        JLabel titleLabel = new JLabel("Buat Akun Baru");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(passwordField, gbc);

        // Fullname
        JLabel fullnameLabel = new JLabel("Nama Lengkap:");
        fullnameLabel.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(fullnameLabel, gbc);

        fullnameField = new JTextField(15);
        fullnameField.setFont(UIConstants.LABEL_FONT);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(fullnameField, gbc);

        // Spacer
        gbc.gridy = 4;
        gbc.weighty = 1;
        formPanel.add(Box.createVerticalBox(), gbc);

        // Button
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton createButton = new JButton("Buat Akun");
        createButton.setFont(UIConstants.BUTTON_FONT);
        createButton.setBackground(new Color(34, 197, 94));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setPreferredSize(new Dimension(150, 40));
        createButton.addActionListener(e -> handleCreateAccount());
        buttonPanel.add(createButton);

        JButton clearButton = new JButton("Bersihkan");
        clearButton.setFont(UIConstants.BUTTON_FONT);
        clearButton.setFocusPainted(false);
        clearButton.setPreferredSize(new Dimension(150, 40));
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);

        gbc.gridy = 5;
        gbc.weighty = 0;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        return panel;
    }

    private void loadAccountData() {
        tableModel.setRowCount(0);
        List<User> students = DatabaseManager.getAllStudents();

        for (User student : students) {
            tableModel.addRow(new Object[]{
                student.getId(),
                student.getUsername(),
                student.getFullname(),
                student.getRole().toString()
            });
        }
    }

    private void handleCreateAccount() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullname = fullnameField.getText().trim();

        // Validasi username kosong
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username tidak boleh kosong!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        // Validasi password kosong
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Password tidak boleh kosong!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        // Validasi fullname kosong
        if (fullname.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nama lengkap tidak boleh kosong!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            fullnameField.requestFocus();
            return;
        }

        // Validasi username tidak duplikat
        if (DatabaseManager.usernameExists(username)) {
            JOptionPane.showMessageDialog(this,
                "Username '" + username + "' sudah terdaftar!\n" +
                "Silakan gunakan username lain.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
            return;
        }

        // Buat akun mahasiswa
        int userId = DatabaseManager.createStudentAccount(username, password, fullname);

        // Cek apakah berhasil dengan kondisi yang benar
        // Method createStudentAccount() mengembalikan userId (>0) jika berhasil, atau -1 jika gagal
        if (userId > 0) {
            JOptionPane.showMessageDialog(this,
                "Akun mahasiswa berhasil dibuat!\n\n" + 
                "Username: " + username + "\n" + 
                "Nama: " + fullname + "\n" +
                "ID: " + userId,
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
 
            clearForm();

            loadAccountData();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal membuat akun mahasiswa!\n" +
                "Silakan coba lagi.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Silakan pilih akun yang akan dihapus!",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        String fullname = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus akun mahasiswa?\n\n" +
            "Username: " + username + "\n" +
            "Nama: " + fullname,
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseManager.deleteStudentAccount(userId)) {
                JOptionPane.showMessageDialog(this,
                    "Akun mahasiswa berhasil dihapus!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                loadAccountData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Gagal menghapus akun mahasiswa!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        fullnameField.setText("");
        usernameField.requestFocus();
    }
}