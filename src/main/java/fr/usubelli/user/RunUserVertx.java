package fr.usubelli.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.usubelli.common.*;
import fr.usubelli.user.adapter.MongoUserRepository;
import org.apache.commons.cli.ParseException;

public class RunUserVertx {

    private static final String DEFAULT_APPLICATION_CONF = "src/main/resources/application.conf";

    public static void main(String[] args) {

        Configuration configuration;
        try {
            configuration = MicroServiceCommand.parse(args, DEFAULT_APPLICATION_CONF);
        } catch (ParseException e) {
            System.exit(1);
            return;
        }

        if (configuration == null) {
            System.exit(1);
            return;
        }

        ObjectMapper serverObjectMapper = new ObjectMapper();
        serverObjectMapper.registerModule(new JavaTimeModule());
        serverObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final VertxMicroService microService = new UserVertx(
                new MongoUserRepository(new MongoConfig(configuration.getConfiguration("mongo", null))), serverObjectMapper);

        VertxServer.create(
                new MicroServiceConfiguration(configuration.getInt("http.port", 8080))
                        .basic(configuration.getConfiguration("http.basic", null))
                        .ssl(configuration.getConfiguration("http.ssl", null))
                        .jwt(configuration.getConfiguration("http.jwt", null)))
                .start(microService);



    }


}
