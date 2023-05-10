package org.acme.services;

import org.acme.model.Country;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CountryProducer {

    @Inject
    @Channel("kafka-countries-out")
    Emitter<Country> countryEmitter;


    public void produce(Country country) {
        countryEmitter.send(country);
    }


}
