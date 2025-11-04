package com.ait.inventory;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthFilter implements Filter {

    // Extensions that must always pass through (served by DefaultServlet)
    private static final Set<String> PUBLIC_EXT = Set.of(
            ".css", ".js", ".map",
            ".png", ".jpg", ".jpeg", ".gif", ".svg", ".ico",
            ".woff", ".woff2", ".ttf", ".eot"
    );

    private boolean isPublic(HttpServletRequest req) {
        String ctx = req.getContextPath();                          // e.g. /inventorylite
        String uri = req.getRequestURI();                            // e.g. /inventorylite/assets/css/app.css
        String path = uri.substring(ctx.length());                   // e.g. /assets/css/app.css

        // 1) Explicit public endpoints (no session required)
        if (path.equals("/") ||
            path.equals("/login") || path.equals("/signup") ||
            path.equals("/password-reset-request") || path.equals("/password-reset")) {
            return true;
        }

        // 2) Public utility endpoints (optional)
        if (path.startsWith("/barcode") || path.startsWith("/qr")) return true;

        // 3) Static resources (let Bootstrap/CSS/fonts/images load)
        if (path.startsWith("/assets/") || path.startsWith("/inc/")) return true;  // header/footer, custom CSS folder

        // 4) File extension allowlist
        int dot = path.lastIndexOf('.');
        if (dot != -1) {
            String ext = path.substring(dot).toLowerCase();
            if (PUBLIC_EXT.contains(ext)) return true;
        }

        return false; // everything else requires session
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        if (isPublic(req)) {
            // Optional: set cache headers for static assets for better perf
            String uri = req.getRequestURI();
            if (uri.startsWith(req.getContextPath() + "/assets/")) {
                resp.setHeader("Cache-Control", "public, max-age=31536000, immutable");
            }
            chain.doFilter(request, response);
            return;
        }

        boolean loggedIn = (session != null && session.getAttribute("user") != null);
        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
