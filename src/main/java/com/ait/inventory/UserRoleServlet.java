package com.ait.inventory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Serves the Users admin page and updates roles.
 * URL: /users
 * View: /WEB-INF/views/users.jsp  (same JSP you already have / or the one we drafted)
 */
@WebServlet(name = "UserRoleServlet", urlPatterns = {"/users"})
public class UserRoleServlet extends HttpServlet {

    private UserDAO userDAO;

    // Allowed roles you want to expose in the UI
    private static final Set<String> ALLOWED_ROLES =
            Set.of("ADMIN", "MANAGER", "CASHIER", "USER", "VIEWER");

    @Override
    public void init() {
        this.userDAO = new UserDAO();
        try { userDAO.initIfNeeded(); } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // (Optional) gate by role: only ADMIN/MANAGER can view
        if (!isAdminOrManager(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            req.setAttribute("users", userDAO.listAll());
            req.getRequestDispatcher("/users.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Failed to load users", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAdminOrManager(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = req.getParameter("action");
        if (!"updateRole".equals(action)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            return;
        }

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String role = req.getParameter("role");
            if (!ALLOWED_ROLES.contains(role)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid role");
                return;
            }

            userDAO.updateRole(id, role);

            // If you later decide to sync user_role, call userDAO.upsertUserRole(id, map(role))
            resp.sendRedirect(req.getContextPath() + "/users?ok=1");
        } catch (Exception e) {
            throw new ServletException("Failed to update role", e);
        }
    }

    private boolean isAdminOrManager(HttpServletRequest req) {
        Object role = req.getSession() != null ? req.getSession().getAttribute("role") : null;
        if (role == null) return false;
        String r = role.toString();
        return "ADMIN".equals(r) || "MANAGER".equals(r);
    }
}

