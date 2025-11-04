package com.ait.inventory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ProductServlet", urlPatterns = {"/", "/products"})
public class ProductServlet extends HttpServlet {

    private ProductDAO dao;
    private CategoryDAO categoryDAO; // <- NEW

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String url = getServletContext().getInitParameter("JDBC_URL");
        String user = getServletContext().getInitParameter("JDBC_USER");
        String pass = getServletContext().getInitParameter("JDBC_PASS");
        DBUtil.configure(url, user, pass);

        dao = new ProductDAO();
        categoryDAO = new CategoryDAO(); // <- NEW
        try {
            dao.initIfNeeded();
            categoryDAO.initIfNeeded();   // <- NEW
        } catch (Exception e) {
            throw new ServletException("DB init failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            HttpSession session = req.getSession(false);
            boolean isAdmin = (session != null && "ADMIN".equals(session.getAttribute("role")));
            switch (action) {
                case "new":
                    if (!isAdmin) { resp.sendError(403); return; }
                    req.setAttribute("categories", categoryDAO.findAllNames()); // <- feed dropdown
                    req.getRequestDispatcher("/product-form.jsp").forward(req, resp);
                    break;
                case "edit":
                    if (!isAdmin) { resp.sendError(403); return; }
                    int id = Integer.parseInt(req.getParameter("id"));
                    Product p = dao.findById(id);
                    req.setAttribute("product", p);
                    req.setAttribute("categories", categoryDAO.findAllNames()); // <- feed dropdown
                    req.getRequestDispatcher("/product-form.jsp").forward(req, resp);
                    break;
                case "delete":
                    if (!isAdmin) { resp.sendError(403); return; }
                    int did = Integer.parseInt(req.getParameter("id"));
                    dao.delete(did);
                    resp.sendRedirect(req.getContextPath() + "/products");
                    break;
                default:
                    List<Product> list = dao.findAll();
                    req.setAttribute("products", list);
                    // (JSP will show Total = quantity * price; we’ll update JSP next)
                    req.getRequestDispatcher("/product-list.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String idStr    = req.getParameter("id");
        String name     = req.getParameter("name");
        String sku      = req.getParameter("sku");
        String category = req.getParameter("category");
        int quantity    = Integer.parseInt(req.getParameter("quantity"));
        double price    = Double.parseDouble(req.getParameter("price")); // unit price

        try {
            HttpSession session = req.getSession(false);
            boolean isAdmin = (session != null && "ADMIN".equals(session.getAttribute("role")));
            if (!isAdmin) { resp.sendError(403); return; }

            // ensure category table has this value
            categoryDAO.ensureExists(category); // <- NEW

            if (idStr == null || idStr.isBlank()) {
                dao.insert(new Product(name, sku, category, quantity, price));
            } else {
                int id = Integer.parseInt(idStr);
                dao.update(new Product(id, name, sku, category, quantity, price));
            }
            resp.sendRedirect(req.getContextPath() + "/products");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
