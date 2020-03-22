package fr.usubelli.user;

import com.typesafe.config.Config;
import fr.usubelli.common.Configuration;

public class MongoConfig extends Configuration {

    private static final String HOST = "host";
    private static final String DATABASE = "database";
    private static final String COLLECTION = "collection";

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_DATABASE = "accounting";
    private static final String DEFAULT_COLLECTION = "user";

    public MongoConfig(Config config) {
        super(config);
    }

    public String getHost() {
        return getString(HOST, DEFAULT_HOST);
    }

    public String getDatabase() {
        return getString(DATABASE, DEFAULT_DATABASE);
    }

    public String getCollection() {
        return getString(COLLECTION, DEFAULT_COLLECTION);
    }
}
