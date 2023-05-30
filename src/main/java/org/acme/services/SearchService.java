package org.acme.services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import org.acme.client.CountryRestClient;
import org.acme.config.MongoProperties;
import org.acme.model.Country;
import org.acme.util.JsonFormat;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;


@ApplicationScoped
public class SearchService {
    @Inject
    @RestClient
    CountryRestClient myCountryRestClient;
    @Inject
    MongoService myMongoService;
    @Inject
    JsonFormat jsonFormat;
    @Inject
    CountryProducer producer;
    @Inject
    CountryConsumer consumer;
    @Inject
    MongoProperties mongoProperties;

    public Uni<ArrayNode> searchingRestClient(String whatImSearching, String name, boolean fulltext) {
        myMongoService.ttl();
        Uni<List<Country>> mCountryList = Uni.createFrom().item(new ArrayList<>());
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        List<Country> searchList = myMongoService.searchCountries(name, whatImSearching, mongoProperties.getCollectionCountriesCache());

        if (searchList.isEmpty()) {
            return jsonFormat.jsonFormatMethod(addToDatabase(mCountryList, whatImSearching, name, fulltext));
        }
        else {
            return jsonFormat.jsonFormatMethod(retrieveFromDatabase(mCountryList, searchList, name));
        }
    }

    public Response postCountryToKafka(Country country) {
        Log.info("Posting country using Kafka");
        List<Country> countries = myCountryRestClient.getCountryByCapital(country.getCapital()).await().indefinitely();
        for (Country mCountry : countries) {
            producer.produce(mCountry);
            consumer.consume(mCountry);
        }
        return Response.ok().build();
    }

    public Uni<ArrayNode> getKafka(@PathParam("country-name") String countryName) {
        Log.info("Getting country using Kafka");
        List<Country> mCountryList = myMongoService.searchCountries(countryName, "Country.name", mongoProperties.getCollectionKafkaCountries());
        Uni<List<Country>> countryUni = Uni.createFrom().item(mCountryList);
        return jsonFormat.jsonFormatMethod(countryUni);
    }

    public Response getAllCountries() {
        return myMongoService.getAllCountries();
    }


    public Uni<List<Country>> addToDatabase(Uni<List<Country>> countryList, String whatImSearching, String name, boolean fulltext) {
        if (whatImSearching.equals("capital")) {
            countryList = myCountryRestClient.getCountryByCapital(name);
        }
        else if (whatImSearching.equals("countryName")) {
            countryList = myCountryRestClient.getCountryByName(name, fulltext);
        }
        countryList.onItem()
                .transformToUni(list ->
                        Uni.combine()
                                .all()
                                .unis(list.stream()
                                        .map(country -> myMongoService.addDocument(country))
                                        .collect(Collectors.toList()))
                                .discardItems()).subscribe().with(ef -> {
                });
        return countryList;
    }

    public Uni<List<Country>> retrieveFromDatabase(Uni<List<Country>> countryList, List<Country> searchList, String name) {
        List<Country> result = new ArrayList<>();
        for (Country country : searchList) {
            if (country.getCapital().contains(name) || country.getName().contains(name)) {
                result.add(country);
            }
        }
        countryList.onItem().transform(list -> {
            list.addAll(result);
            return list;
        }).subscribe().with(ew -> {
        });
        return countryList;
    }

}
