<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/inc/header.jsp"/>

<h2 class="my-3">POS / Sales</h2>
<div class="card p-3">
  <div class="row g-2 mb-2">
    <div class="col-md-5"><input class="form-control" placeholder="Scan or search SKU"></div>
    <div class="col-md-3"><input class="form-control" placeholder="Customer phone/email"></div>
    <div class="col-md-2"><input class="form-control" placeholder="Warehouse" value="1"></div>
    <div class="col-md-2"><button class="btn btn-brand w-100">Add</button></div>
  </div>
  <table class="table table-sm">
    <thead><tr><th>SKU</th><th>Name</th><th>Qty</th><th>Price</th><th>Total</th><th></th></tr></thead>
    <tbody>
      <tr><td>6187246</td><td>Lenovo Ideapad Pro 5i</td><td>1</td><td>₹ 3000000</td><td>₹ 3000000</td>
      <td><button class="btn btn-sm btn-soft">Serials</button></td></tr>
    </tbody>
  </table>
  <div class="d-flex justify-content-end"><button class="btn btn-brand">Checkout</button></div>
</div>

<jsp:include page="/inc/footer.jsp"/>
