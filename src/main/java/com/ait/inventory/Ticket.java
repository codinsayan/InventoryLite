
package com.ait.inventory;

public class Ticket {
    private long id;
    private Long customerId;
    private String deviceDesc;
    private String diagnosis;
    private int laborMinutes;
    private String partsUsedJson;
    private String status;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getDeviceDesc() { return deviceDesc; }
    public void setDeviceDesc(String deviceDesc) { this.deviceDesc = deviceDesc; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public int getLaborMinutes() { return laborMinutes; }
    public void setLaborMinutes(int laborMinutes) { this.laborMinutes = laborMinutes; }
    public String getPartsUsedJson() { return partsUsedJson; }
    public void setPartsUsedJson(String partsUsedJson) { this.partsUsedJson = partsUsedJson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
