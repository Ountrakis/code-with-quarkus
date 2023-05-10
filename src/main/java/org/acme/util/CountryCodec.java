package org.acme.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.model.Country;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;

import java.io.IOException;

public class CountryCodec implements Codec<Country> {
    private final ObjectMapper mapper;

    public CountryCodec(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Country decode(BsonReader reader, DecoderContext decoderContext) {
        try {
            String json = reader.readString();
            return mapper.readValue(json, Country.class);
        } catch (IOException e) {
            throw new CodecConfigurationException("Failed to decode Country", e);
        }
    }

    @Override
    public void encode(BsonWriter writer, Country value, EncoderContext encoderContext) {
        try {
            String json = mapper.writeValueAsString(value);
            writer.writeString(json);
        } catch (JsonProcessingException e) {
            throw new CodecConfigurationException("Failed to encode Country", e);
        }
    }

    @Override
    public Class<Country> getEncoderClass() {
        return Country.class;
    }
}
