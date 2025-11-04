<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="/inc/header.jsp"/>

<section class="page-section">
  <div class="card p-4" style="max-width:900px;margin:16px auto 60px;">
    <h2 class="h2" style="margin-top:0">
      <c:choose>
        <c:when test="${not empty product}">Edit Product</c:when>
        <c:otherwise>Add Product</c:otherwise>
      </c:choose>
    </h2>

    <form method="post" action="${pageContext.request.contextPath}/products" id="productForm">
      <c:if test="${not empty product}">
        <input type="hidden" name="id" value="${product.id}"/>
      </c:if>

      <div class="form-row">
        <div>
          <label>Name</label>
          <input name="name" required value="${product.name}"/>
        </div>
        <div>
          <label>SKU</label>
          <input name="sku" required value="${product.sku}"/>
        </div>
      </div>

      <div class="form-row">
        <div>
          <label>Category</label>
          <!-- Type a new one or choose existing -->
          <input name="category" list="categoryList" value="${product.category}" placeholder="Type or choose…"/>
          <datalist id="categoryList">
            <c:forEach var="cname" items="${categories}">
              <option value="${cname}"/>
            </c:forEach>
          </datalist>
          <small class="form-text">You can type a new category; it will be saved automatically.</small>
        </div>

        <div>
          <label>Quantity</label>
          <input type="number" name="quantity" min="0" value="<c:out value='${empty product ? 0 : product.quantity}'/>"/>
        </div>

        <div>
          <label>Unit Price (₹)</label>
          <input type="number" name="price" step="0.01" min="0" value="<c:out value='${empty product ? "" : product.price}'/>"/>
        </div>
      </div>

      <div class="d-flex justify-content-between align-items-center my-3">
        <span class="muted">Inventory Value: <strong id="invValue">₹ 0</strong></span>
        <div>
          <a class="btn btn-soft" href="${pageContext.request.contextPath}/products">Cancel</a>
          <button class="btn btn-brand" type="submit">Save</button>
        </div>
      </div>
    </form>
  </div>
</section>

<script>
(function(){
  const q = document.querySelector('input[name="quantity"]');
  const p = document.querySelector('input[name="price"]');
  const out = document.getElementById('invValue');
  const nf  = new Intl.NumberFormat('en-IN', { maximumFractionDigits: 2 });

  function update(){
    const qq = parseFloat(q.value || '0');
    const pp = parseFloat(p.value || '0');
    out.textContent = '₹ ' + nf.format((qq*pp) || 0);
  }
  q.addEventListener('input', update);
  p.addEventListener('input', update);
  update();
})();
</script>

<jsp:include page="/inc/footer.jsp"/>
