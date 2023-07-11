package com.cydeo.entity;

import lombok.Data;

@Data
public class ChargeRequest {

    public enum Currency {
        EUR, USD;
    }


    private String description;
    private String amount;
    private Currency currency;
    private String stripeEmail;
    private String stripeToken;
}