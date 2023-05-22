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
import java.util.ArrayList;
import java.util.List;

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
        myMongoService.TTL();
        List<Country> mCountryList = new ArrayList<>();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        List<Country> searchList = myMongoService.searchCountries(name, whatImSearching, mongoProperties.getCollectionCountriesCache());
        if (!searchList.isEmpty()) {
            List<Country> Result = new ArrayList<>();
            for (Country country : searchList) {
                if (country.getCapital().contains(name)) {
                    Result.add(country);
                } else if (country.getName().contains(name)) {
                    Result.add(country);
                }
            }
            mCountryList.addAll(Result);
        } else {
            if (whatImSearching.equals("capital")) {
                mCountryList = myCountryRestClient.getCountryByCapital(name).await().indefinitely();
            } else if (whatImSearching.equals("countryName")) {
                mCountryList = myCountryRestClient.getCountryByName(name, fulltext).await().indefinitely();
            }
           myMongoService.addDocument(mCountryList);
        }
        Uni<List<Country>> countryUni = Uni.createFrom().item(mCountryList);
        return jsonFormat.JsonFormatMethod(countryUni);
    }

    public Response PostCountryToKafka(Country country) {
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
        return jsonFormat.JsonFormatMethod(countryUni);
    }

    public Response getAllCountries() {
        return myMongoService.getAllCountries();
    }

//    public Uni<ArrayNode> searchingRestClient1(String whatImSearching, String name, boolean fulltext) {
//        myMongoService.TTL();
//        Uni<List<Country>> mCountryList ;
//        name = name.substring(0, 1).toUpperCase() + name.substring(1);
//        Uni<List<Country>> searchList = myMongoService.searchCountries1(name, whatImSearching, mongoProperties.getCollectionCountriesCache());
//
//        searchList.onItem().transform()
//        if (!searchList.isEmpty()) {
//            List<Country> Result = new ArrayList<>();
//            for (Country country : searchList) {
//                if (country.getCapital().contains(name)) {
//                    Result.add(country);
//                } else if (country.getName().contains(name)) {
//                    Result.add(country);
//                }
//            }
//            mCountryList.addAll(Result);
//        } else {
//            if (whatImSearching.equals("capital")) {
//                mCountryList = myCountryRestClient.getCountryByCapital(name);
//            } else if (whatImSearching.equals("countryName")) {
//                mCountryList = myCountryRestClient.getCountryByName(name, fulltext);
//            }
//            myMongoService.addDocument1(mCountryList);
//        }
//        return jsonFormat.JsonFormatMethod(mCountryList);
//    }
}
