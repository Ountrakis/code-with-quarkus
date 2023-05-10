package org.acme.services;

import io.smallrye.mutiny.Uni;
import org.acme.model.Country;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/v2/")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "country-api")
public interface CountryService {

    @GET
    @Path("name/{country-name}")
    Uni<List<Country>> getCountryByName(@PathParam("country-name") String countryName, @QueryParam("fulltext") boolean fulltext);

    @GET
    @Path("capital/{capital-name}")
    Uni<List<Country>> getCountryByCapital(@PathParam("capital-name") String capitalName);
}
