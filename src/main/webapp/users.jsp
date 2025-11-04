<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
  String ctx   = request.getContextPath();
  String me    = (String) session.getAttribute("user");
%>

<jsp:include page="/inc/header.jsp" />

<main class="container-narrow">
  <!-- ONE card only -->
  <section class="card users-card">
    <div class="card-header row-between users-header">
      <div class="title-block">
        <h2 class="card-title">Users &amp; Access</h2>
        <p class="muted subtext">Manage staff roles and access control.</p>
      </div>
      <div class="toolbar">
        <a class="btn btn-primary" href="<%=ctx%>/signup">Add New User</a>
      </div>
    </div>

    <div class="card-body users-body">
      <c:choose>
        <c:when test="${empty users}">
          <div class="empty-state">
            <p>No users yet.</p>
            <a class="btn btn-primary" href="<%=ctx%>/signup">Create the first user</a>
          </div>
        </c:when>
        <c:otherwise>
          <div class="table-wrap">
            <table class="table table-hover table-compact">
              <thead>
                <tr>
                  <th style="width:72px;text-align:center;">ID</th>
                  <th>Username</th>
                  <th style="width:320px;">Role</th>
                  <th style="width:140px;">Status</th>
                </tr>
              </thead>
              <tbody>
                <c:set var="rolesCsv" value="ADMIN,MANAGER,CASHIER,USER,VIEWER"/>
                <c:forEach var="u" items="${users}">
                  <tr>
                    <td style="text-align:center;">${u.id}</td>
                    <td class="mono">${u.username}</td>
                    <td>
                      <c:choose>
                        <c:when test="${sessionScope.role == 'ADMIN'}">
                          <form method="post" action="<%=ctx%>/users" class="inline role-form" data-current="${u.role}">
                            <input type="hidden" name="action" value="updateRole"/>
                            <input type="hidden" name="id" value="${u.id}"/>

                            <select name="role" class="input select role-select"
                                    <c:if test="${u.username==me}">disabled</c:if>>
                              <c:forEach var="r" items="${fn:split(rolesCsv, ',')}">
                                <option value="${r}" ${u.role==r?'selected':''}>${r}</option>
                              </c:forEach>
                            </select>

                            <button type="submit"
                                    class="btn btn-secondary save-btn"
                                    <c:if test="${u.username==me}">disabled title="You cannot change your own role"</c:if>>
                              Save
                            </button>
                          </form>
                        </c:when>
                        <c:otherwise>
                          <span class="badge">${u.role}</span>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td><span class="badge success">Active</span></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </section>
</main>

<script>
  (function () {
    // toast on ?ok=1
    const u = new URL(window.location.href);
    if (u.searchParams.get('ok') === '1') {
      const t = document.createElement('div');
      t.className = 'toast success';
      t.textContent = 'Role updated';
      document.body.appendChild(t);
      setTimeout(() => t.classList.add('show'), 10);
      setTimeout(() => t.classList.remove('show'), 2200);
      setTimeout(() => t.remove(), 2600);
    }
    // enable Save only when changed
    document.querySelectorAll('.role-form').forEach(form => {
      const cur = form.dataset.current;
      const sel = form.querySelector('.role-select');
      const btn = form.querySelector('.save-btn');
      const lock = () => { btn.disabled = sel.disabled || sel.value === cur; };
      lock(); sel && sel.addEventListener('change', lock);
    });
  })();
</script>

<style>
  /* scoped spacing to match other pages */
  .users-card { border-radius: 14px; }
  .users-header { padding: 18px 22px 12px 22px; }
  .users-body   { padding: 0 22px 22px 22px; }
  .users-header .title-block { padding-left: 2px; }
  .users-header .toolbar { margin-right: 8px; }
  .users-header .btn-primary { padding:.62rem 1.1rem; border-radius:.75rem; }

  /* shared look */
  .row-between{ display:flex; align-items:flex-end; justify-content:space-between; gap:1rem; }
  .card-title{ margin:0 0 .25rem 0; }
  .subtext{ margin:0; font-size:.92rem; }
  .muted{ color:var(--muted, #a8b0bb); }
  .table-wrap{ overflow:auto; }
  .table-compact td, .table-compact th{ padding:.72rem .9rem; }
  .mono{ font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, "Liberation Mono", monospace; }
  .btn-primary{ background:#2d6cf6; color:#fff; }
  .btn-secondary{ background:#222d3d; color:#e6edf3; }
  .badge{ display:inline-block; padding:.25rem .55rem; border-radius:.5rem; background:#2a3343; color:#cbd5e1; font-size:.82rem; }
  .badge.success{ background:#1f3d2b; color:#8ee59b; }
  .empty-state{ text-align:center; padding:2rem 1rem; }

  .toast{ position:fixed; right:1rem; bottom:1rem; background:#1f2937; color:#cfe7ff;
          padding:.6rem .9rem; border-radius:.6rem; opacity:0; transform:translateY(10px);
          transition:.25s; box-shadow:0 10px 25px rgba(0,0,0,.35); }
  .toast.success{ background:#143a22; color:#b9ffcb; }
  .toast.show{ opacity:1; transform:translateY(0); }

  @media (max-width: 720px){
    .users-header { padding:14px 16px 10px 16px; }
    .users-body   { padding:0 16px 16px 16px; }
  }
</style>

<jsp:include page="/inc/footer.jsp" />
