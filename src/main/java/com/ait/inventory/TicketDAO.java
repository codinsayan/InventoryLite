
package com.ait.inventory;

import java.sql.*;
import java.util.*;

public class TicketDAO {
    public long create(Ticket t) throws SQLException {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO ticket(customer_id, device_desc, diagnosis, labor_minutes, parts_used_json, status) VALUES(?,?,?,?,?,?)",
                 Statement.RETURN_GENERATED_KEYS)) {
            if (t.getCustomerId() == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, t.getCustomerId());
            ps.setString(2, t.getDeviceDesc());
            ps.setString(3, t.getDiagnosis());
            ps.setInt(4, t.getLaborMinutes());
            ps.setString(5, t.getPartsUsedJson());
            ps.setString(6, t.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return -1;
    }

    public List<Ticket> list(String status) throws SQLException {
        String sql = "SELECT * FROM ticket";
        if (status != null) sql += " WHERE status=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (status != null) ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                List<Ticket> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    private Ticket map(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setId(rs.getLong("id"));
        long cust = rs.getLong("customer_id"); t.setCustomerId(rs.wasNull()?null:cust);
        t.setDeviceDesc(rs.getString("device_desc"));
        t.setDiagnosis(rs.getString("diagnosis"));
        t.setLaborMinutes(rs.getInt("labor_minutes"));
        t.setPartsUsedJson(rs.getString("parts_used_json"));
        t.setStatus(rs.getString("status"));
        return t;
    }
}
