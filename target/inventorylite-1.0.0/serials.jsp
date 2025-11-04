<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/inc/header.jsp"/>

<h2 class="my-3">Serial / IMEI Intake</h2>
<div class="card p-3 mb-3">
<form method="post" action="serials" class="row g-3">
  <div class="col-md-4"><label class="form-label">Product ID</label><input name="productId" type="number" class="form-control" required></div>
  <div class="col-md-4"><label class="form-label">Warehouse ID</label><input name="warehouseId" type="number" class="form-control" value="1"></div>
  <div class="col-12"><label class="form-label">Serials (one per line)</label><textarea name="serials" rows="8" class="form-control" placeholder="scan or paste serials here"></textarea></div>
  <div class="col-12"><button class="btn btn-brand">Save Serials</button></div>
</form></div>

<jsp:include page="/inc/footer.jsp"/>
