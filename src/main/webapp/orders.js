// InventoryLite Orders Page Script
// Builds a cart, validates stock, posts arrays: productId[] and qty[]
(function () {
  function ready(fn) {
    if (document.readyState !== "loading") fn();
    else document.addEventListener("DOMContentLoaded", fn, { once: true });
  }

  function formatINR(n) {
    try {
      return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2,
      }).format(n);
    } catch (e) {
      return "₹ " + (Math.round(n * 100) / 100).toFixed(2);
    }
  }

  ready(function () {
    var form = document.getElementById("orderForm");
    if (!form) return;

    var sel = document.getElementById("productSel");
    var qtySel = document.getElementById("qtySel");
    var addBtn = document.getElementById("addBtn");
    var tbody = document.querySelector("#cartTable tbody");
    var tfootGrand = document.getElementById("grandCell");
    var hiddenDiv = document.getElementById("hiddenInputs");

    function rowFor(pid) {
      return tbody.querySelector('tr[data-product-id="' + pid + '"]');
    }

    function refreshTotals() {
      var rows = tbody.querySelectorAll("tr");
      var grand = 0;
      rows.forEach(function (tr) {
        var price = parseFloat(tr.getAttribute("data-price"));
        var qty = parseInt(tr.querySelector(".qty-input").value || "0", 10);
        var total = price * qty;
        tr.querySelector(".line-total").textContent = formatINR(total);
        grand += total;
      });
      tfootGrand.textContent = formatINR(grand);
    }

    function rebuildHidden() {
      hiddenDiv.innerHTML = "";
      var rows = tbody.querySelectorAll("tr");
      rows.forEach(function (tr) {
        var pid = tr.getAttribute("data-product-id");
        var qty = tr.querySelector(".qty-input").value;
        var i1 = document.createElement("input");
        i1.type = "hidden";
        i1.name = "productId[]";
        i1.value = pid;
        var i2 = document.createElement("input");
        i2.type = "hidden";
        i2.name = "qty[]";
        i2.value = qty;
        hiddenDiv.appendChild(i1);
        hiddenDiv.appendChild(i2);
      });
    }

    function clampQty(input, max) {
      var v = parseInt(input.value || "0", 10);
      if (!Number.isFinite(v) || v < 1) v = 1;
      if (v > max) v = max;
      input.value = v;
    }

    function addToCart(pid, name, price, stock, addQty) {
      var existing = rowFor(pid);
      if (existing) {
        var qInput = existing.querySelector(".qty-input");
        var current = parseInt(qInput.value || "0", 10);
        var next = current + addQty;
        if (next > stock) next = stock;
        qInput.value = next;
        refreshTotals();
        rebuildHidden();
        return;
      }

      var tr = document.createElement("tr");
      tr.setAttribute("data-product-id", pid);
      tr.setAttribute("data-price", String(price));
      tr.setAttribute("data-stock", String(stock));

      var tdName = document.createElement("td");
      tdName.textContent = name;

      var tdPrice = document.createElement("td");
      tdPrice.className = "text-right";
      tdPrice.textContent = formatINR(price);

      var tdQty = document.createElement("td");
      var input = document.createElement("input");
      input.type = "number";
      input.min = "1";
      input.value = String(addQty);
      input.className = "qty-input";
      input.style.width = "72px";
      input.addEventListener("input", function () {
        clampQty(input, stock);
        refreshTotals();
        rebuildHidden();
      });
      input.addEventListener("blur", function () {
        clampQty(input, stock);
        refreshTotals();
        rebuildHidden();
      });
      tdQty.appendChild(input);

      var tdStock = document.createElement("td");
      tdStock.className = "text-right";
      tdStock.textContent = stock;

      var tdLine = document.createElement("td");
      tdLine.className = "text-right line-total";
      tdLine.textContent = formatINR(price * addQty);

      var tdDel = document.createElement("td");
      var delBtn = document.createElement("button");
      delBtn.type = "button";
      delBtn.className = "btn btn-danger btn-sm";
      delBtn.textContent = "Remove";
      delBtn.addEventListener("click", function () {
        tr.remove();
        refreshTotals();
        rebuildHidden();
      });
      tdDel.appendChild(delBtn);

      tr.appendChild(tdName);
      tr.appendChild(tdPrice);
      tr.appendChild(tdQty);
      tr.appendChild(tdStock);
      tr.appendChild(tdLine);
      tr.appendChild(tdDel);

      tbody.appendChild(tr);
      refreshTotals();
      rebuildHidden();
    }

    addBtn &&
      addBtn.addEventListener("click", function () {
        var opt = sel.options[sel.selectedIndex];
        if (!opt || !opt.value) {
          alert("Choose a product first.");
          return;
        }
        var pid = opt.value;
        var price = parseFloat(opt.getAttribute("data-price") || "0");
        var stock = parseInt(opt.getAttribute("data-stock") || "0", 10);
        var name = opt.textContent.replace(/\s+\(Stock:.*$/, "").trim();
        var q = parseInt(qtySel.value || "1", 10);
        if (!Number.isFinite(q) || q < 1) q = 1;
        if (stock <= 0) {
          alert("This product is out of stock.");
          return;
        }
        if (q > stock) {
          alert("Only " + stock + " available. Adding " + stock + ".");
          q = stock;
        }
        addToCart(pid, name, price, stock, q);
      });

    form.addEventListener("submit", function (e) {
      if (!tbody.querySelector("tr")) {
        e.preventDefault();
        alert("Add at least one product to the order.");
        return;
      }
      rebuildHidden();
    });
  });
})();
