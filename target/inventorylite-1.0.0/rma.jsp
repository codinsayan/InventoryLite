<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/inc/header.jsp"/>

<h2 class="my-3">RMA & Warranty Center</h2>
<div class="card p-3 mb-3">
<form method="post" action="rma" class="row g-3">
  <div class="col-md-5"><label class="form-label">Serial/IMEI</label><input name="serial" class="form-control"></div>
  <div class="col-md-7"><label class="form-label">Issue</label><input name="issue" class="form-control" placeholder="Describe the issue"></div>
  <div class="col-12"><button class="btn btn-brand">Create RMA</button></div>
</form></div>
<div class="card p-3">
<table class="table table-sm"><thead><tr><th>ID</th><th>Serial</th><th>Status</th><th>Intake</th><th>Resolution</th></tr></thead><tbody></tbody></table>
</div>

<jsp:include page="/inc/footer.jsp"/>
