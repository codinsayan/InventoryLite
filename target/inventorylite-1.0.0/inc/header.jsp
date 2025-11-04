<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>InventoryLite</title>

  <!-- Primary stylesheet -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css" />
  <!-- Inline fallback -->
  <style><%@ include file="/assets/css/app.css" %></style>

  <!-- Load page-specific JS -->
  <c:set var="ctx" value="${pageContext.request.contextPath}" />
  <c:set var="uri" value="${pageContext.request.requestURI}" />
  <c:if test="${fn:endsWith(uri, '/orders')}">
    <!-- correct path for your file location -->
    <script src="${ctx}/orders.js?v=20251103" defer></script>
  </c:if>
</head>
<body>
  <header class="navbar">
    <div class="navbar-inner container-narrow">
      <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">InventoryLite</a>
      <nav class="nav-links">
        <a href="${pageContext.request.contextPath}/">Dashboard</a>
        <a href="${pageContext.request.contextPath}/products">Products</a>
        <a href="${pageContext.request.contextPath}/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/sales">Sales</a>
        <a href="${pageContext.request.contextPath}/pc-builder.jsp">PC Builder</a>
        <a href="${pageContext.request.contextPath}/users">Users</a>
      </nav>
      <div class="nav-right">
        <span class="pill" style="background:#1f3d2b; color:#8ee59b;">Online</span>
        <a class="btn btn-soft" href="${pageContext.request.contextPath}/logout">Logout</a>
      </div>
    </div>
  </header>
  <main>
    <div class="container-narrow">
