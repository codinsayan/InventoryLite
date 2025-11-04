<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  String me  = (String) session.getAttribute("user");
%>

<jsp:include page="/inc/header.jsp"/>

<main class="container-narrow">

  <!-- Hero -->
  <section class="card" style="margin-bottom:16px;">
    <div class="card-header row-between" style="padding:18px 22px 14px 22px;">
      <div>
        <h2 class="card-title" style="margin:0;">Dashboard</h2>
        <p class="muted" style="margin:.25rem 0 0 0;">
          Welcome <span class="mono"><%= (me!=null? me : "there") %></span> — manage your store at a glance.
        </p>
      </div>
    </div>
  </section>

  <!-- Quick Modules -->
  <section class="grid">
    <!-- Products -->
    <div class="card p-3">
      <div class="d-flex justify-content-between">
        <h3>Products</h3>
        <span class="badge-soft">Inventory</span>
      </div>
      <p class="mb-2">View, add, edit and delete products. Keep stock and pricing up to date.</p>
      <a class="btn btn-brand" href="<%=ctx%>/products">Open</a>
    </div>

    <!-- Orders (POS) -->
    <div class="card p-3">
      <div class="d-flex justify-content-between">
        <h3>Orders</h3>
        <span class="badge-soft">POS</span>
      </div>
      <p class="mb-2">Create customer orders and reduce stock when you complete a sale.</p>
      <a class="btn btn-brand" href="<%=ctx%>/orders">Open</a>
    </div>

    <!-- Sales -->
    <div class="card p-3">
      <div class="d-flex justify-content-between">
        <h3>Sales</h3>
        <span class="badge-soft">Reports</span>
      </div>
      <p class="mb-2">Browse orders & sold items, filter by date, and download invoices.</p>
      <a class="btn btn-brand" href="<%=ctx%>/sales">Open</a>
    </div>

    <!-- PC Builder -->
    <div class="card p-3">
      <div class="d-flex justify-content-between">
        <h3>PC Builder</h3>
        <span class="badge-soft">Tools</span>
      </div>
      <p class="mb-2">Check compatibility (CPU, board, PSU, case) and build faster.</p>
      <a class="btn btn-brand" href="<%=ctx%>/pc-builder">Open</a>
    </div>

    <!-- Users (Admin only shows button; others read-only label) -->
    <div class="card p-3">
      <div class="d-flex justify-content-between">
        <h3>Users</h3>
        <span class="badge-soft">Access</span>
      </div>
      <p class="mb-2">Manage staff roles and control permissions across the app.</p>
      <c:choose>
        <c:when test="${sessionScope.role == 'ADMIN'}">
          <a class="btn btn-brand" href="<%=ctx%>/users">Open</a>
        </c:when>
        <c:otherwise>
          <span class="badge"></span>
        </c:otherwise>
      </c:choose>
    </div>
  </section>

</main>

<!-- tiny, page-scoped polish (optional) -->
<style>
  .grid{
    display:grid; gap:16px;
    grid-template-columns: repeat(auto-fill, minmax(280px,1fr));
  }
  .badge-soft{
    display:inline-block; padding:.2rem .5rem; border-radius:.5rem;
    background:#1f2a37; color:#9fb3c8; font-size:.8rem;
  }
  .mono{ font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace; }
</style>

<jsp:include page="/inc/footer.jsp"/>
