// model/ClassSchedule.java
package model;

public class ClassSchedule {
    private int id;
    private String day;
    private String session;
    private String time;
    private String courseName;
    private String courseCode;
    private String semester;
    private String sks;
    private String lecturer;
    private String roomId;
    private String className;

    public ClassSchedule(int id, String day, String session, String time, 
                        String courseName, String courseCode, String semester,
                        String sks, String lecturer, String roomId, String className) {
        this.id = id;
        this.day = day;
        this.session = session;
        this.time = time;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.semester = semester;
        this.sks = sks;
        this.lecturer = lecturer;
        this.roomId = roomId;
        this.className = className;
    }

    // Getters
    public int getId() { return id; }
    public String getDay() { return day; }
    public String getSession() { return session; }
    public String getTime() { return time; }
    public String getCourseName() { return courseName; }
    public String getCourseCode() { return courseCode; }
    public String getSemester() { return semester; }
    public String getSks() { return sks; }
    public String getLecturer() { return lecturer; }
    public String getRoomId() { return roomId; }
    public String getClassName() { return className; }

    // Setters
    public void setDay(String day) { this.day = day; }
    public void setSession(String session) { this.session = session; }
    public void setTime(String time) { this.time = time; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setSks(String sks) { this.sks = sks; }
    public void setLecturer(String lecturer) { this.lecturer = lecturer; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setClassName(String className) { this.className = className; }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s) | %s | Kelas %s | %s",
            day, session, time, courseName, className, roomId);
    }
}