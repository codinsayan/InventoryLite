package com.ait.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

public class OrderDao {
    private final DataSource ds;

    public static class Item {
        public long productId;
        public int qty;
        public Item(long productId, int qty){ this.productId = productId; this.qty = qty; }
    }

    public OrderDao(DataSource ds){ this.ds = ds; }

    public long createOrder(String customerName, List<Item> items) throws SQLException {
        try (Connection con = ds.getConnection()) {
            con.setAutoCommit(false);
            try {
                long orderId;

                // 1) Create the order shell
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO orders(customer_name,total) VALUES(?,0)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, (customerName == null || customerName.isBlank()) ? null : customerName.trim());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { rs.next(); orderId = rs.getLong(1); }
                }

                double grandTotal = 0.0;

                // 2) For each item: lock row in products, check stock, deduct, add order_item (snapshot name)
                try (PreparedStatement lock = con.prepareStatement(
                         "SELECT name, price, quantity FROM products WHERE id=? FOR UPDATE");
                     PreparedStatement upd  = con.prepareStatement(
                         "UPDATE products SET quantity = quantity - ? WHERE id=?");
                     PreparedStatement add  = con.prepareStatement(
                         "INSERT INTO order_items(order_id, product_id, product_name, qty, price) VALUES(?,?,?,?,?)")) {

                    for (Item it : items) {
                        if (it.qty <= 0) throw new SQLException("Invalid qty for product " + it.productId);

                        lock.setLong(1, it.productId);
                        String productName; double price; int stock;
                        try (ResultSet rs = lock.executeQuery()) {
                            if (!rs.next()) throw new SQLException("Product not found: " + it.productId);
                            productName = rs.getString("name");
                            price       = rs.getDouble("price");
                            stock       = rs.getInt("quantity");
                        }
                        if (stock < it.qty) throw new SQLException("Insufficient stock for product " + it.productId);

                        // deduct stock
                        upd.setInt(1, it.qty);
                        upd.setLong(2, it.productId);
                        upd.executeUpdate();

                        // insert snapshot line
                        add.setLong(1, orderId);
                        add.setLong(2, it.productId);
                        add.setString(3, productName); // <- snapshot name into order_items.product_name
                        add.setInt(4, it.qty);
                        add.setDouble(5, price);
                        add.executeUpdate();

                        grandTotal += price * it.qty;
                    }
                }

                // 3) finalize total
                try (PreparedStatement ps = con.prepareStatement("UPDATE orders SET total=? WHERE id=?")) {
                    ps.setDouble(1, grandTotal);
                    ps.setLong(2, orderId);
                    ps.executeUpdate();
                }

                con.commit();
                return orderId;
            } catch (Exception e) {
                con.rollback();
                throw (e instanceof SQLException) ? (SQLException) e : new SQLException(e);
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
