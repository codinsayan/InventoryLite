package com.ait.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class SalesDao {
    private final DataSource ds;
    public SalesDao(DataSource ds){ this.ds = ds; }

    // ---------- DTOs ----------
    public static final class OrderSummary {
        public final long id;
        public final String customerName;
        public final Timestamp createdAt;
        public final double total;
        public final String itemsPreview;
        public OrderSummary(long id, String customerName, Timestamp createdAt, double total, String itemsPreview){
            this.id=id; this.customerName=customerName; this.createdAt=createdAt; this.total=total; this.itemsPreview=itemsPreview;
        }
        public long getId() { return id; }
        public String getCustomerName() { return customerName; }
        public Timestamp getCreatedAt() { return createdAt; }
        public double getTotal() { return total; }
        public String getItemsPreview() { return itemsPreview; }
    }

    public static final class SoldItemRow {
        public final long orderId; public final Timestamp createdAt; public final String customerName;
        public final long productId; public final String productName; public final int qty; public final double price;
        public SoldItemRow(long orderId, Timestamp createdAt, String customerName, long productId, String productName, int qty, double price){
            this.orderId=orderId; this.createdAt=createdAt; this.customerName=customerName;
            this.productId=productId; this.productName=productName; this.qty=qty; this.price=price;
        }
        public double lineTotal(){ return qty * price; }
        public long getOrderId() { return orderId; }
        public Timestamp getCreatedAt() { return createdAt; }
        public String getCustomerName() { return customerName; }
        public long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQty() { return qty; }
        public double getPrice() { return price; }
        public double getLineTotal() { return lineTotal(); }
    }

    public static final class OrderItem {
        public final long productId; public final String productName; public final int qty; public final double price;
        public OrderItem(long productId, String productName, int qty, double price){
            this.productId=productId; this.productName=productName; this.qty=qty; this.price=price;
        }
        public double lineTotal(){ return qty * price; }
        public long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQty() { return qty; }
        public double getPrice() { return price; }
        public double getLineTotal() { return lineTotal(); }
    }

    public static final class OrderDetail {
        public final long id; public final String customerName; public final Timestamp createdAt; public final double total;
        public final List<OrderItem> items;
        public OrderDetail(long id, String customerName, Timestamp createdAt, double total, List<OrderItem> items){
            this.id=id; this.customerName=customerName; this.createdAt=createdAt; this.total=total; this.items=items;
        }
        public long getId() { return id; }
        public String getCustomerName() { return customerName; }
        public Timestamp getCreatedAt() { return createdAt; }
        public double getTotal() { return total; }
        public List<OrderItem> getItems() { return items; }
    }

    // ---------- Orders list (items preview uses snapshot name) ----------
    public List<OrderSummary> listOrders(java.util.Date from, java.util.Date to, String q, int offset, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT o.id,o.customer_name,o.total,o.created_at, " +
            " (SELECT GROUP_CONCAT(CONCAT(COALESCE(oi.product_name, p.name),' × ',oi.qty) " +
            "         ORDER BY oi.id SEPARATOR ', ') " +
            "    FROM order_items oi " +
            "    LEFT JOIN products p ON p.id=oi.product_id " +   // << use products + LEFT JOIN
            "   WHERE oi.order_id=o.id) AS items_preview " +
            "FROM orders o WHERE 1=1");
        List<Object> ps = new ArrayList<>();
        if (from!=null){ sql.append(" AND o.created_at>=?"); ps.add(new Timestamp(from.getTime())); }
        if (to!=null)  { sql.append(" AND o.created_at<?");  ps.add(new Timestamp(to.getTime())); }
        if (q!=null && !q.isBlank()){
            String like = "%"+q+"%";
            sql.append(" AND (o.customer_name LIKE ? OR CAST(o.id AS CHAR) LIKE ? OR EXISTS(" +
                       "SELECT 1 FROM order_items oi " +
                       "LEFT JOIN products p ON p.id=oi.product_id " +
                       "WHERE oi.order_id=o.id AND COALESCE(oi.product_name,p.name) LIKE ?))");
            ps.add(like); ps.add(like); ps.add(like);
        }
        sql.append(" ORDER BY o.created_at DESC, o.id DESC LIMIT ? OFFSET ?");
        ps.add(limit); ps.add(offset);

        try (Connection c = ds.getConnection();
             PreparedStatement st = prepare(c, sql.toString(), ps);
             ResultSet rs = st.executeQuery()){
            List<OrderSummary> out = new ArrayList<>();
            while (rs.next()){
                out.add(new OrderSummary(
                    rs.getLong("id"),
                    rs.getString("customer_name"),
                    rs.getTimestamp("created_at"),
                    rs.getDouble("total"),
                    rs.getString("items_preview")
                ));
            }
            return out;
        }
    }

    public int countOrders(java.util.Date from, java.util.Date to, String q) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM orders o WHERE 1=1");
        List<Object> ps = new ArrayList<>();
        if (from!=null){ sql.append(" AND o.created_at>=?"); ps.add(new Timestamp(from.getTime())); }
        if (to!=null)  { sql.append(" AND o.created_at<?");  ps.add(new Timestamp(to.getTime())); }
        if (q!=null && !q.isBlank()){
            String like = "%"+q+"%";
            sql.append(" AND (o.customer_name LIKE ? OR CAST(o.id AS CHAR) LIKE ? OR EXISTS(" +
                       "SELECT 1 FROM order_items oi " +
                       "LEFT JOIN products p ON p.id=oi.product_id " +
                       "WHERE oi.order_id=o.id AND COALESCE(oi.product_name,p.name) LIKE ?))");
            ps.add(like); ps.add(like); ps.add(like);
        }
        try (Connection c = ds.getConnection();
             PreparedStatement st = prepare(c, sql.toString(), ps);
             ResultSet rs = st.executeQuery()){
            rs.next(); return rs.getInt(1);
        }
    }

    // ---------- Sold items ----------
    public List<SoldItemRow> listSoldItems(java.util.Date from, java.util.Date to, String q, int offset, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT oi.order_id,o.created_at,o.customer_name,oi.product_id, " +
            "       COALESCE(oi.product_name, p.name) AS product_name, oi.qty, oi.price " +
            "FROM order_items oi " +
            "JOIN orders o  ON o.id=oi.order_id " +
            "LEFT JOIN products p ON p.id=oi.product_id WHERE 1=1");   // << LEFT JOIN products
        List<Object> ps = new ArrayList<>();
        if (from!=null){ sql.append(" AND o.created_at>=?"); ps.add(new Timestamp(from.getTime())); }
        if (to!=null)  { sql.append(" AND o.created_at<?");  ps.add(new Timestamp(to.getTime())); }
        if (q!=null && !q.isBlank()){
            String like = "%"+q+"%";
            sql.append(" AND (o.customer_name LIKE ? OR COALESCE(oi.product_name,p.name) LIKE ? OR CAST(oi.order_id AS CHAR) LIKE ?)");
            ps.add(like); ps.add(like); ps.add(like);
        }
        sql.append(" ORDER BY o.created_at DESC, oi.order_id DESC, oi.id ASC LIMIT ? OFFSET ?");
        ps.add(limit); ps.add(offset);

        try (Connection c = ds.getConnection();
             PreparedStatement st = prepare(c, sql.toString(), ps);
             ResultSet rs = st.executeQuery()){
            List<SoldItemRow> out = new ArrayList<>();
            while (rs.next()){
                out.add(new SoldItemRow(
                    rs.getLong("order_id"),
                    rs.getTimestamp("created_at"),
                    rs.getString("customer_name"),
                    rs.getLong("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("qty"),
                    rs.getDouble("price")
                ));
            }
            return out;
        }
    }

    public int countSoldItems(java.util.Date from, java.util.Date to, String q) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) " +
            "FROM order_items oi JOIN orders o ON o.id=oi.order_id " +
            "LEFT JOIN products p ON p.id=oi.product_id WHERE 1=1");   // << LEFT JOIN products
        List<Object> ps = new ArrayList<>();
        if (from!=null){ sql.append(" AND o.created_at>=?"); ps.add(new Timestamp(from.getTime())); }
        if (to!=null)  { sql.append(" AND o.created_at<?");  ps.add(new Timestamp(to.getTime())); }
        if (q!=null && !q.isBlank()){
            String like = "%"+q+"%";
            sql.append(" AND (o.customer_name LIKE ? OR COALESCE(oi.product_name,p.name) LIKE ? OR CAST(oi.order_id AS CHAR) LIKE ?)");
            ps.add(like); ps.add(like); ps.add(like);
        }
        try (Connection c = ds.getConnection();
             PreparedStatement st = prepare(c, sql.toString(), ps);
             ResultSet rs = st.executeQuery()){
            rs.next(); return rs.getInt(1);
        }
    }

    // ---------- Order detail for invoice ----------
    public OrderDetail getOrder(long id) throws SQLException {
        String sqlO = "SELECT id, customer_name, total, created_at FROM orders WHERE id=?";
        String sqlI = "SELECT oi.product_id, COALESCE(oi.product_name, p.name) AS product_name, oi.qty, oi.price " +
                      "FROM order_items oi LEFT JOIN products p ON p.id=oi.product_id " +  // << LEFT JOIN products
                      "WHERE oi.order_id=? ORDER BY oi.id";
        try (Connection c = ds.getConnection()) {
            String cust=null; Timestamp created=null; double total=0;
            try (PreparedStatement ps = c.prepareStatement(sqlO)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    cust = rs.getString("customer_name");
                    created = rs.getTimestamp("created_at");
                    total = rs.getDouble("total");
                }
            }
            List<OrderItem> items = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(sqlI)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(new OrderItem(
                            rs.getLong("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("qty"),
                            rs.getDouble("price")
                        ));
                    }
                }
            }
            return new OrderDetail(id, cust, created, total, items);
        }
    }

    // ---------- helper ----------
    private static PreparedStatement prepare(Connection c, String sql, List<Object> params) throws SQLException {
        PreparedStatement ps = c.prepareStatement(sql);
        int i = 1;
        for (Object p : params) {
            if (p instanceof java.util.Date && !(p instanceof java.sql.Date) && !(p instanceof java.sql.Timestamp)) {
                ps.setTimestamp(i++, new java.sql.Timestamp(((java.util.Date) p).getTime()));
            } else {
                ps.setObject(i++, p);
            }
        }
        return ps;
    }
}
