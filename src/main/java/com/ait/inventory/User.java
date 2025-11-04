package com.ait.inventory;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String salt;
    private String role;

    public User() {}

    public User(int id, String username, String passwordHash, String salt, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public String getRole() { return role; }

    // Setters (bug fixed: setSalt no longer assigns role)
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setSalt(String salt) { this.salt = salt; }
    public void setRole(String role) { this.role = role; }
}
