package org.acme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Language {
    @JsonSetter("iso639_1")
    @JsonIgnore
    private String iso1;
    @JsonSetter("iso639_2")
    @JsonIgnore
    private String iso2;
    @JsonProperty("officialLanguage")
    @JsonSetter("name")
    private String name;

    @JsonIgnore
    private String nativeName;

    public String getIso1() {
        return iso1;
    }

    public void setIso1(String iso1) {
        this.iso1 = iso1;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public Language() {
    }

    public Language(String name) {
        this.name = name;
    }
}
