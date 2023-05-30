package org.acme.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.smallrye.mutiny.Uni;
import org.acme.model.Country;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class JsonFormat {
    ObjectMapper mapper = new ObjectMapper();

    public Uni<ArrayNode> jsonFormatMethod(Uni<List<Country>> countryUni) {
        return Uni.createFrom().emitter(emitter -> countryUni.subscribe().with(
                response -> {
                    ArrayNode countryArrayNode = mapper.createArrayNode();
                    List<Country> countryList = response.stream().toList();
                    for (Country country : countryList) {
                        ObjectNode countryNode = mapper.createObjectNode();
                        Optional.ofNullable(countryNode)
                                .stream()
                                .filter(Objects::nonNull)
                                .findFirst()
                                .ifPresent(mCountry -> {
                                    countryNode.put("countryName", country.getName());
                                    countryNode.put("countryCode", country.getAlpha2Code());
                                    countryNode.put("capital", country.getCapital());
                                    countryNode.put("continent", country.getRegion());
                                    Optional.ofNullable(country.getLanguages())
                                            .stream()
                                            .flatMap(Collection::stream)
                                            .filter(Objects::nonNull)
                                            .findFirst()
                                            .ifPresent(language ->
                                                    countryNode.put("officialLanguage", language.getName()));
                                    Optional.ofNullable(country.getCurrencies())
                                            .stream()
                                            .flatMap(Collection::stream)
                                            .filter(Objects::nonNull)
                                            .findFirst()
                                            .ifPresent(currencies ->
                                                    countryNode.put("currencyName", currencies.getName()));
                                });
                        countryArrayNode.add(countryNode);
                    }
                    emitter.complete(countryArrayNode);
                },
                emitter::fail
        ));
    }
}
