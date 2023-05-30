package org.acme.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Currencies {
    @JsonProperty(value = "currencyName")
    @JsonSetter("name")
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Currencies(String name) {
        this.name = name;
    }

    public Currencies() {
    }
}
