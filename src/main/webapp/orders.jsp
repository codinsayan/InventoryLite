<jsp:include page="/inc/header.jsp"/>
<% if (request.getAttribute("products") == null) {
     response.sendRedirect(request.getContextPath()+"/orders"
       + (request.getQueryString()!=null? "?"+request.getQueryString():""));
     return;
   } %>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="card p-4" style="max-width:1050px;margin:16px auto 60px;">
  <div class="d-flex justify-content-between align-items-center mb-2">
    <h2 class="h2" style="margin:0">Create Order</h2>
  </div>

  <c:if test="${not empty param.success}">
    <div class="alert" style="border-color:#1e8e3e;color:#c8f7ce">Order #${param.orderId} created.</div>
  </c:if>
  <c:if test="${not empty error}">
    <div class="alert" style="border-color:#ea4335;color:#ffd0d0">${error}</div>
  </c:if>

  <form method="post" action="${pageContext.request.contextPath}/orders" id="orderForm">
    <div class="form-row mb-2">
      <div>
        <label>Customer (optional)</label>
        <input name="customer" placeholder="Walk-in / Customer name"/>
      </div>
    </div>

    <div class="card p-3" style="margin-top:8px;">
      <div class="form-row">
        <div>
          <label>Product</label>
          <select id="productSel">
            <option value="">-- choose product --</option>
            <c:forEach var="p" items="${products}">
              <option value="${p.id}" data-price="${p.price}" data-stock="${p.quantity}">
                ${p.name} — ₹ <fmt:formatNumber value="${p.price}" minFractionDigits="0" maxFractionDigits="2"/> (Stock: ${p.quantity})
              </option>
            </c:forEach>
          </select>
        </div>
        <div style="max-width:160px;">
          <label>Qty</label>
          <input id="qtySel" type="number" min="1" value="1"/>
        </div>
        <div style="align-self:end">
          <button type="button" class="btn btn-brand" id="addBtn">Add</button>
        </div>
      </div>

      <div class="table-wrap" style="margin-top:12px;">
        <table class="table table-hover table-sm" id="cartTable">
          <thead>
            <tr>
              <th style="width:48px;">#</th>
              <th>Product</th>
              <th style="width:120px;" class="text-center">Qty</th>
              <th style="width:140px;" class="text-right">Unit</th>
              <th style="width:140px;" class="text-right">Line Total</th>
              <th style="width:100px;" class="text-center">Remove</th>
            </tr>
          </thead>
          <tbody></tbody>
          <tfoot>
            <tr>
              <th colspan="4" class="text-right">Grand Total</th>
              <th class="text-right" id="grandCell">₹ 0</th>
              <th></th>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>

    <!-- hidden arrays posted to servlet -->
    <div id="hiddenInputs"></div>

    <div class="d-flex justify-content-between align-items-center" style="margin-top:16px;">
      <span class="muted">Stock updates when you press Create Order.</span>
      <button class="btn btn-brand" type="submit" id="submitBtn">Create Order</button>
    </div>
  </form>
</div>

<!-- put this just above -->
<!-- <jsp:include page="/inc/footer.jsp"/> -->

