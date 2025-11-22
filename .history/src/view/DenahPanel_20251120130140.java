public DenahPanel(String role) {
        this.userRole = role;
        
        ruanganList = new ArrayList<>();
        
        // --- Ukuran Dasar Ruangan ---
        int stdWidth = 100;  // Lebar standar ruangan
        int stdHeight = 100; // Tinggi standar ruangan
        
        // Hitung total lebar denah untuk centering
        int totalDenahWidth = (stdWidth * 2) + // R.B4.11, R.B4.10
                              (stdHeight + roomGap) + // R.B4.08 (tegak)
                              (stdWidth * 2) + (roomGap * 2) + // 2 blok abu-abu kiri
                              (stdWidth + roomGap) + // Lobby
                              (stdWidth + roomGap) + // Blok abu-abu tengah kanan
                              (stdWidth + roomGap) + // R.B4.04
                              (stdWidth + roomGap) + // R.B4.06 & R.B4.12
                              (stdHeight + roomGap) + // R.B4.05
                              (stdWidth * 2) + (roomGap * 3) + // Lab TIK
                              (margin * 2);
        
        int totalDenahHeight = (stdHeight * 2) + roomGap + (margin * 2);
        
        // Panel size dengan extra space
        int panelWidth = 1200;
        int panelHeight = 400;
        
        // Hitung offset untuk centering
        int offsetX = (panelWidth - totalDenahWidth) / 2 + margin;
        int offsetY = (panelHeight - totalDenahHeight) / 2 + margin;
        
        // --- Koordinat Awal (Centered) ---
        int currentX = offsetX;
        int currentY = offsetY;
        
        // --- Sisi Kiri Denah ---
        // R.B4.11 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.11", currentX, currentY, stdWidth, stdHeight, true, "Peminjaman"));
        // R.B4.10 (Hijau/Available) - Sesuai gambar baru
        ruanganList.add(new RoomData("R.B4.10", currentX, currentY + stdHeight + roomGap, stdWidth, stdHeight, true, "Peminjaman"));
        
        currentX += stdWidth + roomGap;
        
        // R.B4.08 (Tegak) (Hijau/Available) - Sesuai gambar baru
        ruanganList.add(new RoomData("R.B4.08", currentX, currentY, stdHeight, (stdHeight * 2) + roomGap, true, "Peminjaman"));
        
        currentX += stdHeight + roomGap; 
        
        // --- Area Tengah (Abu-abu & Lobby) ---
        // Blok Abu-abu Kiri (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, stdWidth, stdHeight, false, "Non-Peminjaman"));
        ruanganList.add(new RoomData("", currentX, currentY + stdHeight + roomGap, stdWidth, stdHeight, false, "Non-Peminjaman"));
        
        currentX += stdWidth + roomGap;
        
        // Blok Abu-abu Tengah Kiri (Non-Peminjaman)
        ruanganList.add(new RoomData("", currentX, currentY, stdWidth, stdHeight, false, "Non-Peminjaman"));
        ruanganList.add(new RoomData("", currentX, currentY + stdHeight + roomGap, stdWidth, stdHeight, false, "Non-Peminjaman"));
        
        currentX += stdWidth + roomGap;
        
        // Lobby (Hitam)
        ruanganList.add(new RoomData("Lobby", currentX, currentY, stdWidth, (stdHeight * 2) + roomGap, false, "Lobby"));
        
        currentX += stdWidth + roomGap;
        
        // Blok Abu-abu Tengah Kanan (Non-Peminjaman) - 3 kotak vertikal
        ruanganList.add(new RoomData("", currentX, currentY, stdWidth / 2 - roomGap/2, stdHeight / 2, false, "Non-Peminjaman")); 
        ruanganList.add(new RoomData("", currentX, currentY + (stdHeight / 2) + roomGap, stdWidth / 2 - roomGap/2, stdHeight / 2, false, "Non-Peminjaman")); 
        ruanganList.add(new RoomData("", currentX, currentY + stdHeight + roomGap * 2, stdWidth / 2 - roomGap/2, stdHeight, false, "Non-Peminjaman"));
        
        int tempX = currentX + (stdWidth / 2) + roomGap/2;
        
        // Blok Abu-abu Kanan (Non-Peminjaman) - 2 kotak vertikal
        ruanganList.add(new RoomData("", tempX, currentY, stdWidth / 2 - roomGap/2, stdHeight + roomGap, false, "Non-Peminjaman")); 
        ruanganList.add(new RoomData("", tempX, currentY + stdHeight + roomGap * 2, stdWidth / 2 - roomGap/2, stdHeight, false, "Non-Peminjaman"));
        
        currentX = tempX + (stdWidth / 2) + roomGap;
        
        // --- Sisi Kanan Denah ---
        // R.B4.04 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.04", currentX, currentY, stdWidth, stdHeight, true, "Peminjaman"));
        
        // R.B4.06 (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.06", currentX, currentY + stdHeight + roomGap, stdWidth / 2 - roomGap/2, stdHeight, true, "Peminjaman"));
        
        // R.B4.12 (Hijau/Available) - Sesuai gambar baru
        ruanganList.add(new RoomData("R.B4.12", currentX + (stdWidth / 2) + roomGap/2, currentY + stdHeight + roomGap, stdWidth / 2 - roomGap/2, stdHeight, true, "Peminjaman"));
        
        currentX += stdWidth + roomGap;

        // R.B4.05 (Tegak) (Hijau/Available)
        ruanganList.add(new RoomData("R.B4.05", currentX, currentY, stdHeight, (stdHeight * 2) + roomGap, true, "Peminjaman"));
        
        currentX += stdHeight + roomGap * 2;
        
        // --- Area Lab TIK (Pojok Kanan) ---
        int labTikX = currentX;
        int labTikY = currentY;
        
        // Lab TIK 1 (Hijau/Available)
        ruanganList.add(new RoomData("Lab TIK 1", labTikX, labTikY, stdWidth, stdHeight, true, "Peminjaman"));
        // Lab TIK 2 (Hijau/Available) - Sesuai gambar baru
        ruanganList.add(new RoomData("Lab TIK 2", labTikX + stdWidth + roomGap, labTikY, stdWidth, stdHeight, true, "Peminjaman"));
        
        labTikY += stdHeight + roomGap;
        
        // Lab TIK 3 (Hijau/Available)
        ruanganList.add(new RoomData("Lab TIK 3", labTikX, labTikY, stdWidth, stdHeight, true, "Peminjaman"));
        // Lab TIK 4 (Hijau/Available) - Sesuai gambar baru
        ruanganList.add(new RoomData("Lab TIK 4", labTikX + stdWidth + roomGap, labTikY, stdWidth, stdHeight, true, "Peminjaman"));
        
        // Set ukuran panel
        setPreferredSize(new Dimension(panelWidth, panelHeight));