<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/inc/header.jsp"/>

<h2 class="my-3">Service & Repair Tickets</h2>
<div class="card p-3 mb-3">
<form method="post" action="tickets" class="row g-3">
  <div class="col-md-6"><label class="form-label">Device description</label><input name="device" class="form-control"></div>
  <div class="col-md-6"><label class="form-label">Diagnosis</label><input name="diagnosis" class="form-control"></div>
  <div class="col-md-3"><label class="form-label">Labor minutes</label><input name="labor" type="number" class="form-control" value="0"></div>
  <div class="col-12"><button class="btn btn-brand">Create Ticket</button></div>
</form></div>
<div class="card p-3">
<table class="table table-sm"><thead><tr><th>ID</th><th>Device</th><th>Status</th><th>Labor</th></tr></thead><tbody></tbody></table>
</div>

<jsp:include page="/inc/footer.jsp"/>
