<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<jsp:include page="/inc/header.jsp"/>

<div class="card p-4" style="max-width:480px; margin:0 auto;">
  <h2 class="my-3" style="margin-top:0">Create account</h2>

  <c:if test="${not empty error}">
    <div class="alert" style="background:#4b1d1d;border-color:#7f1d1d;color:#ffb4b4;margin-bottom:10px">
      ${error}
    </div>
  </c:if>
  <c:if test="${not empty success}">
    <div class="alert" style="background:#16361e;border-color:#1f6b2e;color:#b7ffd1;margin-bottom:10px">
      ${success}
    </div>
  </c:if>

  <form method="post" action="${pageContext.request.contextPath}/signup">
    <div class="form-row">
      <div style="flex:1 1 100%">
        <label>Username</label>
        <input name="username" required/>
      </div>
    </div>
    <div class="form-row">
      <div>
        <label>Password</label>
        <input type="password" name="password" required minlength="4"/>
      </div>
      <div>
        <label>Confirm password</label>
        <input type="password" name="confirm" required minlength="4"/>
      </div>
    </div>
    <div class="form-row">
      <button class="btn btn-brand" type="submit">Create account</button>
      <span class="form-text">Already have an account? <a href="${pageContext.request.contextPath}/login">Log in</a></span>
    </div>
  </form>
</div>

<jsp:include page="/inc/footer.jsp"/>
