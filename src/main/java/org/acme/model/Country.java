package org.acme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
@MongoEntity(collection = "CountriesCache")
public class Country {

    @Id
    private ObjectId id;

    @JsonProperty(value = "countryName")
    @JsonSetter("name")
    private String name;

    @JsonProperty(value = "countryCode")
    @JsonSetter("alpha2Code")
    private String alpha2Code;

    @JsonSetter("capital")
    private String capital;

    @JsonProperty(value = "continent")
    @JsonSetter("region")
    private String region;

    @JsonProperty(value = "language")
    @JsonSetter("languages")
    private List<Language> languages;

    @JsonProperty(value = "currency")
    @JsonSetter("currencies")
    private List<Currencies> currencies;

    @JsonIgnore
    private Date createdAt;

    public Country() {
        this.createdAt = new Date();
        this.languages = new ArrayList<>();
        this.currencies = new ArrayList<>();
    }

    public Country(String name, String alpha2Code, String capital, String region, List<Language> languages, List<Currencies> currencies) {
        this.name = name;
        this.alpha2Code = alpha2Code;
        this.capital = capital;
        this.region = region;
        this.languages = languages;
        this.currencies = currencies;
    }



    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }

    public void setAlpha2Code(String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<Currencies> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currencies> currencies) {
        this.currencies = currencies;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
