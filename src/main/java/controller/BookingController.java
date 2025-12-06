// controller/BookingController.java

package controller;

import model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;

public class BookingController {

    public static boolean isRoomAvailable(String roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Check bookings
        List<Booking> bookings = DatabaseManager.getBookingsByDateAndTime(date, startTime, endTime);

        for (Booking booking : bookings) {
            if (booking.getRoomId().equals(roomId) &&
                (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.PENDING)) {
                return false;
            }
        }

        // Check class schedules
        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
        List<ClassSchedule> schedules = DatabaseManager.getSchedulesByRoomAndDay(roomId, dayName);
        
        for (ClassSchedule schedule : schedules) {
            if (isTimeOverlapping(schedule, startTime, endTime)) {
                return false;
            }
        }

        return true;
    }

    public static RoomStatus getRoomStatus(String roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Check Schedules dulu
        String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
        List<ClassSchedule> schedules = DatabaseManager.getSchedulesByRoomAndDay(roomId, dayName);
        
        for (ClassSchedule schedule : schedules) {
            if (isTimeOverlapping(schedule, startTime, endTime)) {
                return RoomStatus.APPROVED;
            }
        }

        // Check Booking
        List<Booking> bookings = DatabaseManager.getBookingsByDateAndTime(date, startTime, endTime);

        boolean hasPending = false;
        boolean hasApproved = false;

        for (Booking booking : bookings) {
            if (booking.getRoomId().equals(roomId)) {
                if (booking.getStatus() == BookingStatus.APPROVED) {
                    hasApproved = true;
                } else if (booking.getStatus() == BookingStatus.PENDING) {
                    hasPending = true;
                }
            }
        }

        if (hasApproved) return RoomStatus.APPROVED;
        if (hasPending) return RoomStatus.PENDING;
        return RoomStatus.AVAILABLE;
    }

    // Check overlap
    private static boolean isTimeOverlapping(ClassSchedule schedule, LocalTime startTime, LocalTime endTime) {

        String timeStr = schedule.getTime().trim();
        String[] parts = timeStr.split("-");
        
        if (parts.length != 2) {
            return false;
        }
        
        try {
            LocalTime scheduleStart = LocalTime.parse(parts[0].trim());
            LocalTime scheduleEnd = LocalTime.parse(parts[1].trim());
            
            // Check overlap lagi
            return !(endTime.isBefore(scheduleStart) || endTime.equals(scheduleStart) || 
                     startTime.isAfter(scheduleEnd) || startTime.equals(scheduleEnd));
        } catch (Exception e) {
            System.err.println("Error parsing schedule time: " + timeStr);
            return false;
        }
    }

    public static Map<String, RoomStatus> getAllRoomStatuses(LocalDate date, LocalTime startTime, LocalTime endTime) {
        Map<String, RoomStatus> statuses = new HashMap<>();
        String[] roomIds = {
            "R_B4_11",
            "R_B4_10",
            "R_B4_08",
            "R_B4_04",
            "R_B4_06",
            "R_B4_12",
            "R_B4_05",
            "LAB_TIK_1",
            "LAB_TIK_2",
            "LAB_TIK_3",
            "LAB_TIK_4"
        };

        for (String roomId : roomIds) {
            statuses.put(roomId, getRoomStatus(roomId, date, startTime, endTime));
        }

        return statuses;
    }

    // validasi Booking
    public static boolean validateBooking(LocalDate date, LocalTime startTime, LocalTime endTime,
                                          String purpose, String borrower) {
        if (date == null || startTime == null || endTime == null) {
            return false;
        }

        if (date.isBefore(LocalDate.now())) {
            return false;
        }

        if (!endTime.isAfter(startTime)) {
            return false;
        }

        if (purpose == null || purpose.trim().isEmpty()) {
            return false;
        }

        if (borrower == null || borrower.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    // validasi submit mahasiswa
    public static int submitBooking(String roomId, LocalDate date, LocalTime startTime,
                                   LocalTime endTime, String purpose, String borrower) {

        if (!validateBooking(date, startTime, endTime, purpose, borrower)) {
            return -1;
        }

        if (!isRoomAvailable(roomId, date, startTime, endTime)) {
            return -2;
        }

        return DatabaseManager.createBooking(roomId, date, startTime, endTime, purpose, borrower);
    }

    // validasi submit admin
    public static int submitBookingAsAdmin(String roomId, LocalDate date, LocalTime startTime,
                                          LocalTime endTime, String purpose, String borrower) {

        if (date == null || startTime == null || endTime == null) {
            return -1;
        }

        if (!endTime.isAfter(startTime)) {
            return -1;
        }

        if (purpose == null || purpose.trim().isEmpty()) {
            return -1;
        }

        if (borrower == null || borrower.trim().isEmpty()) {
            return -1;
        }

        return DatabaseManager.createBookingAsApproved(roomId, date, startTime, endTime, purpose, borrower);
    }
}