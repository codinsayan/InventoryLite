
package com.ait.inventory;

public class Rma {
    private long id;
    private Long customerId;
    private long serialId;
    private String issue;
    private String status; // OPEN, DOA, REPAIR, SWAP, CLOSED
    private java.sql.Timestamp intakeDate;
    private String resolution;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public long getSerialId() { return serialId; }
    public void setSerialId(long serialId) { this.serialId = serialId; }
    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.sql.Timestamp getIntakeDate() { return intakeDate; }
    public void setIntakeDate(java.sql.Timestamp intakeDate) { this.intakeDate = intakeDate; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
}