<script>
(function () {
  function formatINR(n){try{return new Intl.NumberFormat('en-IN',{style:'currency',currency:'INR',maximumFractionDigits:2}).format(n);}catch(e){return "₹ "+(Math.round(n*100)/100).toFixed(2);}}
  function renumberRows(tbody){Array.from(tbody.querySelectorAll("tr")).forEach(function(tr,i){var c=tr.querySelector(".row-idx"); if(c) c.textContent=String(i+1);});}

  var form  = document.getElementById("orderForm");
  var sel   = document.getElementById("productSel");
  var qtyEl = document.getElementById("qtySel");
  var add   = document.getElementById("addBtn");
  var table = document.getElementById("cartTable");
  var tbody = table.querySelector("tbody");
  var grand = document.getElementById("grandCell");
  var hid   = document.getElementById("hiddenInputs");

  if(!form || !sel || !qtyEl || !add || !tbody || !grand || !hid){ console.warn("Orders JS: missing DOM ids"); return; }

  function findRow(pid){ return tbody.querySelector('tr[data-product-id="'+pid+'"]'); }

  function clampQty(input, max){
    var v = parseInt(input.value||"0",10);
    if(!Number.isFinite(v) || v<1) v=1;
    if(max>0 && v>max) v=max;
    input.value = String(v);
  }

  function refreshTotals(){
    var rows = tbody.querySelectorAll("tr");
    var sum = 0;
    rows.forEach(function(tr){
      var price = parseFloat(tr.getAttribute("data-price")||"0");
      var q = parseInt(tr.querySelector(".qty-input").value||"0",10);
      var lt = price * q;
      tr.querySelector(".line-total").textContent = formatINR(lt);
      sum += lt;
    });
    grand.textContent = formatINR(sum);
    renumberRows(tbody);
  }

  function rebuildHidden(){
    hid.innerHTML = "";
    Array.from(tbody.querySelectorAll("tr")).forEach(function(tr){
      var pid = tr.getAttribute("data-product-id");
      var q   = tr.querySelector(".qty-input").value;
      var i1=document.createElement("input"); i1.type="hidden"; i1.name="productId[]"; i1.value=pid;
      var i2=document.createElement("input"); i2.type="hidden"; i2.name="qty[]";        i2.value=q;
      hid.appendChild(i1); hid.appendChild(i2);
    });
  }

  function addRow(pid,name,price,stock,qty){
    var existing = findRow(pid);
    if(existing){
      var inp = existing.querySelector(".qty-input");
      var cur = parseInt(inp.value||"0",10);
      var next = cur + qty;
      if(stock>0 && next>stock) next=stock;
      inp.value = String(next);
      refreshTotals(); rebuildHidden();
      return;
    }

    var tr=document.createElement("tr");
    tr.setAttribute("data-product-id", pid);
    tr.setAttribute("data-price", String(price));
    tr.setAttribute("data-stock", String(stock));

    var tdIdx=document.createElement("td"); tdIdx.className="row-idx text-center"; tdIdx.textContent="";

    var tdName=document.createElement("td"); tdName.textContent=name;

    var tdQty=document.createElement("td"); tdQty.className="text-center";
    var inp=document.createElement("input"); inp.type="number"; inp.min="1"; inp.value=String(qty); inp.className="qty-input"; inp.style.width="72px";
    inp.addEventListener("input", function(){ clampQty(inp, stock); refreshTotals(); rebuildHidden(); });
    inp.addEventListener("blur",  function(){ clampQty(inp, stock); refreshTotals(); rebuildHidden(); });
    tdQty.appendChild(inp);

    var tdUnit=document.createElement("td"); tdUnit.className="text-right"; tdUnit.textContent=formatINR(price);

    var tdLine=document.createElement("td"); tdLine.className="text-right line-total"; tdLine.textContent=formatINR(price*qty);

    var tdDel=document.createElement("td"); tdDel.className="text-center";
    var del=document.createElement("button"); del.type="button"; del.className="btn btn-danger btn-sm"; del.textContent="Remove";
    del.addEventListener("click", function(){ tr.remove(); refreshTotals(); rebuildHidden(); });
    tdDel.appendChild(del);

    tr.appendChild(tdIdx); tr.appendChild(tdName); tr.appendChild(tdQty); tr.appendChild(tdUnit); tr.appendChild(tdLine); tr.appendChild(tdDel);
    tbody.appendChild(tr);
    refreshTotals(); rebuildHidden();
  }

  add.addEventListener("click", function(){
    var opt = sel.options[sel.selectedIndex];
    if(!opt || !opt.value){ alert("Choose a product first."); return; }
    var pid   = opt.value;
    var price = parseFloat(opt.getAttribute("data-price")||"0");
    var stock = parseInt(opt.getAttribute("data-stock")||"0",10);
    var name  = opt.textContent.replace(/\s+\(Stock:.*$/,"").trim();
    var q     = parseInt(qtyEl.value||"1",10);
    if(!Number.isFinite(q) || q<1) q=1;
    if(stock<=0){ alert("This product is out of stock."); return; }
    if(q>stock){ alert("Only "+stock+" available. Adding "+stock+"."); q=stock; }
    addRow(pid,name,price,stock,q);
  });

  form.addEventListener("submit", function(e){
    if(!tbody.querySelector("tr")){ e.preventDefault(); alert("Add at least one product to the order."); return; }
    rebuildHidden();
  });
})();
</script>

<jsp:include page="/inc/footer.jsp"/>
