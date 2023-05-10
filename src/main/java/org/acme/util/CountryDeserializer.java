package org.acme.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.model.Country;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class CountryDeserializer implements Deserializer<Country> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public Country deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, Country.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing country", e);
        }
    }

    @Override
    public void close() {}
}
