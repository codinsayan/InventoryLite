<jsp:include page="/inc/header.jsp"/>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>

<section class="page-section">
  <div class="card p-4 products-card">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <h2 class="h2" style="margin:0">Products</h2>
      <c:if test="${sessionScope.role == 'ADMIN'}">
        <a class="btn btn-brand" href="${pageContext.request.contextPath}/products?action=new">+ Add Product</a>
      </c:if>
    </div>

    <div class="table-wrap">
      <table class="table table-hover table-sm products-table">
        <colgroup>
          <col style="width:72px;" />
          <col />
          <col style="width:160px;" />
          <col style="width:140px;" />
          <col style="width:90px;" />
          <col style="width:140px;" />
          <col style="width:160px;" />
          <col style="width:180px;" />
        </colgroup>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>SKU</th>
            <th>Category</th>
            <th class="text-center">Qty</th>
            <th class="text-right">Unit Price</th>
            <th class="text-right">Total Value</th>
            <th class="text-center">Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${products}" var="p">
            <tr>
              <td>${p.id}</td>
              <td class="truncate">${p.name}</td>
              <td class="mono">${p.sku}</td>
              <td>${p.category}</td>
              <td class="text-center mono">${p.quantity}</td>
              <td class="text-right mono">₹ <fmt:formatNumber value="${p.price}" minFractionDigits="0" maxFractionDigits="2"/></td>
              <td class="text-right mono">
                ₹ <fmt:formatNumber value="${p.totalValue}" minFractionDigits="0" maxFractionDigits="2"/>
              </td>
              <td class="text-center">
                <c:if test="${sessionScope.role == 'ADMIN'}">
                  <span class="btn-group">
                    <a class="btn btn-soft" href="${pageContext.request.contextPath}/products?action=edit&id=${p.id}">Edit</a>
                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/products?action=delete&id=${p.id}"
                       onclick="return confirm('Delete this product?');">Delete</a>
                  </span>
                </c:if>
              </td>
            </tr>
          </c:forEach>

          <c:if test="${empty products}">
            <tr><td colspan="8" style="text-align:center;">No products yet.</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</section>

<jsp:include page="/inc/footer.jsp"/>
