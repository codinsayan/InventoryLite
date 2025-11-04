package com.ait.inventory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name="PasswordResetRequestServlet", urlPatterns={"/password-reset-request"})
public class PasswordResetRequestServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        try { userDAO.initIfNeeded(); } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/password-reset-request.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        try {
            String token = userDAO.createResetToken(username, 30);
            // For this demo, we just show the link rather than email it.
            if (token != null) {
                req.setAttribute("info", "Reset link (valid 30 min): " + req.getContextPath() + "/password-reset?token=" + token);
            } else {
                req.setAttribute("info", "If the user exists, a reset link has been generated.");
            }
            req.getRequestDispatcher("/password-reset-request.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
