<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/inc/header.jsp"/>

<h2 class="my-4">Login</h2>
<div class="card p-4" style="max-width:520px; margin:0 auto;">
  <form method="post" action="${pageContext.request.contextPath}/login">
    <div class="mb-3">
      <label class="form-label">Username</label>
      <input name="username" class="form-control" required>
    </div>
    <div class="mb-3">
      <label class="form-label">Password</label>
      <input name="password" type="password" class="form-control" required>
    </div>
    <button class="btn btn-brand w-100" type="submit">Login</button>
    <div class="d-flex justify-content-between small mt-3">
      <a href="${pageContext.request.contextPath}/signup">Create account</a>
      <a href="${pageContext.request.contextPath}/password-reset-request">Forgot password?</a>
    </div>
  </form>
</div>

<jsp:include page="/inc/footer.jsp"/>
