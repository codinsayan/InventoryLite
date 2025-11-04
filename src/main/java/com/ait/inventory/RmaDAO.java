
package com.ait.inventory;

import java.sql.*;
import java.util.*;

public class RmaDAO {

    public long create(Rma r) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO rma(customer_id, serial_id, issue, status) VALUES(?,?,?,?)",
                 Statement.RETURN_GENERATED_KEYS)) {
            if (r.getCustomerId() == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, r.getCustomerId());
            ps.setLong(2, r.getSerialId());
            ps.setString(3, r.getIssue());
            ps.setString(4, r.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return -1;
    }

    public List<Rma> list(String status) throws SQLException {
        String sql = "SELECT * FROM rma";
        if (status != null) sql += " WHERE status=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (status != null) ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                List<Rma> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    public void updateStatus(long id, String status, String resolution) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "UPDATE rma SET status=?, resolution=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setString(2, resolution);
            ps.setLong(3, id);
            ps.executeUpdate();
        }
    }

    private Rma map(ResultSet rs) throws SQLException {
        Rma r = new Rma();
        r.setId(rs.getLong("id"));
        long cust = rs.getLong("customer_id"); r.setCustomerId(rs.wasNull()?null:cust);
        r.setSerialId(rs.getLong("serial_id"));
        r.setIssue(rs.getString("issue"));
        r.setStatus(rs.getString("status"));
        r.setIntakeDate(rs.getTimestamp("intake_date"));
        r.setResolution(rs.getString("resolution"));
        return r;
    }
}
