// model/Booking.java

package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {
    private int id;
    private String roomId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String purpose;
    private String borrower;
    private BookingStatus status;

    public Booking(int id, String roomId, LocalDate date, LocalTime startTime, 
                   LocalTime endTime, String purpose, String borrower, BookingStatus status) {
        this.id = id;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.borrower = borrower;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getRoomId() { return roomId; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getPurpose() { return purpose; }
    public String getBorrower() { return borrower; }
    public BookingStatus getStatus() { return status; }

    // Setters
    public void setStatus(BookingStatus status) { this.status = status; }

    /**
     * Nge-check pengajuannya tabrakan tabrakan sama slot waktu lain atau ngga
     * Rentang waktu tabrakan kalo:
     * - Rentang A: [mulaiA, akhirA)
     * - Rentang B: [mulaiB, akhirB)
     * - Tabrakan jika: mulaiA < akhirB DAN mulaiB < akhirA
     * 
     * Contoh:
     * - [07:00, 08:00) vs [08:00, 09:00) = ngga tabrakan
     * - [07:00, 08:00) vs [07:30, 08:30) = tabrakan
     * - [07:00, 09:00) vs [08:00, 09:00) = tabrakan
     */
    public boolean overlaps(LocalDate otherDate, LocalTime otherStart, LocalTime otherEnd) {
        // Kalo beda tanggal otomatis ngga overlap
        if (!this.date.equals(otherDate)) {
            return false;
        }

        boolean noOverlap = otherEnd.isBefore(this.startTime) || 
                           otherEnd.equals(this.startTime) || 
                           otherStart.isAfter(this.endTime) || 
                           otherStart.equals(this.endTime);
        
        boolean hasOverlap = !noOverlap;
        
        System.out.println("Overlap check: [" + this.startTime + "-" + this.endTime + 
                         "] vs [" + otherStart + "-" + otherEnd + "] = " + 
                         (hasOverlap ? "OVERLAP" : "NO OVERLAP"));
        
        return hasOverlap;
    }
}