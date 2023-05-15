package org.acme.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix ="quarkus.mongodb" )
public interface MongoProperties {
        @WithName("connection-string")
        String getConnectionString();

        @WithName("database")
        String getDatabase();

        @WithName("countriesCache")
        String getCollectionCountriesCache();

        @WithName("kafkaCountries")
        String getCollectionKafkaCountries();

        @WithName("documentClass")
        String getDocumentClass();


}
