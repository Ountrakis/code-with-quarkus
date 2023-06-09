package org.acme.util;

import org.acme.config.KafkaProperties;
import org.acme.config.MongoProperties;
import org.acme.model.Country;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.Properties;

@ApplicationScoped
public class KafkaConfiguration {
    @Inject
    MongoProperties mongoProperties;
    @Inject
    KafkaProperties kafkaProperties;

    @Produces
    public KafkaProducer<String, Country> countryProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CountrySerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    @Produces
    public KafkaConsumer<String, Country> countryConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumerGroup());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getAutoOffset());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CountryDeserializer.class.getName());
        KafkaConsumer<String, Country> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(mongoProperties.getCollectionKafkaCountries()));
        return consumer;
    }
}