// controller/Database.java

package controller;

import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:booking_fatisda.db";
    private static Connection conn;

    public static void initialize() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            try (Statement s = conn.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON");
                s.execute("PRAGMA journal_mode = WAL");
            } catch (SQLException e) {
                System.err.println("Eksekusi PRAGMA gagal: " + e.getMessage());
            }

            createTables();
            insertDefaultData();
        } catch (SQLException e) {
            System.err.println("Inisialisasi database gagal!");
            e.printStackTrace();
        }
    }

    private static void createTables() throws SQLException {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                fullname TEXT NOT NULL,
                role TEXT NOT NULL
            )
        """;

        String createRooms = """
            CREATE TABLE IF NOT EXISTS rooms (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL
            )
        """;

        String createBookings = """
            CREATE TABLE IF NOT EXISTS bookings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                room_id TEXT NOT NULL,
                date TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL,
                purpose TEXT NOT NULL,
                borrower TEXT NOT NULL,
                status TEXT NOT NULL,
                FOREIGN KEY (room_id) REFERENCES rooms(id)
            )
        """;

        String createClassSchedules = """
            CREATE TABLE IF NOT EXISTS class_schedules (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                day TEXT NOT NULL,
                session TEXT NOT NULL,
                time TEXT NOT NULL,
                course_name TEXT NOT NULL,
                course_code TEXT NOT NULL,
                semester TEXT NOT NULL,
                sks TEXT NOT NULL,
                lecturer TEXT NOT NULL,
                room_id TEXT NOT NULL,
                class_name TEXT NOT NULL,
                FOREIGN KEY (room_id) REFERENCES rooms(id)
            )
        """;

        String createIndex = """
            CREATE INDEX IF NOT EXISTS idx_bookings_room_date 
            ON bookings(room_id, date)
        """;

        String createScheduleIndex = """
            CREATE INDEX IF NOT EXISTS idx_schedules_room_day 
            ON class_schedules(room_id, day)
        """;

        Statement stmt = conn.createStatement();
        stmt.execute(createUsers);
        stmt.execute(createRooms);
        stmt.execute(createBookings);
        stmt.execute(createClassSchedules);
        stmt.execute(createIndex);
        stmt.execute(createScheduleIndex);
    }

    private static void insertDefaultData() throws SQLException {
        String checkUser = "SELECT COUNT(*) FROM users";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(checkUser);

        if (rs.next() && rs.getInt(1) == 0) {
            String insertUsers = """
                INSERT INTO users (username, password, fullname, role) VALUES
                ('admin', 'admin123', 'Administrator', 'ADMIN'),
                ('mhs', 'mhs123', 'Mahasiswa Demo', 'MAHASISWA')
            """;
            stmt.execute(insertUsers);
        }

        String checkRooms = "SELECT COUNT(*) FROM rooms";
        rs = stmt.executeQuery(checkRooms);

        if (rs.next() && rs.getInt(1) == 0) {
            String insertRooms = """
                INSERT INTO rooms (id, name) VALUES
                ('R_B4_11', 'R. B4. 11'),
                ('R_B4_10', 'R. B4. 10'),
                ('R_B4_08', 'R. B4. 08'),
                ('R_B4_04', 'R. B4. 04'),
                ('R_B4_06', 'R. B4. 06'),
                ('R_B4_12', 'R. B4. 12'),
                ('R_B4_05', 'R. B4. 05'),
                ('LAB_TIK_1', 'Lab. TIK 1'),
                ('LAB_TIK_2', 'Lab. TIK 2'),
                ('LAB_TIK_3', 'Lab. TIK 3'),
                ('LAB_TIK_4', 'Lab. TIK 4')
            """;
            stmt.execute(insertRooms);
        }

        String checkSchedules = "SELECT COUNT(*) FROM class_schedules";
        rs = stmt.executeQuery(checkSchedules);

        if (rs.next() && rs.getInt(1) == 0) {
            insertDefaultSchedules();
        }
    }

    private static void insertDefaultSchedules() throws SQLException {
        String insertSchedules = """
            INSERT INTO class_schedules (day, session, time, course_name, course_code, semester, sks, lecturer, room_id, class_name) VALUES
            ('Senin', '1', '07:30 - 08:20', 'Sistem Digital', '12013120307', '1', '3', 'Endra Pratama', 'R_B4_11', 'A')
        """;
        Statement stmt = conn.createStatement();
        stmt.execute(insertSchedules);
    }

    // User Authentication
    public static User authenticateUser(String username, String password) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("fullname"),
                    UserRole.valueOf(rs.getString("role"))
                );
            }
        } catch (SQLException e) {
            System.err.println("Terjadi kesalahan saat autentikasi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }
        return null;
    }

    // User Management
    public static List<User> getAllStudents() {
        List<User> students = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM users WHERE role = ? ORDER BY id";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, UserRole.MAHASISWA.toString());

            rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("fullname"),
                    UserRole.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data mahasiswa: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }
        return students;
    }

    public static boolean usernameExists(String username) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Gagal memeriksa username: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }
        return false;
    }

    public static int createStudentAccount(String username, String password, String fullname) {
    // Return:
    // >0 : new user id (success)
    // -1 : general failure
    // -2 : duplicate username (constraint)
    if (conn == null) {
        System.err.println("Koneksi database null di createStudentAccount()");
        return -1;
    }

    String sql = "INSERT INTO users (username, password, fullname, role) VALUES (?, ?, ?, ?)";
    try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        pstmt.setString(1, username);
        pstmt.setString(2, password); // di hash ngga ya? ngga usah lah ribet
        pstmt.setString(3, fullname);
        pstmt.setString(4, UserRole.MAHASISWA.toString());

        int affected = pstmt.executeUpdate();
        if (affected == 0) {
            System.err.println("Tidak ada baris yang terpengaruh saat menambahkan user: " + username);
            return -1;
        }

        // generate key JDBC dulu, ngefiks yang dia nampilin alert gagal meskipun berhasil
        try (ResultSet gk = pstmt.getGeneratedKeys()) {
            if (gk != null && gk.next()) {
                int newId = gk.getInt(1);
                System.out.println("Akun dibuat. ID (generatedKeys): " + newId);
                return newId;
            }
        } catch (SQLException ex) {
            System.err.println("getGeneratedKeys() gagal: " + ex.getMessage());
        }

        // fallback
        try (Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT last_insert_rowid()")) {
            if (rs != null && rs.next()) {
                int lastId = rs.getInt(1);
                System.out.println("Akun dibuat. ID (last_insert_rowid): " + lastId);
                return lastId;
            }
        } catch (SQLException ex) {
            System.err.println("Fallback last_insert_rowid() gagal: " + ex.getMessage());
        }

        System.out.println("Akun dibuat tetapi ID tidak dapat diambil. Mengembalikan 1 sebagai fallback.");
        return 1;

    } catch (SQLException e) {
        int errorCode = e.getErrorCode();
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
        System.err.println("SQLException di createStudentAccount: code=" + errorCode + " msg=" + e.getMessage());

        if (errorCode == 19 || message.contains("constraint") || message.contains("unique")) {
            System.err.println("Terjadi duplikasi username: " + username);
            return -2;
        }

        e.printStackTrace();
        return -1;
    }
}


    public static boolean deleteStudentAccount(int userId) {
        PreparedStatement pstmt = null;
        try {
            String query = "DELETE FROM users WHERE id = ? AND role = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setString(2, UserRole.MAHASISWA.toString());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Akun berhasil dihapus. User ID: " + userId);
                return true;
            }
            
            System.err.println("Gagal menghapus akun: tidak ditemukan user yang cocok");
            return false;
            
        } catch (SQLException e) {
            System.err.println("Gagal menghapus akun mahasiswa: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt);
        }
    }

    // Booking management
    public static int createBooking(String roomId, LocalDate date, LocalTime startTime,
                                    LocalTime endTime, String purpose, String borrower) {
        return createBookingWithStatus(roomId, date, startTime, endTime, purpose, borrower, BookingStatus.PENDING);
    }

    public static int createBookingAsApproved(String roomId, LocalDate date, LocalTime startTime,
                                             LocalTime endTime, String purpose, String borrower) {
        return createBookingWithStatus(roomId, date, startTime, endTime, purpose, borrower, BookingStatus.APPROVED);
    }

    private static int createBookingWithStatus(String roomId, LocalDate date, LocalTime startTime,
                                           LocalTime endTime, String purpose, String borrower,
                                           BookingStatus status) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = """
                INSERT INTO bookings (room_id, date, start_time, end_time, purpose, borrower, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomId);
            pstmt.setString(2, date.toString());
            pstmt.setString(3, startTime.toString());
            pstmt.setString(4, endTime.toString());
            pstmt.setString(5, purpose);
            pstmt.setString(6, borrower);
            pstmt.setString(7, status.toString());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }

            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT last_insert_rowid()");
            if (rs.next()) {
                return rs.getInt(1);
            }

            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeResources(rs, pstmt);
        }
    }

    public static List<Booking> getBookingsByDateAndTime(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Booking> bookings = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT * FROM bookings WHERE date = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, date.toString());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("id"),
                    rs.getString("room_id"),
                    LocalDate.parse(rs.getString("date")),
                    LocalTime.parse(rs.getString("start_time")),
                    LocalTime.parse(rs.getString("end_time")),
                    rs.getString("purpose"),
                    rs.getString("borrower"),
                    BookingStatus.valueOf(rs.getString("status"))
                );

                if (booking.overlaps(date, startTime, endTime)) {
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil booking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }

        return bookings;
    }

    public static List<Booking> getBookingsByRoomAndDate(String roomId, LocalDate date) {
        List<Booking> bookings = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM bookings WHERE room_id = ? AND date = ? ORDER BY start_time";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomId);
            pstmt.setString(2, date.toString());

            rs = pstmt.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("id"),
                    rs.getString("room_id"),
                    LocalDate.parse(rs.getString("date")),
                    LocalTime.parse(rs.getString("start_time")),
                    LocalTime.parse(rs.getString("end_time")),
                    rs.getString("purpose"),
                    rs.getString("borrower"),
                    BookingStatus.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil booking berdasarkan ruangan dan tanggal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }
        return bookings;
    }

    public static List<Booking> getPendingBookings() {
        List<Booking> bookings = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM bookings WHERE status = ? ORDER BY date, start_time";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, BookingStatus.PENDING.toString());

            rs = pstmt.executeQuery();
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("id"),
                    rs.getString("room_id"),
                    LocalDate.parse(rs.getString("date")),
                    LocalTime.parse(rs.getString("start_time")),
                    LocalTime.parse(rs.getString("end_time")),
                    rs.getString("purpose"),
                    rs.getString("borrower"),
                    BookingStatus.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil booking yang pending: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }
        return bookings;
    }

    public static void updateBookingStatus(int bookingId, BookingStatus status) {
        PreparedStatement pstmt = null;
        try {
            String query = "UPDATE bookings SET status = ? WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, status.toString());
            pstmt.setInt(2, bookingId);

            pstmt.executeUpdate();
            System.out.println("Status booking diperbarui. ID: " + bookingId + " -> " + status);
        } catch (SQLException e) {
            System.err.println("Gagal memperbarui status booking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(null, pstmt);
        }
    }

    public static boolean deleteBooking(int bookingId) {
        PreparedStatement pstmt = null;
        try {
            String query = "DELETE FROM bookings WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, bookingId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Booking berhasil dihapus. ID: " + bookingId);
            } else {
                System.err.println("Gagal menghapus booking: tidak ditemukan booking dengan ID " + bookingId);
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt);
        }
    }

    public static List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM bookings ORDER BY date DESC, start_time DESC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("id"),
                    rs.getString("room_id"),
                    LocalDate.parse(rs.getString("date")),
                    LocalTime.parse(rs.getString("start_time")),
                    LocalTime.parse(rs.getString("end_time")),
                    rs.getString("purpose"),
                    rs.getString("borrower"),
                    BookingStatus.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua booking: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt);
        }
        return bookings;
    }

    // Class Schedule Management
    public static List<ClassSchedule> getAllClassSchedules() {
        List<ClassSchedule> schedules = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String query = """
                SELECT * FROM class_schedules
                ORDER BY CASE day
                    WHEN 'Senin' THEN 1
                    WHEN 'Selasa' THEN 2
                    WHEN 'Rabu' THEN 3
                    WHEN 'Kamis' THEN 4
                    WHEN 'Jumat' THEN 5
                    WHEN 'Sabtu' THEN 6
                    WHEN 'Minggu' THEN 7
                    ELSE 99
                END, CAST(session AS INTEGER)
            """;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                schedules.add(new ClassSchedule(
                    rs.getInt("id"),
                    rs.getString("day"),
                    rs.getString("session"),
                    rs.getString("time"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("semester"),
                    rs.getString("sks"),
                    rs.getString("lecturer"),
                    rs.getString("room_id"),
                    rs.getString("class_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil semua jadwal kuliah: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt);
        }
        return schedules;
    }


    public static List<ClassSchedule> getSchedulesByRoomAndDay(String roomId, String day) {
        List<ClassSchedule> schedules = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM class_schedules WHERE room_id = ? AND day = ? ORDER BY CAST(session AS INTEGER)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomId);
            pstmt.setString(2, day);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                schedules.add(new ClassSchedule(
                    rs.getInt("id"),
                    rs.getString("day"),
                    rs.getString("session"),
                    rs.getString("time"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("semester"),
                    rs.getString("sks"),
                    rs.getString("lecturer"),
                    rs.getString("room_id"),
                    rs.getString("class_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil jadwal berdasarkan ruangan dan hari: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }
        return schedules;
    }

    public static List<ClassSchedule> getSchedulesByDate(LocalDate date) {
        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
        List<ClassSchedule> schedules = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM class_schedules WHERE day = ? ORDER BY CAST(session AS INTEGER)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, dayName);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                schedules.add(new ClassSchedule(
                    rs.getInt("id"),
                    rs.getString("day"),
                    rs.getString("session"),
                    rs.getString("time"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("semester"),
                    rs.getString("sks"),
                    rs.getString("lecturer"),
                    rs.getString("room_id"),
                    rs.getString("class_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil jadwal berdasarkan tanggal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }
        return schedules;
    }

    public static int createClassSchedule(String day, String session, String time, String courseName,
                                         String courseCode, String semester, String sks, String lecturer,
                                         String roomId, String className) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = """
                INSERT INTO class_schedules (day, session, time, course_name, course_code, semester, sks, lecturer, room_id, class_name)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, day);
            pstmt.setString(2, session);
            pstmt.setString(3, time);
            pstmt.setString(4, courseName);
            pstmt.setString(5, courseCode);
            pstmt.setString(6, semester);
            pstmt.setString(7, sks);
            pstmt.setString(8, lecturer);
            pstmt.setString(9, roomId);
            pstmt.setString(10, className);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Jadwal kuliah dibuat. ID: " + id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal membuat jadwal kuliah: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }
        return -1;
    }

    public static boolean deleteClassSchedule(int scheduleId) {
        PreparedStatement pstmt = null;
        try {
            String query = "DELETE FROM class_schedules WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, scheduleId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Jadwal kuliah berhasil dihapus. ID: " + scheduleId);
            } else {
                System.err.println("Gagal menghapus jadwal kuliah: tidak ditemukan ID " + scheduleId);
            }
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Gagal menghapus jadwal kuliah: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt);
        }
    }

    public static String getRoomName(String roomId) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT name FROM rooms WHERE id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil nama ruangan: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }
        return roomId;
    }

    private static void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            System.err.println("Gagal menutup ResultSet: " + e.getMessage());
        }
        
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Gagal menutup Statement: " + e.getMessage());
        }
    }

    public static void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Koneksi database berhasil ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}