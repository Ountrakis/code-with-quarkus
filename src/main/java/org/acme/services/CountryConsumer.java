package org.acme.services;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.acme.model.Country;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@ApplicationScoped
public class CountryConsumer {
    CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
    MongoClientSettings settings = MongoClientSettings.builder()
            .codecRegistry(fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry))
            .build();
    MongoClient mongoClient = MongoClients.create(settings);

    @Incoming("kafka-countries-in")
    public void consume(Country country) {
        Document document = new Document("Country", country);
        mongoClient
                .getDatabase("Countries")
                .getCollection("KafkaCountries")
                .insertOne(document);


    }

}
