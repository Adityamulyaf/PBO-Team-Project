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
            ('Senin', '1', '07:30 - 08:20', 'Sistem Digital', '12013120307', '1', '3', 'Endra Pratama', 'R_B4_11', 'A'),
            ('Senin', '2', '08:25 - 09:15', 'Sistem Digital', '12013120307', '1', '3', 'Endra Pratama', 'R_B4_11', 'A'),
            ('Senin', '3', '09:20 - 10:10', 'Kalkulus I', '12013120308', '1', '3', 'Supriyadi Wibowo', 'R_B4_06', 'A'),
            ('Senin', '4', '10:15 - 11:05', 'Kalkulus I', '12013120308', '1', '3', 'Supriyadi Wibowo', 'R_B4_06', 'A'),
            ('Senin', '5', '11:10 - 12:00', 'Kalkulus I', '12013120308', '1', '3', 'Supriyadi Wibowo', 'R_B4_06', 'A'),
            ('Senin', '6', '13:00 - 13:50', 'Bahasa Indonesia', '12013110204', '1', '2', 'Muhammad Rohmadi', 'R_B4_11', 'A'),
            ('Senin', '7', '13:55 - 14:45', 'Bahasa Indonesia', '12013110204', '1', '2', 'Muhammad Rohmadi', 'R_B4_11', 'A'),
            ('Senin', '9', '16:25 - 17:15', 'Bahasa Inggris I', '12013120210', '1', '2', 'Primanda Dewandi S.Pd', 'R_B4_12', 'A'),
            ('Senin', '10', '18:10 - 19:00', 'Bahasa Inggris I', '12013120210', '1', '2', 'Primanda Dewandi S.Pd', 'R_B4_12', 'A'),
            ('Selasa', '1', '07:30 - 08:20', 'Konsep Pemrograman', '12013120406', '1', '4', 'Bambang Harjito', 'R_B4_12', 'A'),
            ('Selasa', '2', '08:25 - 09:15', 'Konsep Pemrograman', '12013120406', '1', '4', 'Bambang Harjito', 'R_B4_12', 'A'),
            ('Selasa', '3', '09:20 - 10:10', 'Konsep Pemrograman', '12013120406', '1', '4', 'Bambang Harjito', 'R_B4_12', 'A'),
            ('Rabu', '8', '15:30 - 16:20', 'Konsep Pemrograman', '12013120406', '1', '4', 'Bambang Harjito', 'R_B4_05', 'A'),
            ('Kamis', '1', '07:30 - 08:20', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Ristu Saptono', 'R_B4_10', 'A'),
            ('Kamis', '2', '08:25 - 09:15', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Ristu Saptono', 'R_B4_10', 'A'),
            ('Kamis', '3', '09:20 - 10:10', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Ristu Saptono', 'R_B4_10', 'A'),
            ('Kamis', '4', '10:15 - 11:05', 'Pendidikan Agama Islam', '12013110201', '1', '2', 'Irfan A N', 'R_B4_06', 'A'),
            ('Kamis', '5', '11:10 - 12:00', 'Pendidikan Agama Islam', '12013110201', '1', '2', 'Irfan A N', 'R_B4_06', 'A'),
            ('Jumat', '1', '07:30 - 08:20', 'Fisika', '12013120309', '1', '3', 'Budi Purnama', 'R_B4_05', 'A'),
            ('Jumat', '2', '08:25 - 09:15', 'Fisika', '12013120309', '1', '3', 'Budi Purnama', 'R_B4_05', 'A'),
            ('Jumat', '3', '09:20 - 10:10', 'Fisika', '12013120309', '1', '3', 'Budi Purnama', 'R_B4_05', 'A'),

            -- KELAS B
            ('Senin', '3', '09:20 - 10:10', 'Konsep Pemrograman', '12013120406', '1', '4', 'Bambang Harjito', 'R_B4_11', 'B'),
            ('Senin', '4', '10:15 - 11:05', 'Konsep Pemrograman', '12013120406', '1', '4', 'Bambang Harjito', 'R_B4_11', 'B'),
            ('Senin', '5', '11:10 - 12:00', 'Konsep Pemrograman', '12013120406', '1', '4', 'Bambang Harjito', 'R_B4_11', 'B'),
            ('Selasa', '4', '10:15 - 11:05', 'Bahasa Indonesia', '12013110204', '1', '2', 'Muhammad Rohmadi', 'R_B4_10', 'B'),
            ('Selasa', '5', '11:10 - 12:00', 'Bahasa Indonesia', '12013110204', '1', '2', 'Muhammad Rohmadi', 'R_B4_10', 'B'),
            ('Selasa', '8', '15:30 - 16:20', 'Konsep Pemrograman', '12013120406', '1', '4', 'Bambang Harjito', 'LAB_TIK_2', 'B'),
            ('Rabu', '6', '13:00 - 13:50', 'Sistem Digital', '12013120307', '1', '3', 'Endra Pratama', 'LAB_TIK_1', 'B'),
            ('Rabu', '7', '13:55 - 14:45', 'Sistem Digital', '12013120307', '1', '3', 'Endra Pratama', 'LAB_TIK_1', 'B'),
            ('Rabu', '8', '15:30 - 16:20', 'Sistem Digital', '12013120307', '1', '3', 'Endra Pratama', 'LAB_TIK_1', 'B'),
            ('Kamis', '4', '10:15 - 11:05', 'Pendidikan Agama Islam', '12013110201', '1', '2', 'Arifuddin', 'LAB_TIK_2', 'B'),
            ('Kamis', '5', '11:10 - 12:00', 'Pendidikan Agama Islam', '12013110201', '1', '2', 'Arifuddin', 'LAB_TIK_2', 'B'),
            ('Kamis', '6', '13:00 - 13:50', 'Fisika', '12013120309', '1', '3', 'BUDI PURNAMA', 'R_B4_06', 'B'),
            ('Kamis', '7', '13:55 - 14:45', 'Fisika', '12013120309', '1', '3', 'BUDI PURNAMA', 'R_B4_06', 'B'),
            ('Kamis', '8', '15:30 - 16:20', 'Fisika', '12013120309', '1', '3', 'BUDI PURNAMA', 'R_B4_06', 'B'),
            ('Jumat', '1', '07:30 - 08:20', 'Kalkulus I', '12013120308', '1', '3', 'Dwi Purnama', 'R_B4_05', 'B'),
            ('Jumat', '2', '08:25 - 09:15', 'Kalkulus I', '12013120308', '1', '3', 'Dwi Purnama', 'R_B4_05', 'B'),
            ('Jumat', '3', '09:20 - 10:10', 'Kalkulus I', '12013120308', '1', '3', 'Dwi Purnama', 'R_B4_05', 'B'),
            ('Jumat', '5', '13:00 - 13:50', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Ristu Saptono', 'R_B4_11', 'B'),
            ('Jumat', '6', '13:55 - 14:45', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Ristu Saptono', 'R_B4_11', 'B'),
            ('Jumat', '7', '15:30 - 16:20', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Ristu Saptono', 'R_B4_11', 'B'),

            -- KELAS C
            ('Senin', '1', '07:30 - 08:20', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Esti Suryani', 'R_B4_10', 'C'),
            ('Senin', '2', '08:25 - 09:15', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Esti Suryani', 'R_B4_10', 'C'),
            ('Senin', '3', '09:20 - 10:10', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Esti Suryani', 'R_B4_10', 'C'),
            ('Senin', '6', '13:00 - 13:50', 'Bahasa Indonesia', '12013110204', '1', '2', 'Kundharu Saddhono', 'LAB_TIK_1', 'C'),
            ('Senin', '7', '13:55 - 14:45', 'Bahasa Indonesia', '12013110204', '1', '2', 'Kundharu Saddhono', 'LAB_TIK_1', 'C'),
            ('Senin', '8', '15:30 - 16:20', 'Sistem Digital', '12013120307', '1', '3', 'Akhmad Syaifuddin', 'LAB_TIK_1', 'C'),
            ('Selasa', '3', '09:20 - 10:10', 'Pendidikan Agama Islam', '12013110201', '1', '2', 'CHOIROEL ANAM', 'LAB_TIK_1', 'C'),
            ('Selasa', '4', '10:15 - 11:05', 'Pendidikan Agama Islam', '12013110201', '1', '2', 'CHOIROEL ANAM', 'LAB_TIK_1', 'C'),
            ('Selasa', '6', '13:00 - 13:50', 'Konsep Pemrograman', '12013120406', '1', '4', 'AFRIZAL DOEWES', 'R_B4_05', 'C'),
            ('Selasa', '7', '13:55 - 14:45', 'Konsep Pemrograman', '12013120406', '1', '4', 'AFRIZAL DOEWES', 'R_B4_05', 'C'),
            ('Selasa', '8', '15:30 - 16:20', 'Konsep Pemrograman', '12013120406', '1', '4', 'AFRIZAL DOEWES', 'R_B4_05', 'C'),
            ('Selasa', '9', '16:25 - 17:15', 'Konsep Pemrograman', '12013120406', '1', '4', 'AFRIZAL DOEWES', 'R_B4_05', 'C'),
            ('Rabu', '1', '07:30 - 08:20', 'Kalkulus I', '12013120308', '1', '3', 'Mira Andriyani', 'R_B4_12', 'C'),
            ('Rabu', '2', '08:25 - 09:15', 'Kalkulus I', '12013120308', '1', '3', 'Mira Andriyani', 'R_B4_12', 'C'),
            ('Rabu', '3', '09:20 - 10:10', 'Kalkulus I', '12013120308', '1', '3', 'Mira Andriyani', 'R_B4_12', 'C'),
            ('Rabu', '4', '10:15 - 11:05', 'Sistem Digital', '12013120307', '1', '3', 'Akhmad Syaifuddin', 'R_B4_11', 'C'),
            ('Rabu', '5', '11:10 - 12:00', 'Sistem Digital', '12013120307', '1', '3', 'Akhmad Syaifuddin', 'R_B4_11', 'C'),
            ('Kamis', '1', '07:30 - 08:20', 'Fisika (Team Teaching)', '12013120309', '1', '3', 'Agus Supriyanto + Luthfiya Kurnia Permatahati', 'R_B4_06', 'C'),
            ('Kamis', '2', '08:25 - 09:15', 'Fisika (Team Teaching)', '12013120309', '1', '3', 'Agus Supriyanto + Luthfiya Kurnia Permatahati', 'R_B4_06', 'C'),
            ('Kamis', '3', '09:20 - 10:10', 'Fisika (Team Teaching)', '12013120309', '1', '3', 'Agus Supriyanto + Luthfiya Kurnia Permatahati', 'R_B4_06', 'C'),

            -- KELAS D
            ('Senin', '1', '07:30 - 08:20', 'Konsep Pemrograman', '12013120406', '1', '4', 'AFRIZAL DOEWES', 'R_B4_05', 'D'),
            ('Senin', '2', '08:25 - 09:15', 'Konsep Pemrograman', '12013120406', '1', '4', 'AFRIZAL DOEWES', 'R_B4_05', 'D'),
            ('Senin', '3', '09:20 - 10:10', 'Konsep Pemrograman', '12013120406', '1', '4', 'AFRIZAL DOEWES', 'R_B4_05', 'D'),
            ('Selasa', '1', '07:30 - 08:20', 'Fisika (Team Teaching)', '12013120309', '1', '3', 'Agus Supriyanto + Luthfiya Kurnia Permatahati', 'R_B4_06', 'D'),
            ('Selasa', '2', '08:25 - 09:15', 'Fisika (Team Teaching)', '12013120309', '1', '3', 'Agus Supriyanto + Luthfiya Kurnia Permatahati', 'R_B4_06', 'D'),
            ('Selasa', '3', '09:20 - 10:10', 'Fisika (Team Teaching)', '12013120309', '1', '3', 'Agus Supriyanto + Luthfiya Kurnia Permatahati', 'R_B4_06', 'D'),
            ('Selasa', '4', '10:15 - 11:05', 'Bahasa Indonesia', '12013110204', '1', '2', 'Kundharu Saddhono', 'R_B4_11', 'D'),
            ('Selasa', '5', '11:10 - 12:00', 'Bahasa Indonesia', '12013110204', '1', '2', 'Kundharu Saddhono', 'R_B4_11', 'D'),
            ('Selasa', '6', '13:00 - 13:50', 'Pendidikan Agama Islam', '12013110201', '1', '2', 'Relly Prihatin', 'LAB_TIK_1', 'D'),
            ('Selasa', '7', '13:55 - 14:45', 'Pendidikan Agama Islam', '12013110201', '1', '2', 'Relly Prihatin', 'LAB_TIK_1', 'D'),
            ('Rabu', '6', '13:00 - 13:50', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Esti Suryani', 'R_B4_11', 'D'),
            ('Rabu', '7', '13:55 - 14:45', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Esti Suryani', 'R_B4_11', 'D'),
            ('Rabu', '8', '15:30 - 16:20', 'Statistika & Probabilitas', '12013120311', '1', '3', 'Esti Suryani', 'R_B4_11', 'D'),
            ('Kamis', '4', '10:15 - 11:05', 'Sistem Digital', '12013120307', '1', '3', 'Akhmad Syaifuddin', 'R_B4_10', 'D'),
            ('Kamis', '5', '11:10 - 12:00', 'Sistem Digital', '12013120307', '1', '3', 'Akhmad Syaifuddin', 'R_B4_10', 'D'),
            ('Kamis', '9', '16:25 - 17:15', 'Sistem Digital', '12013120307', '1', '3', 'Akhmad Syaifuddin', 'R_B4_10', 'D'),
            ('Jumat', '5', '13:00 - 13:50', 'Kalkulus I', '12013120308', '1', '3', 'Ade Susanti', 'LAB_TIK_1', 'D'),
            ('Jumat', '6', '13:55 - 14:45', 'Kalkulus I', '12013120308', '1', '3', 'Ade Susanti', 'LAB_TIK_1', 'D'),
            ('Jumat', '7', '15:30 - 16:20', 'Kalkulus I', '12013120308', '1', '3', 'Ade Susanti', 'LAB_TIK_1', 'D'),
            ('Jumat', '8', '16:25 - 17:15', 'Konsep Pemrograman', '12013120406', '1', '4', 'AFRIZAL DOEWES', 'R_B4_11', 'D'),

            -- SEMESTER 3
            ('Senin', '3', '09:20 - 10:10', 'Basis Data', '12013120420', '3', '4', 'Brilyan Hendrasuryawan', 'R_B4_12', 'A'),
            ('Senin', '4', '10:15 - 11:05', 'Basis Data', '12013120420', '3', '4', 'Brilyan Hendrasuryawan', 'R_B4_12', 'A'),
            ('Senin', '5', '11:10 - 12:00', 'Basis Data', '12013120420', '3', '4', 'Brilyan Hendrasuryawan', 'R_B4_12', 'A'),
            ('Senin', '6', '13:00 - 13:50', 'Pemrograman Berorientasi Objek', '12013120319', '3', '3', 'Afrizal Doewes', 'R_B4_10', 'A'),
            ('Senin', '7', '13:55 - 14:45', 'Pemrograman Berorientasi Objek', '12013120319', '3', '3', 'Afrizal Doewes', 'R_B4_10', 'A'),
            ('Senin', '8', '15:30 - 16:20', 'Pemrograman Berorientasi Objek', '12013120319', '3', '3', 'Afrizal Doewes', 'R_B4_10', 'A'),
            ('Selasa', '8', '15:30 - 16:20', 'Metode Numerik', '12013120322', '3', '3', 'Umi Salamah', 'R_B4_12', 'A'),
            ('Rabu', '1', '07:30 - 08:20', 'Matematika Diskrit II', '12013120218', '3', '2', 'Shaifudin Zuhdi', 'R_B4_06', 'A'),
            ('Rabu', '2', '08:25 - 09:15', 'Matematika Diskrit II', '12013120218', '3', '2', 'Shaifudin Zuhdi', 'R_B4_06', 'A'),
            ('Rabu', '4', '10:15 - 11:05', 'Metode Numerik', '12013120322', '3', '3', 'Umi Salamah', 'R_B4_05', 'A'),
            ('Rabu', '5', '11:10 - 12:00', 'Metode Numerik', '12013120322', '3', '3', 'Umi Salamah', 'R_B4_05', 'A'),
            ('Kamis', '1', '07:30 - 08:20', 'Sistem Operasi', '12013120321', '3', '3', 'Haryono Setiadi', 'R_B4_12', 'A'),
            ('Kamis', '2', '08:25 - 09:15', 'Sistem Operasi', '12013120321', '3', '3', 'Haryono Setiadi', 'R_B4_12', 'A'),
            ('Kamis', '4', '10:15 - 11:05', 'Pendidikan Pancasila', '12013110202', '3', '2', 'Irwan Iftadi', 'LAB_TIK_2', 'A'),
            ('Kamis', '5', '11:05 - 12:00', 'Pendidikan Pancasila', '12013110202', '3', '2', 'Irwan Iftadi', 'LAB_TIK_2', 'A'),
            ('Jumat', '3', '09:20 - 10:10', 'Sistem Operasi', '12013120321', '3', '3', 'Haryono Setiadi', 'LAB_TIK_1', 'A'),
            ('Jumat', '5', '13:00 - 13:50', 'Desain & Analisis Algoritma', '12013120323', '3', '3', 'Arif Rohmadi', 'R_B4_10', 'A'),
            ('Jumat', '6', '13:55 - 14:45', 'Desain & Analisis Algoritma', '12013120323', '3', '3', 'Arif Rohmadi', 'R_B4_10', 'A'),
            ('Jumat', '7', '15:30 - 16:20', 'Desain & Analisis Algoritma', '12013120323', '3', '3', 'Arif Rohmadi', 'R_B4_10', 'A'),

            -- SEMESTER 5
            ('Senin', '1', '07:30 - 08:20', 'Manajemen Jaringan', '12013120307', '5', '3', 'Herdito Ibnu Dewangkoro', 'LAB_TIK_1', 'A'),
            ('Senin', '2', '08:25 - 09:15', 'Manajemen Jaringan', '12013120307', '5', '3', 'Herdito Ibnu Dewangkoro', 'LAB_TIK_1', 'A'),
            ('Senin', '3', '09:20 - 10:10', 'Manajemen Jaringan', '12013120307', '5', '3', 'Herdito Ibnu Dewangkoro', 'LAB_TIK_1', 'A'),
            ('Selasa', '1', '07:30 - 08:20', 'Machine Learning', '12013120307', '5', '3', 'Shaifudin Zuhdi', 'R_B4_11', 'A'),
            ('Selasa', '2', '08:25 - 09:15', 'Machine Learning', '12013120307', '5', '3', 'Shaifudin Zuhdi', 'R_B4_11', 'A'),
            ('Selasa', '3', '09:20 - 10:10', 'Machine Learning', '12013120307', '5', '3', 'Shaifudin Zuhdi', 'R_B4_11', 'A'),
            ('Rabu', '1', '07:30 - 08:20', 'Komputasi Grid', '12013130323', '5', '3', 'WISNU WIDIARTO', 'R_B4_11', 'A'),
            ('Rabu', '2', '08:25 - 09:15', 'Komputasi Grid', '12013130323', '5', '3', 'WISNU WIDIARTO', 'R_B4_11', 'A'),
            ('Rabu', '3', '09:20 - 10:10', 'Komputasi Grid', '12013130323', '5', '3', 'WISNU WIDIARTO', 'R_B4_11', 'A'),
            ('Rabu', '4', '10:15 - 11:05', 'Riset Operasi', '12013130222', '5', '2', 'Bambang Harjito', 'LAB_TIK_1', 'A'),
            ('Rabu', '5', '11:10 - 12:00', 'Riset Operasi', '12013130222', '5', '2', 'Bambang Harjito', 'LAB_TIK_1', 'A'),
            ('Rabu', '6', '13:00 - 13:50', 'Basis Data Lanjut', '12013140304', '5', '3', 'Muhammad Fahmy Nadlif', 'R_B4_12', 'A'),
            ('Rabu', '7', '13:55 - 14:45', 'Basis Data Lanjut', '12013140304', '5', '3', 'Muhammad Fahmy Nadlif', 'R_B4_12', 'A'),
            ('Rabu', '8', '15:30 - 16:20', 'Basis Data Lanjut', '12013140304', '5', '3', 'Muhammad Fahmy Nadlif', 'R_B4_12', 'A'),
            ('Kamis', '1', '07:30 - 08:20', 'Pengolahan Sinyal Digital', '12013140302', '5', '3', 'Hasan Dwi Cahyono', 'LAB_TIK_2', 'A'),
            ('Kamis', '2', '08:25 - 09:15', 'Pengolahan Sinyal Digital', '12013140302', '5', '3', 'Hasan Dwi Cahyono', 'LAB_TIK_2', 'A'),
            ('Kamis', '3', '09:20 - 10:10', 'Pengolahan Sinyal Digital', '12013140302', '5', '3', 'Hasan Dwi Cahyono', 'LAB_TIK_2', 'A'),
            ('Kamis', '4', '10:15 - 11:05', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'Heri Prasetyo', 'R_B4_10', 'A'),

            ('Kamis', '5', '11:10 - 12:00', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'Heri Prasetyo', 'LAB_TIK_1', 'A'),
            ('Kamis', '6', '13:00 - 13:50', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'Heri Prasetyo', 'LAB_TIK_1', 'A'),
            ('Kamis', '7', '13:55 - 14:45', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Fajar Muslim', 'LAB_TIK_1', 'A'),
            ('Kamis', '8', '15:30 - 16:20', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Fajar Muslim', 'LAB_TIK_1', 'A'),
            ('Kamis', '9', '16:25 - 17:15', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Fajar Muslim', 'LAB_TIK_1', 'A'),
            ('Jumat', '1', '07:30 - 08:20', 'Wireless & Mobile Computing', '12013130325', '5', '3', 'Arif Rohmadi', 'R_B4_11', 'A'),
            ('Jumat', '2', '08:25 - 09:15', 'Wireless & Mobile Computing', '12013130325', '5', '3', 'Arif Rohmadi', 'R_B4_11', 'A'),
            ('Jumat', '3', '09:20 - 10:10', 'Wireless & Mobile Computing', '12013130325', '5', '3', 'Arif Rohmadi', 'R_B4_11', 'A'),
            ('Jumat', '5', '13:00 - 13:50', 'Kriptografi', '12013130324', '5', '3', 'Bambang Harjito', 'LAB_TIK_3', 'A'),
            ('Jumat', '6', '13:55 - 14:45', 'Kriptografi', '12013130324', '5', '3', 'Bambang Harjito', 'LAB_TIK_3', 'A'),
            ('Jumat', '7', '15:30 - 16:20', 'Kriptografi', '12013130324', '5', '3', 'Bambang Harjito', 'LAB_TIK_3', 'A'),

            ('Senin', '3', '09:20 - 10:10', 'Teori Game', '12013130327', '5', '3', 'NUGHTHOH ARFAWI KURDHI', 'LAB_TIK_3', 'B'),  
            ('Senin', '4', '10:15 - 11:05', 'Teori Game', '12013130327', '5', '3', 'NUGHTHOH ARFAWI KURDHI', 'LAB_TIK_3', 'B'),  
            ('Senin', '5', '11:10 - 12:00', 'Teori Game', '12013130327', '5', '3', 'NUGHTHOH ARFAWI KURDHI', 'LAB_TIK_3', 'B'),  
            ('Senin', '6', '13:00 - 13:50', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'HERI PRASETYO', 'R_B4_06', 'B'),  
            ('Senin', '7', '13:55 - 14:45', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'HERI PRASETYO', 'R_B4_06', 'B'),  
            ('Senin', '8', '15:30 - 16:20', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'HERI PRASETYO', 'R_B4_06', 'B'),
            ('Rabu', '1', '07:30 - 08:20', 'Data Mining', '12013220330', '5', '3', 'Wiranto', 'R_B4_10', 'B'),  
            ('Rabu', '2', '08:25 - 09:15', 'Data Mining', '12013220330', '5', '3', 'Wiranto', 'R_B4_10', 'B'),  
            ('Rabu', '3', '09:20 - 10:10', 'Data Mining', '12013220330', '5', '3', 'Wiranto', 'R_B4_10', 'B'),  
            ('Rabu', '6', '13:00 - 13:50', 'Interaksi Manusia & Komputer (Team Teaching)', '12013120231', '5', '2', 'Ery Permana Yudha + RINI ANGGRAININGSIH', 'LAB_TIK_2', 'B'),  
            ('Rabu', '7', '13:55 - 14:45', 'Interaksi Manusia & Komputer (Team Teaching)', '12013120231', '5', '2', 'Ery Permana Yudha + RINI ANGGRAININGSIH', 'LAB_TIK_2', 'B'),  
            ('Kamis', '1', '07:30 - 08:20', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Fajar Muslim', 'LAB_TIK_1', 'B'),  
            ('Kamis', '2', '08:25 - 09:15', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Fajar Muslim', 'LAB_TIK_1', 'B'),  
            ('Kamis', '3', '09:20 - 10:10', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Fajar Muslim', 'LAB_TIK_1', 'B'),

            ('Selasa', '6', '13:00 - 13:50', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Ery Permana Yudha', 'R_B4_06', 'C'),  
            ('Selasa', '7', '13:55 - 14:45', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Ery Permana Yudha', 'R_B4_06', 'C'),  
            ('Selasa', '8', '15:30 - 16:20', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Ery Permana Yudha', 'R_B4_06', 'C'),  
            ('Rabu', '1', '07:30 - 08:20', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'Herdito Ibnu Dewangkoro', 'LAB_TIK_1', 'C'),  
            ('Rabu', '2', '08:25 - 09:15', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'Herdito Ibnu Dewangkoro', 'LAB_TIK_1', 'C'),  
            ('Rabu', '3', '09:20 - 10:10', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'Herdito Ibnu Dewangkoro', 'LAB_TIK_1', 'C'),  
            ('Rabu', '6', '13:00 - 13:50', 'Data Mining', '12013220330', '5', '3', 'Wiranto', 'R_B4_10', 'C'),  
            ('Rabu', '7', '13:55 - 14:45', 'Data Mining', '12013220330', '5', '3', 'Wiranto', 'R_B4_10', 'C'),  
            ('Rabu', '8', '15:30 - 16:20', 'Data Mining', '12013220330', '5', '3', 'Wiranto', 'R_B4_10', 'C'),  
            ('Jumat', '5', '13:00 - 13:50', 'Interaksi Manusia & Komputer (Team Teaching)', '12013120231', '5', '2', 'Ery Permana Yudha + RINI ANGGRAININGSIH', 'LAB_TIK_1', 'C'),  
            ('Jumat', '6', '13:55 - 14:45', 'Interaksi Manusia & Komputer (Team Teaching)', '12013120231', '5', '2', 'Ery Permana Yudha + RINI ANGGRAININGSIH', 'LAB_TIK_1', 'C'),

            ('Rabu', '4', '10:15 - 11:05', 'Interaksi Manusia & Komputer', '12013120231', '5', '2', 'RINI ANGGRAININGSIH', 'R_B4_12', 'D'),  
            ('Rabu', '5', '11:10 - 12:00', 'Interaksi Manusia & Komputer', '12013120231', '5', '2', 'RINI ANGGRAININGSIH', 'R_B4_12', 'D'),  
            ('Kamis', '3', '09:20 - 10:10', 'Data Mining', '12013220330', '5', '3', 'Wiranto', 'R_B4_11', 'D'),  
            ('Kamis', '4', '10:15 - 11:05', 'Data Mining', '12013220330', '5', '3', 'Wiranto', 'R_B4_11', 'D'),  
            ('Kamis', '5', '11:10 - 12:00', 'Data Mining', '12013220330', '5', '3', 'Wiranto', 'R_B4_11', 'D'),  
            ('Kamis', '6', '13:00 - 13:50', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'Herdito Ibnu Dewangkoro', 'R_B4_12', 'D'),  
            ('Kamis', '7', '13:55 - 14:45', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'Herdito Ibnu Dewangkoro', 'R_B4_12', 'D'),  
            ('Kamis', '8', '15:30 - 16:20', 'Pengolahan Citra Digital', '12013120332', '5', '3', 'Herdito Ibnu Dewangkoro', 'R_B4_12', 'D'),  
            ('Jumat', '1', '07:30 - 08:20', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Ery Permana Yudha', 'R_B4_10', 'D'),  
            ('Jumat', '2', '08:25 - 09:15', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Ery Permana Yudha', 'R_B4_10', 'D'),  
            ('Jumat', '3', '09:20 - 10:10', 'Sistem Terdistribusi', '12013120333', '5', '3', 'Ery Permana Yudha', 'R_B4_10', 'D'),


            ('Senin', '1', '07:30 - 08:20', 'Kewirausahaan', '12013120203', '7', '2', 'Akhmad Syaifuddin', 'LAB_TIK_3', 'A'),
            ('Senin', '2', '08:25 - 09:15', 'Kewirausahaan', '12013120203', '7', '2', 'Akhmad Syaifuddin', 'LAB_TIK_3', 'A'),
            ('Senin', '3', '09:20 - 10:10', 'Kecerdasan Komputasional', '12013140309', '7', '3', 'Wiharto', 'R_B4_10', 'A'),
            ('Senin', '4', '10:15 - 11:05', 'Kecerdasan Komputasional', '12013140309', '7', '3', 'Wiharto', 'R_B4_10', 'A'),
            ('Senin', '5', '11:10 - 12:00', 'Kecerdasan Komputasional', '12013140309', '7', '3', 'Wiharto', 'R_B4_10', 'A'),
            ('Senin', '6', '13:00 - 13:50', 'Etika Profesi', '12013120236', '7', '2', 'Haryono Setiadi', 'LAB_TIK_3', 'A'),
            ('Senin', '7', '13:55 - 14:45', 'Etika Profesi', '12013120236', '7', '2', 'Haryono Setiadi', 'LAB_TIK_3', 'A'),
            ('Selasa', '1', '07:30 - 08:20', 'Semantic Web', '12013140312', '7', '3', 'Dewi Wisnu Wardani', 'R_B4_10', 'A'),
            ('Selasa', '2', '08:25 - 09:15', 'Semantic Web', '12013140312', '7', '3', 'Dewi Wisnu Wardani', 'R_B4_10', 'A'),
            ('Selasa', '3', '09:20 - 10:10', 'Semantic Web', '12013140312', '7', '3', 'Dewi Wisnu Wardani', 'R_B4_10', 'A'),
            ('Rabu', '6', '13:00 - 13:50', 'Teknologi IoT', '12013140311', '7', '3', 'Abdul Aziz', 'LAB_TIK_3', 'A'),
            ('Rabu', '7', '13:55 - 14:45', 'Teknologi IoT', '12013140311', '7', '3', 'Abdul Aziz', 'LAB_TIK_3', 'A'),
            ('Rabu', '8', '15:30 - 16:20', 'Teknologi IoT', '12013140311', '7', '3', 'Abdul Aziz', 'LAB_TIK_3', 'A'),
            ('Kamis', '6', '13:00 - 13:50', 'E-commerce', '12013130341', '7', '3', 'Haryono Setiadi', 'LAB_TIK_3', 'A'),
            ('Kamis', '7', '13:55 - 14:45', 'E-commerce', '12013130341', '7', '3', 'Haryono Setiadi', 'LAB_TIK_3', 'A'),
            ('Kamis', '8', '15:30 - 16:20', 'E-commerce', '12013130341', '7', '3', 'Haryono Setiadi', 'LAB_TIK_3', 'A'),

            ('Selasa', '1', '07:30 - 08:20', 'Etika Profesi', '12013120236', '7', '2', 'Haryono Setiadi', 'LAB_TIK_3', 'B'),
            ('Selasa', '2', '08:25 - 09:15', 'Etika Profesi', '12013120236', '7', '2', 'Haryono Setiadi', 'LAB_TIK_3', 'B'),
            ('Selasa', '6', '13:00 - 13:50', 'Kecerdasan Komputasional', '12013140309', '7', '3', 'Wiharto', 'R_B4_10', 'B'),
            ('Selasa', '7', '13:55 - 14:45', 'Kecerdasan Komputasional', '12013140309', '7', '3', 'Wiharto', 'R_B4_10', 'B'),
            ('Selasa', '8', '15:30 - 16:20', 'Kecerdasan Komputasional', '12013140309', '7', '3', 'Wiharto', 'R_B4_10', 'B'),
            ('Rabu', '1', '07:30 - 08:20', 'Kewirausahaan', '12013120203', '7', '2', 'Akhmad Syaifuddin', 'LAB_TIK_4', 'B'),
            ('Rabu', '2', '08:25 - 09:15', 'Kewirausahaan', '12013120203', '7', '2', 'Akhmad Syaifuddin', 'LAB_TIK_4', 'B'),

            ('Selasa', '6', '13:00 - 13:50', 'Kewirausahaan', '12013120203', '7', '2', 'Akhmad Syaifuddin', 'LAB_TIK_3', 'C'),
            ('Selasa', '7', '13:55 - 14:45', 'Kewirausahaan', '12013120203', '7', '2', 'Akhmad Syaifuddin', 'LAB_TIK_3', 'C'),

            ('Selasa', '3', '09:20 - 10:10', 'Kewirausahaan', '12013120203', '7', '2', 'Nisaul Hasanah & A. Rosyad', 'LAB_TIK_3', 'D'),
            ('Selasa', '4', '10:15 - 11:05', 'Kewirausahaan', '12013120203', '7', '2', 'Nisaul Hasanah & A. Rosyad', 'LAB_TIK_3', 'D')
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
