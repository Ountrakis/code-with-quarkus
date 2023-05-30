package org.acme.client;

import io.smallrye.mutiny.Uni;
import org.acme.model.Country;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/v2/")
@RegisterRestClient(configKey = "country-api")
public interface CountryRestClient {

    @GET
    @Path("name/{country-name}")
    Uni<List<Country>> getCountryByName(@PathParam("country-name") String countryName, @QueryParam("fulltext") boolean fulltext);

    @GET
    @Path("capital/{capital-name}")
    Uni<List<Country>> getCountryByCapital(@PathParam("capital-name") String capitalName);
}
