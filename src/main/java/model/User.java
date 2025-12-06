// model/User.java
package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullname;
    private UserRole role;

    public User(int id, String username, String password, String fullname, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullname() { return fullname; }
    public UserRole getRole() { return role; }
}