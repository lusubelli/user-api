package fr.usubelli.user;

import fr.usubelli.common.Configuration;

public class MongoConfig {

    private static final String HOST = "host";
    private static final String DATABASE = "database";
    private static final String COLLECTION = "collection";

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_DATABASE = "accounting";
    private static final String DEFAULT_COLLECTION = "user";

    private final Configuration configuration;

    public MongoConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getHost() {
        return configuration.getString(HOST, DEFAULT_HOST);
    }

    public String getDatabase() {
        return configuration.getString(DATABASE, DEFAULT_DATABASE);
    }

    public String getCollection() {
        return configuration.getString(COLLECTION, DEFAULT_COLLECTION);
    }
}
