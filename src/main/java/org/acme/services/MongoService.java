package org.acme.services;

import com.mongodb.client.*;
import com.mongodb.client.model.IndexOptions;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.acme.config.MongoProperties;
import org.acme.model.Country;
import org.acme.model.Currencies;
import org.acme.model.Language;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@ApplicationScoped
public class MongoService {

    @Inject
    MongoProperties mongoProperties;
    MongoClient mongoClient;
    MongoDatabase database;
    @PostConstruct
    void init(){
         mongoClient= MongoClients.create(mongoProperties.getConnectionString());
         database= mongoClient.getDatabase(mongoProperties.getDatabase());
    }

    public  Uni<Void> ttl() {
        database.getCollection(mongoProperties.getCollectionCountriesCache())
                .createIndex(new Document("createdAt", 1), new IndexOptions().expireAfter(10L, TimeUnit.MINUTES));
        return Uni.createFrom().nullItem();
    }

    public List<Country> searchCountries(String name, String whatAmISearching, String searchingCollection) {
        MongoCollection<Document> collection = database.getCollection(searchingCollection);
        List<Country> countryList = new ArrayList<>();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        Document query;
        query = new Document(whatAmISearching, pattern);

        List<Document> results = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(query);
        for (Document result : findIterable) {
            results.add(result);
        }

        for (Document result : results) {
            if (result != null)
            {
                Country country = new Country();
                if (searchingCollection.equals(mongoProperties.getCollectionKafkaCountries()))
                {
                    country.setName(result.get(mongoProperties.getDocumentClass(), Document.class).getString("name"));
                    country.setAlpha2Code(result.get(mongoProperties.getDocumentClass(), Document.class).getString("alpha2Code"));
                    country.setCapital(result.get(mongoProperties.getDocumentClass(), Document.class).getString("capital"));
                    country.setRegion(result.get(mongoProperties.getDocumentClass(), Document.class).getString("region"));
                    country.setLanguages(result.get(mongoProperties.getDocumentClass(), Document.class)
                            .getList("languages", Document.class)
                            .stream()
                            .map(doc -> new Language(doc.getString("name")))
                            .collect(Collectors.toList()));
                    country.setCurrencies(result.get(mongoProperties.getDocumentClass(), Document.class)
                            .getList("currencies", Document.class)
                            .stream()
                            .map(doc -> new Currencies(doc.getString("name")))
                            .collect(Collectors.toList()));
                }
                else
                {
                    country.setName(result.getString("countryName"));
                    country.setAlpha2Code(result.getString("countryCode"));
                    country.setCapital(result.getString("capital"));
                    country.setRegion(result.getString("continent"));
                    Language language = new Language();
                    List<Language> languageList = new ArrayList<>();
                    language.setName(result.getString("officialLanguage"));
                    languageList.add(language);
                    country.setLanguages(languageList);
                    Currencies currencies = new Currencies();
                    List<Currencies> currenciesList = new ArrayList<>();
                    currencies.setName(result.getString("currencyName"));
                    currenciesList.add(currencies);
                    country.setCurrencies(currenciesList);
                }
                countryList.add(country);
            }
        }
        return countryList;
    }

    public Uni<Void> addDocument(Country country) {
        MongoCollection<Document> collection = database.getCollection(mongoProperties.getCollectionCountriesCache());
        Document countryDoc = new Document();
        Optional.ofNullable(countryDoc)
                .stream()
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(mCountry -> {
                    countryDoc.append("countryName", country.getName());
                    countryDoc.append("countryCode", country.getAlpha2Code());
                    countryDoc.append("capital", country.getCapital());
                    countryDoc.append("continent", country.getRegion());
                    Optional.ofNullable(country.getLanguages())
                            .stream()
                            .flatMap(Collection::stream)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .ifPresent(language ->
                                    countryDoc.append("officialLanguage", language.getName()));
                    Optional.ofNullable(country.getCurrencies())
                            .stream()
                            .flatMap(Collection::stream)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .ifPresent(currencies ->
                                    countryDoc.append("currencyName", currencies.getName()));
                });
        collection.insertOne(countryDoc);
        return Uni.createFrom().nullItem();

    }

    public Uni<Response> getAllCountries() {
        return Uni.createFrom().item(() -> {
            List<Document> results = new ArrayList<>();
            MongoCollection<Document> collection = database.getCollection(mongoProperties.getCollectionCountriesCache());
            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                while (cursor.hasNext()) {
                    Document result = cursor.next();
                    results.add(result);
                }
            }
            return Response.ok(results).build();
        }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

}
