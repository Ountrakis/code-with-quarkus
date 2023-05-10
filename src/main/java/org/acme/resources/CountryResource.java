package org.acme.resources;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import org.acme.model.Country;
import org.acme.services.SearchService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("")
public class CountryResource {
    @Inject
    SearchService searchService;

    //First Question
    @GET
    @Path("capital/{capital-name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<ArrayNode> getCountryByCapital(@PathParam("capital-name") String capital) {
        Log.info("Searching Countries By Capital");
        return searchService.searchingRestClient("capital", capital, false);
    }
    //Second Question
    @GET
    @Path("country/{country-name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<ArrayNode> getCountryByName(@PathParam("country-name") String countryName, @QueryParam("fulltext") boolean fulltext) {
        Log.info("Searching Countries By Name");
        return searchService.searchingRestClient("countryName", countryName, fulltext);
    }
    //Third Question
    @POST
    @Path("kafka")
    @Produces(MediaType.APPLICATION_JSON)
    public Response PostCountryToKafka(Country country) {
        return searchService.PostCountryToKafka(country);
    }
    //Fourth Question
    @GET
    @Path("kafka/{country-name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<ArrayNode> getKafka(@PathParam("country-name") String countryName) {
        return searchService.getKafka(countryName);
    }
    @GET
    @Path("countries")
    public Response getAll() {
        return searchService.getAllCountries();
    }


}
