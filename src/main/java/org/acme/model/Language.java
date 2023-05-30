package org.acme.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Language {
    @JsonProperty("officialLanguage")
    @JsonSetter("name")
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Language() {
    }

    public Language(String name) {
        this.name = name;
    }
}
