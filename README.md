PBO Team Project

# PanggonFATISDA - Classroom Booking System

**Panggon FATISDA** is a desktop-based application built with Java Swing designed to manage room bookings and visualize class schedules within the FATISDA (Fakultas Teknologi Informasi dan Sains Data) environment. This application streamlines room reservations for students and assists administrators in efficiently validating booking requests.

## Key Features

### For Students
* **Interactive Floor Plan**: View real-time room availability through a floor plan visualization.
* **Booking Requests**: Reserve rooms for student activities with a built-in schedule conflict validation system.
* **Weekly Class Schedule**: View a comprehensive list of recurring class schedules for each room.

### For Admins
* **Approval System (Approve/Reject)**: Manage and decide on pending room booking requests from students.
* **Account Management**: Create, edit, or delete student accounts.
* **Schedule Management**: Add or remove fixed class schedules within the database.
* **Real-time Room Monitoring**: Track the status of every room (Available, Pending, or Approved/Scheduled).

## Technologies Used
* **Java 17**: The primary programming language.
* **Maven**: For project management and dependency handling.
* **FlatLaf**: A modern UI theme for Swing to ensure a clean and elegant interface.
* **SQLite**: A serverless local database for storing user, room, and schedule data.
* **LGoodDatePicker**: User-friendly date and time picker components.

## How to Run (Development)

1. **Clone the Repository**:
   ```bash
   git clone [https://github.com/Adityamulyaf/PBO-Team-Project.git](https://github.com/Adityamulyaf/PBO-Team-Project.git)
2. **Build with Maven: Run this command to download dependencies and compile the project**:
   ```bash
   mvn clean package
3. **Execute the Application: Use the generated Fat JAR located in the target/ directory**:
   ```bash
   java -jar target/booking-kelas-fat.jar

## Database Configuration
The application uses an SQLite database named booking_fatisda.db, which is automatically created upon the first launch. Default accounts available:
- Admin: Username admin | Password admin123
- Student: Username mhs | Password mhs123
