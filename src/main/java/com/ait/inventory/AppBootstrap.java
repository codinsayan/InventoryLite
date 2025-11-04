package com.ait.inventory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppBootstrap implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        DBUtil.configure(
            ctx.getInitParameter("JDBC_URL"),
            ctx.getInitParameter("JDBC_USER"),
            ctx.getInitParameter("JDBC_PASS")
        );
    }
}
