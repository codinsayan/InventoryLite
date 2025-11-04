<%-- Display flash messages placed in request scope --%>
<c:if test="${not empty success}"><div class="alert alert-success my-2">${success}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-danger my-2">${error}</div></c:if>
<c:if test="${not empty info}"><div class="alert alert-warning my-2">${info}</div></c:if>
