package fr.usubelli.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import fr.usubelli.common.VertxCommand;
import fr.usubelli.common.VertxConfiguration;
import fr.usubelli.common.VertxServer;
import fr.usubelli.user.adapter.MongoUserRepository;
import org.apache.commons.cli.*;

import java.io.File;

public class RunUserVertx {


    public static void main(String[] args) {

        VertxConfiguration vertxConfiguration;
        try {
            vertxConfiguration = new VertxCommand().parse(args);
        } catch (ParseException e) {
            System.exit(1);
            return;
        }

        System.out.println(String.format("Loading config from %s", vertxConfiguration.getConfigPath()));
        System.out.println(String.format("HTTPS : %s", vertxConfiguration.getSslKeystorePath() != null));
        System.out.println(String.format("HTPASSWD : %s", vertxConfiguration.getAuthHtpasswdPath() != null));

        Config configuration = ConfigFactory.empty();
        final File configFile = new File(vertxConfiguration.getConfigPath());
        if (configFile.exists()) {
            configuration = ConfigFactory.parseFile(configFile).resolve();
        }

        final MongoUserRepository mongoUserRepository = new MongoUserRepository(new MongoConfig(configuration.getConfig("mongo")));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final VertxServer vertxServer = VertxServer.create();
        vertxServer.htpasswd(vertxConfiguration.getAuthHtpasswdRealm(), vertxConfiguration.getAuthHtpasswdPath());
        if (vertxConfiguration.getSslKeystorePath() != null) {
            vertxServer.ssl(vertxConfiguration.getSslKeystorePath(), vertxConfiguration.getSslPassword());
        }
        vertxServer.start(new UserVertx(mongoUserRepository, objectMapper), vertxConfiguration.getPort());

    }


}
