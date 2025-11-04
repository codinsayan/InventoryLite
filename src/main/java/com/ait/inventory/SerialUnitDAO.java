
package com.ait.inventory;

import java.sql.*;
import java.util.*;

public class SerialUnitDAO {

    public void create(SerialUnit su) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO serial_unit(product_id, serial_or_imei, status, warranty_start, warranty_end, notes) VALUES(?,?,?,?,?,?)",
                 Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, su.getProductId());
            ps.setString(2, su.getSerialOrImei());
            ps.setString(3, su.getStatus());
            ps.setDate(4, su.getWarrantyStart());
            ps.setDate(5, su.getWarrantyEnd());
            ps.setString(6, su.getNotes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) su.setId(rs.getLong(1));
            }
        }
    }

    public Optional<SerialUnit> findBySerial(String serial) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT * FROM serial_unit WHERE serial_or_imei=?")) {
            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    public List<SerialUnit> listByProduct(long productId, String status) throws SQLException {
        String sql = "SELECT * FROM serial_unit WHERE product_id=?";
        if (status != null) sql += " AND status=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, productId);
            if (status != null) ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                List<SerialUnit> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }

    public void updateStatus(long id, String status) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE serial_unit SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    private SerialUnit map(ResultSet rs) throws SQLException {
        SerialUnit su = new SerialUnit();
        su.setId(rs.getLong("id"));
        su.setProductId(rs.getLong("product_id"));
        su.setSerialOrImei(rs.getString("serial_or_imei"));
        su.setStatus(rs.getString("status"));
        su.setWarrantyStart(rs.getDate("warranty_start"));
        su.setWarrantyEnd(rs.getDate("warranty_end"));
        su.setNotes(rs.getString("notes"));
        return su;
    }
}
