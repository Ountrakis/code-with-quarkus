package org.acme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Currencies {

    @JsonIgnore
    String code;
    @JsonProperty(value = "currencyName")
    @JsonSetter("name")
    String name;
    @JsonIgnore
    String symbol;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Currencies(String name) {
        this.name = name;
    }

    public Currencies() {
    }
}
