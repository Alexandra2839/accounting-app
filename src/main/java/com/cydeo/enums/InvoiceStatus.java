package com.cydeo.enums;

public enum InvoiceStatus {

    AWAITING_APPROVAL("Awaiting Approval"), APPROVED("Approved");

    private final String values;

    InvoiceStatus(String values) {
        this.values = values;
    }

    public String getValues() {
        return values;
    }
}
