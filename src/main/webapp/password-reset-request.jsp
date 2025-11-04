<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<jsp:include page="/inc/header.jsp"/>

<div class="card p-4" style="max-width:520px; margin:0 auto;">
  <h2 class="my-3">Password reset</h2>
  <p class="mb-2">Enter your username. A reset link will be generated (for this demo it will be shown on screen).</p>

  <c:if test="${not empty info}">
    <div class="alert" style="background:#111a2b;border-color:#22314d">
      ${info}
    </div>
  </c:if>

  <form method="post" action="${pageContext.request.contextPath}/password-reset-request">
    <div class="mb-3">
      <label>Username</label>
      <input name="username" required/>
    </div>
    <button class="btn btn-brand" type="submit">Generate link</button>
  </form>
</div>

<jsp:include page="/inc/footer.jsp"/>
