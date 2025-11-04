package com.ait.inventory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name="PasswordResetServlet", urlPatterns={"/password-reset"})
public class PasswordResetServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        try { userDAO.initIfNeeded(); } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        req.setAttribute("token", token);
        req.getRequestDispatcher("/password-reset.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        String p1 = req.getParameter("password");
        String p2 = req.getParameter("confirm");
        if (p1 == null || !p1.equals(p2) || p1.length() < 4) {
            req.setAttribute("error", "Password must be at least 4 chars and match confirmation.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/password-reset.jsp").forward(req, resp);
            return;
        }
        try {
            User u = userDAO.findUserByResetToken(token);
            if (u == null) {
                req.setAttribute("error", "Invalid or expired token.");
                req.setAttribute("token", token);
                req.getRequestDispatcher("/password-reset.jsp").forward(req, resp);
                return;
            }
            userDAO.updatePassword(u.getId(), p1);
            userDAO.consumeResetToken(token);
            req.setAttribute("success", "Password updated. Please login.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
