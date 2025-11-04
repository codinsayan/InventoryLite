package com.ait.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public void initIfNeeded() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS categories (
              id   INT PRIMARY KEY AUTO_INCREMENT,
              name VARCHAR(100) NOT NULL UNIQUE
            )
            """;
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    /** Insert category if missing (ignores blanks) */
    public void ensureExists(String name) throws SQLException {
        if (name == null) return;
        String trimmed = name.trim();
        if (trimmed.isEmpty()) return;
        String sql = "INSERT IGNORE INTO categories(name) VALUES(?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trimmed);
            ps.executeUpdate();
        }
    }

    /** For dropdowns */
    public List<String> findAllNames() throws SQLException {
        List<String> out = new ArrayList<>();
        String sql = "SELECT name FROM categories ORDER BY name ASC";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(rs.getString(1));
        }
        return out;
    }
}
