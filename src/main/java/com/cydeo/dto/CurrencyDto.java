package com.cydeo.dto;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "date",
        "usd"
})
@Generated("jsonschema2pojo")
public class CurrencyDto {

    @JsonProperty("date")
    private String date;
    @JsonProperty("usd")
    private Map<String, BigDecimal> usd;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("usd")
    public Map<String, BigDecimal> getUsd() {
        return usd;
    }

    @JsonProperty("usd")
    public void setUsd(Map<String, BigDecimal> usd) {
        this.usd = usd;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public BigDecimal getEuro() {
        return usd.get("eur").setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getBritishPound() {
        return usd.get("gbp").setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCanadianDollar() {
        return usd.get("cad").setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getJapaneseYen() {
        return usd.get("jpy").setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getIndianRupee() {
        return usd.get("inr").setScale(2, RoundingMode.HALF_UP);
    }
}
