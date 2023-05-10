package org.acme.services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
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
    CountryService myCountryService;
    @Inject
    MongoService myMongoService;
    @Inject
    JsonFormat jsonFormat;
    @Inject
    CountryProducer producer;
    @Inject
    CountryConsumer consumer;
    public Uni<ArrayNode> searchingRestClient(String whatImSearching, String name, boolean fulltext) {
        myMongoService.TTL();
        List<Country> mCountryList = new ArrayList<>();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        List<Country> searchList = myMongoService.searchCountries(name,whatImSearching,"CountriesCache");

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
        }
        else {
            if (whatImSearching.equals("capital")) {
                mCountryList = myCountryService.getCountryByCapital(name).await().indefinitely();

            } else if (whatImSearching.equals("countryName")) {
                mCountryList = myCountryService.getCountryByName(name, fulltext).await().indefinitely();
            }
            myMongoService.addDocument(mCountryList);
        }
        Uni<List<Country>> countryUni = Uni.createFrom().item(mCountryList);

        return jsonFormat.JsonFormatMethod(countryUni);
    }
    public Response PostCountryToKafka(Country country) {
        Log.info("Posting country to Kafka");
        List<Country> countries = myCountryService.getCountryByCapital(country.getCapital()).await().indefinitely();
        for (Country mCountry : countries) {
            producer.produce(mCountry);
            consumer.consume(mCountry);
        }
        return Response.ok().build();
    }
    public Uni<ArrayNode> getKafka(@PathParam("country-name") String countryName) {
        Log.info("Getting country using Kafka");
        List<Country> mCountryList = myMongoService.searchCountries(countryName,"Country.name","KafkaCountries");
        Uni<List<Country>> countryUni = Uni.createFrom().item(mCountryList);
        return jsonFormat.JsonFormatMethod(countryUni);
    }
    public Response getAllCountries() {
        return myMongoService.getAllCountries();
    }
}
