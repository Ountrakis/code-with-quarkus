package org.acme.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.smallrye.mutiny.Uni;
import org.acme.model.Country;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class JsonFormat {
    ObjectMapper mapper = new ObjectMapper();
    public Uni<ArrayNode> JsonFormatMethod(Uni<List<Country>> countryUni) {
        return Uni.createFrom().emitter(emitter -> countryUni.subscribe().with(
                response -> {
                    ArrayNode countryArrayNode = mapper.createArrayNode();
                    List<Country> countryList = response.stream().toList();

                    for (Country country : countryList) {
                        ObjectNode countryNode = mapper.createObjectNode();
                        countryNode.put("countryName", country.getName());
                        countryNode.put("countryCode", country.getAlpha2Code());
                        countryNode.put("capital", country.getCapital());
                        countryNode.put("continent", country.getRegion());
                        countryNode.put("officialLanguage", country.getLanguages().get(0).getName());
                        countryNode.put("currencyName", country.getCurrencies().get(0).getName());
                        countryArrayNode.add(countryNode);
                    }
                    emitter.complete(countryArrayNode);
                },
                emitter::fail
        ));
    }
}
