package com.ait.inventory;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SalesServlet extends HttpServlet {

    /** JNDI -> cached context attr "DS" -> build from web.xml jdbc.* (then cache). */
    private DataSource lookupDS() {
        Object cached = getServletContext().getAttribute("DS");
        if (cached instanceof DataSource) return (DataSource) cached;

        try {
            javax.naming.InitialContext ic = new javax.naming.InitialContext();
            Object o = ic.lookup("java:comp/env/jdbc/inventorylite");
            if (o instanceof DataSource) {
                DataSource ds = (DataSource) o;
                getServletContext().setAttribute("DS", ds);
                return ds;
            }
        } catch (javax.naming.NamingException ignore) {}

        String url  = getServletContext().getInitParameter("jdbc.url");
        String user = getServletContext().getInitParameter("jdbc.user");
        String pass = getServletContext().getInitParameter("jdbc.pass");
        if (url != null && user != null) {
            DataSource ds = new SimpleDriverManagerDS(url, user, pass);
            getServletContext().setAttribute("DS", ds);
            return ds;
        }
        return null;
    }

    /** Minimal DataSource backed by DriverManager (no extra deps). */
    static final class SimpleDriverManagerDS implements DataSource {
        private final String url, user, pass;
        SimpleDriverManagerDS(String url, String user, String pass) {
            this.url = url; this.user = user; this.pass = pass;
            try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException ignored) {}
        }
        public java.sql.Connection getConnection() throws java.sql.SQLException {
            return java.sql.DriverManager.getConnection(url, user, pass);
        }
        public java.sql.Connection getConnection(String u, String p) throws java.sql.SQLException {
            return java.sql.DriverManager.getConnection(url, u, p);
        }
        public java.io.PrintWriter getLogWriter(){ return null; }
        public void setLogWriter(java.io.PrintWriter out){}
        public void setLoginTimeout(int seconds){}
        public int getLoginTimeout(){ return 0; }
        public java.util.logging.Logger getParentLogger(){ return java.util.logging.Logger.getGlobal(); }
        public <T> T unwrap(Class<T> iface){ throw new UnsupportedOperationException(); }
        public boolean isWrapperFor(Class<?> iface){ return false; }
    }

    private Date parseDate(String s, boolean endOfDay) {
        if (s == null || s.isBlank()) return null;
        try {
            if (s.length() == 10) { // yyyy-MM-dd
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                df.setLenient(false);
                Date d = df.parse(s);
                return endOfDay ? new Date(d.getTime() + 24L*3600L*1000L) : d;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        DataSource ds = lookupDS();
        if (ds == null) {
            resp.sendError(500, "DataSource not available (JNDI jdbc/inventorylite or context attr 'DS' or jdbc.* params).");
            return;
        }

        SalesDao dao = new SalesDao(ds);

        String view = Optional.ofNullable(req.getParameter("view")).orElse("orders"); // "orders" or "items"
        String q = Optional.ofNullable(req.getParameter("q")).orElse("").trim();

        int page = 1, size = 20;
        try { page = Math.max(1, Integer.parseInt(Optional.ofNullable(req.getParameter("page")).orElse("1"))); } catch (Exception ignore) {}
        try { size = Math.max(1, Math.min(200, Integer.parseInt(Optional.ofNullable(req.getParameter("size")).orElse("20")))); } catch (Exception ignore) {}
        int offset = (page - 1) * size;

        Date from = parseDate(req.getParameter("from"), false);
        Date to   = parseDate(req.getParameter("to"), true);

        try {
            if ("items".equalsIgnoreCase(view)) {
                // Sold Items tab: real rows from order_items + product
                List<SalesDao.SoldItemRow> rows = dao.listSoldItems(from, to, q, offset, size);
                int total = dao.countSoldItems(from, to, q);
                req.setAttribute("sold", rows);
                req.setAttribute("soldCount", total);
            } else {
                // Orders tab: order summaries + preview of items
                List<SalesDao.OrderSummary> rows = dao.listOrders(from, to, q, offset, size);
                int total = dao.countOrders(from, to, q);
                req.setAttribute("orders", rows);
                req.setAttribute("ordersCount", total);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        req.setAttribute("view", view);
        req.setAttribute("q", q);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.setAttribute("from", req.getParameter("from"));
        req.setAttribute("to", req.getParameter("to"));

        req.getRequestDispatcher("/sales.jsp").forward(req, resp);
    }
}
