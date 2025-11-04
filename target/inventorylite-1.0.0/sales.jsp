<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/inc/header.jsp"/>

<div class="card p-4" style="max-width:1100px;margin:16px auto 60px;">
  <div class="d-flex justify-content-between align-items-center">
    <h2 style="margin:0">Sales</h2>
    <div>
      <a href="${pageContext.request.contextPath}/sales?view=orders"
         class="btn ${view=='orders'?'btn-brand':'btn-secondary'}">Orders</a>
      <a href="${pageContext.request.contextPath}/sales?view=items"
         class="btn ${view=='items'?'btn-brand':'btn-secondary'}">Sold Items</a>
    </div>
  </div>

  <form method="get" action="${pageContext.request.contextPath}/sales"
        class="form-row" style="margin-top:12px;gap:12px;">
    <input type="hidden" name="view" value="${view}"/>
    <div><label>From</label><input type="date" name="from" value="${from}"/></div>
    <div><label>To</label><input type="date" name="to" value="${to}"/></div>
    <div style="flex:1;"><label>Search</label>
      <input type="text" name="q" value="${q}" placeholder="Customer / Product / Order #"/>
    </div>
    <div style="align-self:end"><button class="btn btn-brand" type="submit">Filter</button></div>
  </form>

  <c:choose>
    <c:when test="${view=='items'}">
      <div class="table-wrap" style="margin-top:16px;">
        <table class="table table-hover table-sm">
          <thead>
            <tr>
              <th>Order #</th>
              <th>Date</th>
              <th>Customer</th>
              <th>Product</th>
              <th class="text-center">Qty</th>
              <th class="text-right">Unit</th>
              <th class="text-right">Line Total</th>
              <th class="text-center">Invoice</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="r" items="${sold}">
              <tr>
                <td>${r.orderId}</td>
                <td><fmt:formatDate value="${r.createdAt}" pattern="dd-MMM-yy HH:mm"/></td>
                <td><c:out value="${empty r.customerName ? 'Walk-in' : r.customerName}"/></td>
                <td><c:out value="${r.productName}"/></td>
                <td class="text-center">${r.qty}</td>
                <td class="text-right">₹ <fmt:formatNumber value="${r.price}" minFractionDigits="0" maxFractionDigits="2"/></td>
                <td class="text-right">₹ <fmt:formatNumber value="${r.lineTotal}" minFractionDigits="0" maxFractionDigits="2"/></td>
                <td class="text-center">
                  <a class="btn btn-secondary btn-sm"
                     href="${pageContext.request.contextPath}/invoice.pdf?id=${r.orderId}" target="_blank">View PDF</a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:when>

    <c:otherwise>
      <div class="table-wrap" style="margin-top:16px;">
        <table class="table table-hover table-sm">
          <thead>
            <tr>
              <th>Order #</th>
              <th>Date</th>
              <th>Customer</th>
              <th>Items</th>
              <th class="text-right">Total</th>
              <th class="text-center">Invoice</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="o" items="${orders}">
              <tr>
                <td>${o.id}</td>
                <td><fmt:formatDate value="${o.createdAt}" pattern="dd-MMM-yy HH:mm"/></td>
                <td><c:out value="${empty o.customerName ? 'Walk-in' : o.customerName}"/></td>
                <td><c:out value="${empty o.itemsPreview ? '-' : o.itemsPreview}"/></td>
                <td class="text-right">₹ <fmt:formatNumber value="${o.total}" minFractionDigits="0" maxFractionDigits="2"/></td>
                <td class="text-center">
                  <a class="btn btn-secondary btn-sm"
                     href="${pageContext.request.contextPath}/invoice.pdf?id=${o.id}" target="_blank">View PDF</a>
                  <a class="btn btn-brand btn-sm"
                     href="${pageContext.request.contextPath}/invoice.pdf?id=${o.id}&download=1">Download</a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:otherwise>
  </c:choose>
</div>

<jsp:include page="/inc/footer.jsp"/>
