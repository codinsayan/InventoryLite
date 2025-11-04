package com.ait.inventory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private DataSource ds;

    /** Fallback DataSource via DriverManager (when JNDI isn’t configured) */
    private static class DriverManagerDataSource implements DataSource {
        private final String url, user, pass;
        DriverManagerDataSource(String url, String user, String pass) throws ClassNotFoundException {
            this.url = url; this.user = user; this.pass = pass;
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        @Override public Connection getConnection() throws SQLException { return DriverManager.getConnection(url, user, pass); }
        @Override public Connection getConnection(String u, String p) throws SQLException { return DriverManager.getConnection(url, u, p); }
        @Override public <T> T unwrap(Class<T> iface){ throw new UnsupportedOperationException(); }
        @Override public boolean isWrapperFor(Class<?> iface){ return false; }
        @Override public java.io.PrintWriter getLogWriter(){ throw new UnsupportedOperationException(); }
        @Override public void setLogWriter(java.io.PrintWriter out){ throw new UnsupportedOperationException(); }
        @Override public void setLoginTimeout(int seconds){ throw new UnsupportedOperationException(); }
        @Override public int getLoginTimeout(){ return 0; }
        @Override public java.util.logging.Logger getParentLogger(){ throw new UnsupportedOperationException(); }
    }

    @Override public void init() throws ServletException {
        // 1) JNDI
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/inventorylite");
            if (ds != null) return;
        } catch (Exception ignore) {}

        // 2) web.xml context-params
        var sc = getServletContext();
        String url  = sc.getInitParameter("jdbc.url");
        String user = sc.getInitParameter("jdbc.user");
        String pass = sc.getInitParameter("jdbc.pass");
        if (url != null && user != null) {
            try { ds = new DriverManagerDataSource(url, user, pass); return; }
            catch (ClassNotFoundException e) { throw new ServletException("JDBC driver not found", e); }
        }
        throw new ServletException("No DataSource (JNDI jdbc/inventorylite or jdbc.* params).");
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Map<String,Object>> products = new ArrayList<>();

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, name, quantity, price FROM products ORDER BY name ASC");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String,Object> row = new HashMap<>();
                row.put("id",       rs.getLong("id"));
                row.put("name",     rs.getString("name"));
                row.put("quantity", rs.getInt("quantity"));
                row.put("price",    rs.getDouble("price"));
                products.add(row);
            }
        } catch (SQLException e) {
            throw new ServletException("Failed to load products for orders", e);
        }

        req.setAttribute("products", products);
        req.getRequestDispatcher("/orders.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String customer = Optional.ofNullable(req.getParameter("customer")).orElse("").trim();
        String[] pIds = req.getParameterValues("productId[]");
        String[] qtys = req.getParameterValues("qty[]");

        if (pIds == null || qtys == null || pIds.length == 0) {
            req.setAttribute("error", "Add at least one item.");
            doGet(req, resp);
            return;
        }

        List<OrderDao.Item> items = new ArrayList<>();
        for (int i = 0; i < pIds.length; i++) {
            try {
                long pid = Long.parseLong(pIds[i]);
                int q   = Integer.parseInt(qtys[i]);
                if (q > 0) items.add(new OrderDao.Item(pid, q));
            } catch (Exception ignore) {}
        }
        if (items.isEmpty()) {
            req.setAttribute("error", "Invalid items.");
            doGet(req, resp);
            return;
        }

        try {
            long orderId = new OrderDao(ds).createOrder(customer, items);
            resp.sendRedirect(req.getContextPath() + "/orders?success=1&orderId=" + orderId);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        }
    }
}
