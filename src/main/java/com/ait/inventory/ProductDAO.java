package com.ait.inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public void initIfNeeded() throws SQLException {
        // Ensures table exists (simple for classroom use)
        String sql = """
            CREATE TABLE IF NOT EXISTS products (
              id INT PRIMARY KEY AUTO_INCREMENT,
              name VARCHAR(100) NOT NULL,
              sku VARCHAR(50) NOT NULL UNIQUE,
              category VARCHAR(50),
              quantity INT NOT NULL DEFAULT 0,
              price DECIMAL(10,2) NOT NULL DEFAULT 0.00
            )
            """;
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public List<Product> findAll() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id DESC";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("sku"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"));
                list.add(p);
            }
        }
        return list;
    }

    public Product findById(int id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("sku"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"));
                }
                return null;
            }
        }
    }

    public void insert(Product p) throws SQLException {
        String sql = "INSERT INTO products(name, sku, category, quantity, price) VALUES(?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getSku());
            ps.setString(3, p.getCategory());
            ps.setInt(4, p.getQuantity());
            ps.setDouble(5, p.getPrice());
            ps.executeUpdate();
        }
    }

    public void update(Product p) throws SQLException {
        String sql = "UPDATE products SET name=?, sku=?, category=?, quantity=?, price=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getSku());
            ps.setString(3, p.getCategory());
            ps.setInt(4, p.getQuantity());
            ps.setDouble(5, p.getPrice());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
