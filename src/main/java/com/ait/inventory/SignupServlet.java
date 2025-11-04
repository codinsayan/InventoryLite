package com.ait.inventory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "SignupServlet", urlPatterns = {"/signup"})
public class SignupServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        try {
            userDAO.initIfNeeded();
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirm  = req.getParameter("confirm");

        // Basic validation
        if (username == null || username.isBlank() ||
            password == null || password.length() < 4 ||
            confirm  == null || !password.equals(confirm)) {
            req.setAttribute("error",
                    "Username required; password must be ≥ 4 chars and match confirmation.");
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
            return;
        }

        try {
            // Block duplicate username
            if (userDAO.findByUsername(username) != null) {
                req.setAttribute("error", "Username already exists.");
                req.getRequestDispatcher("/signup.jsp").forward(req, resp);
                return;
            }

            // Decide role: first ever user -> ADMIN, else USER
            boolean firstUserIsAdmin = isFirstUser();
            if (firstUserIsAdmin) {
                userDAO.insert(username, password, "ADMIN");
            } else {
                userDAO.insert(username, password, "USER");
            }

            req.setAttribute("success", "Account created. Please login.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    /** Checks if there are zero users in the DB. */
    private boolean isFirstUser() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return false;
    }
}
