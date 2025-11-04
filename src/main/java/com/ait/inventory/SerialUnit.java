
package com.ait.inventory;

public class SerialUnit {
    private long id;
    private long productId;
    private String serialOrImei;
    private String status; // IN, SOLD, RMA, REFURB
    private java.sql.Date warrantyStart;
    private java.sql.Date warrantyEnd;
    private String notes;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public String getSerialOrImei() { return serialOrImei; }
    public void setSerialOrImei(String serialOrImei) { this.serialOrImei = serialOrImei; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.sql.Date getWarrantyStart() { return warrantyStart; }
    public void setWarrantyStart(java.sql.Date warrantyStart) { this.warrantyStart = warrantyStart; }
    public java.sql.Date getWarrantyEnd() { return warrantyEnd; }
    public void setWarrantyEnd(java.sql.Date warrantyEnd) { this.warrantyEnd = warrantyEnd; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
