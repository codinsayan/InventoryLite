package com.ait.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Works with your existing tables:
 *   users(id, username, salt, password_hash, role)
 *   password_resets(token, user_id, expires_at)
 * No change to schema or login/signup required.
 */
public class UserDAO {

    public void initIfNeeded() throws SQLException {
        // create users + password_resets if missing (kept from your version)
        String ddlUsers = """
            CREATE TABLE IF NOT EXISTS users (
              id INT PRIMARY KEY AUTO_INCREMENT,
              username VARCHAR(50) NOT NULL UNIQUE,
              salt VARCHAR(24) NOT NULL,
              password_hash VARCHAR(44) NOT NULL,
              role VARCHAR(20) NOT NULL DEFAULT 'USER'
            )
            """;
        String ddlResets = """
            CREATE TABLE IF NOT EXISTS password_resets (
              token VARCHAR(64) PRIMARY KEY,
              user_id INT NOT NULL,
              expires_at TIMESTAMP NOT NULL,
              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
            """;

        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement()) {
            st.execute(ddlUsers);
            st.execute(ddlResets);
        }
    }

    /* ----------------------- Create / Read / Validate ----------------------- */

    public void insert(String username, String rawPassword) throws SQLException {
        insert(username, rawPassword, "USER");
    }

    public void insert(String username, String rawPassword, String role) throws SQLException {
        String salt = PasswordUtil.randomSalt();
        String hash = PasswordUtil.hash(rawPassword, salt);
        String sql = "INSERT INTO users(username, salt, password_hash, role) VALUES(?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, salt);
            ps.setString(3, hash);
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("salt"),
                        rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    public boolean validate(String username, String rawPassword) throws SQLException {
        User u = findByUsername(username);
        if (u == null) return false;
        String calc = PasswordUtil.hash(rawPassword, u.getSalt());
        return calc.equals(u.getPasswordHash());
    }

    /* ---------------------------- Password reset ---------------------------- */

    public String createResetToken(String username, long ttlMinutes) throws SQLException {
        User u = findByUsername(username);
        if (u == null) return null;

        String token = UUID.randomUUID().toString().replace("-", "");
        String sql = "INSERT INTO password_resets(token, user_id, expires_at) " +
                     "VALUES(?, ?, DATE_ADD(NOW(), INTERVAL ? MINUTE))";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setInt(2, u.getId());
            ps.setLong(3, ttlMinutes);
            ps.executeUpdate();
        }
        return token;
    }

    public User findUserByResetToken(String token) throws SQLException {
        String sql = """
            SELECT u.* FROM password_resets r
            JOIN users u ON u.id = r.user_id
            WHERE r.token=? AND r.expires_at > NOW()
            """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("salt"),
                        rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    public void consumeResetToken(String token) throws SQLException {
        String sql = "DELETE FROM password_resets WHERE token=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.executeUpdate();
        }
    }

    public void updatePassword(int userId, String newRawPassword) throws SQLException {
        String newSalt = PasswordUtil.randomSalt();
        String newHash = PasswordUtil.hash(newRawPassword, newSalt);
        String sql = "UPDATE users SET salt=?, password_hash=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newSalt);
            ps.setString(2, newHash);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    /* --------------------------- Users tab helpers -------------------------- */

    /** For Users page: list all users. */
    public List<User> listAll() throws SQLException {
        String sql = "SELECT id, username, salt, password_hash, role FROM users ORDER BY id";
        List<User> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("salt"),
                    rs.getString("role")
                ));
            }
        }
        return out;
    }

    /** For Users page: update a user's role. */
    public void updateRole(int userId, String role) throws SQLException {
        String sql = "UPDATE users SET role=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /* ------------------- Optional user_role sync (if needed) -------------------
    If later you want to keep user_role table in sync, add:
    
    public void upsertUserRole(int userId, int roleId) throws SQLException {
        String del = "DELETE FROM user_role WHERE user_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement d = c.prepareStatement(del)) {
            d.setInt(1, userId);
            d.executeUpdate();
        }
        String ins = "INSERT INTO user_role(user_id, role_id) VALUES(?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(ins)) {
            ps.setInt(1, userId);
            ps.setInt(2, roleId);
            ps.executeUpdate();
        }
    }
    ----------------------------------------------------------------------------- */
}
