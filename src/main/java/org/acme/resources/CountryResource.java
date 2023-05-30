package org.acme.resources;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import org.acme.model.Country;
import org.acme.services.SearchService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("")
public class CountryResource {
    @Inject
    SearchService searchService;

    //First Question : Getting Country, searching by capital, from collection "CountriesCache"
    @GET
    @Path("capital/{capital-name}")
    public Uni<ArrayNode> getCountryByCapital(@PathParam("capital-name") String capital) {
        Log.info("Searching Countries By Capital");
        return searchService.searchingRestClient("capital", capital,false);
    }

    //Second Question : Getting Country, searching by name, from collection "CountriesCache"
    @GET
    @Path("country/{country-name}")
    public Uni<ArrayNode> getCountryByName(@PathParam("country-name") String countryName, @QueryParam("fulltext") boolean fulltext) {
        Log.info("Searching Countries By Name");
        return searchService.searchingRestClient("countryName", countryName, fulltext);
    }

    //Third Question : Posting Country using Kafka to collection "KafkaCountries"
    @POST
    @Path("kafka")
    public Response postCountryToKafka(Country country) {
        return searchService.postCountryToKafka(country);
    }

    //Fourth Question : Getting Country using Kafka from collection "KafkaCountries"
    @GET
    @Path("kafka/{country-name}")
    public Uni<ArrayNode> getKafka(@PathParam("country-name") String countryName) {
        return searchService.getKafka(countryName);
    }

    //Getting all Countries stored in myMongoDB Collection "CountriesCache"
    @GET
    @Path("countries")
    public Response getAll() {
        return searchService.getAllCountries();
    }


}
