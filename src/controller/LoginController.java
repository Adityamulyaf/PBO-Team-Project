package controller;

// Asumsi kita akan menggunakan kelas yang menangani koneksi database di sini
// import model.DatabaseManager; 

public class LoginController {
    
    // --- Metode Utama untuk Otentikasi ---
    /**
     * Memverifikasi kredensial pengguna berdasarkan peran (Admin atau Mahasiswa).
     * @param username NIM atau Username Admin
     * @param password Password pengguna
     * @param role "Admin" atau "Mahasiswa"
     * @return true jika kredensial valid, false jika tidak.
     */
    public boolean authenticate(String username, String password, String role) {
        
        // 1. Validasi Input Dasar
        if (username.isEmpty() || password.isEmpty()) {
            // Sebaiknya View (LoginFrame) yang menampilkan pesan, 
            // tapi Controller bisa mengembalikan false jika input kosong.
            return false;
        }

        // 2. Logika Verifikasi Berdasarkan Peran
        if (role.equalsIgnoreCase("Admin")) {
            // Di sini nanti Anda akan memanggil metode dari DatabaseManager
            // Contoh: return DatabaseManager.verifyAdmin(username, password);
            
            // --- SIMULASI DUMMY ADMIN ---
            if (username.equals("admin") && password.equals("fitsuns123")) {
                System.out.println("Controller: Admin login berhasil.");
                return true;
            }
            
        } else if (role.equalsIgnoreCase("Mahasiswa")) {
            // Di sini nanti Anda akan memanggil metode dari DatabaseManager
            // Contoh: return DatabaseManager.verifyMahasiswa(username, password);
            
            // --- SIMULASI DUMMY MAHASISWA ---
            // Kita asumsikan username Mahasiswa adalah NIM
            if (username.length() == 10 && password.equals("mahasiswa123")) {
                System.out.println("Controller: Mahasiswa login berhasil.");
                return true;
            }
        }

        // Jika tidak ada peran yang cocok atau kredensial salah
        return false;
    }
    
    // Anda bisa menambahkan metode lain yang terkait dengan manajemen sesi login
    
    /**
     * Metode dummy untuk mendapatkan detail pengguna setelah login.
     * Nantinya akan mengambil data dari DB.
     * @param username NIM atau Username
     * @return Nama pengguna
     */
    public String getUserName(String username) {
        if (username.equals("admin")) {
            return "Bapak/Ibu Dekan FITS";
        } else if (username.startsWith("I0")) {
            return "Mahasiswa Aang Setiawan";
        }
        return "Pengguna";
    }
}