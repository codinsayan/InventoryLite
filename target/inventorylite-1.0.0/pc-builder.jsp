<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/inc/header.jsp"/>

<h2 class="my-3">PC Builder</h2>
<div class="card p-3">
<form class="row g-3">
  <div class="col-md-3"><label class="form-label">CPU Socket</label><input id="cpuSocket" class="form-control"></div>
  <div class="col-md-3"><label class="form-label">Motherboard Socket</label><input id="moboSocket" class="form-control"></div>
  <div class="col-md-2"><label class="form-label">CPU Watt</label><input id="cpuWatt" type="number" class="form-control"></div>
  <div class="col-md-2"><label class="form-label">GPU Watt</label><input id="gpuWatt" type="number" class="form-control"></div>
  <div class="col-md-2"><label class="form-label">PSU Watt</label><input id="psuWatt" type="number" class="form-control"></div>
  <div class="col-12"><button type="button" class="btn btn-brand" onclick="validate()">Check Compatibility</button>
    <span class="badge-soft ms-2" id="result">Awaiting input…</span></div>
</form>
</div>
<script>
function validate(){
  const cs=document.getElementById('cpuSocket').value.trim().toLowerCase();
  const ms=document.getElementById('moboSocket').value.trim().toLowerCase();
  const cpu=parseInt(document.getElementById('cpuWatt').value||0);
  const gpu=parseInt(document.getElementById('gpuWatt').value||0);
  const psu=parseInt(document.getElementById('psuWatt').value||0);
  const ok=(cs&&ms&&cs===ms)&&(psu>=cpu+gpu+150);
  document.getElementById('result').textContent = ok ? 'Looks compatible ✅' : 'Incompatible ❌';
}
</script>

<jsp:include page="/inc/footer.jsp"/>
