package fr.usubelli.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import fr.usubelli.user.adapter.MongoUserRepository;
import org.apache.commons.cli.*;

import java.io.File;

public class RunUserVertx {


    public static void main(String[] args) {

        Options options = new Options();

        Option input = new Option("c", "config", true, "configuration file path");
        input.setRequired(true);
        options.addOption(input);

        Option sslActivated = new Option("ssl", "ssl", true, "ssl is activated");
        sslActivated.setRequired(false);
        options.addOption(sslActivated);

        Option sslKeystore = new Option("sslk", "ssl-keystore", true, "ssl keystore mandatory if ssl is activated");
        sslKeystore.setRequired(false);
        options.addOption(sslKeystore);

        Option sslPassword = new Option("sslp", "ssl-password", true, "ssl password mandatory if ssl is activated");
        sslPassword.setRequired(false);
        options.addOption(sslPassword);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("User Micro Service", options);
            System.exit(1);
            return;
        }

        Config configuration = ConfigFactory.parseFile(new File(cmd.getOptionValue("config"))).resolve();

        final MongoUserRepository mongoUserRepository = new MongoUserRepository(
                configuration.getString("mongo.host"),
                configuration.getString("mongo.database"),
                configuration.getString("mongo.collection"));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final VertxServer vertxServer = VertxServer.create();
        if (configuration.getString("http.auth.type") != null
            && configuration.getString("http.auth.type").equals("basic")) {
            vertxServer
                    .basicAuth(
                            configuration.getString("http.auth.realm"),
                            configuration.getString("http.auth.username"),
                            configuration.getString("http.auth.password"));
        }
        if (Boolean.parseBoolean(cmd.getOptionValue("ssl", String.valueOf(false)))) {
            vertxServer
                    .ssl(cmd.getOptionValue("ssl-keystore", "ssl\\localhost.keystore"),
                            cmd.getOptionValue("ssl-password", "password"));
        }
        vertxServer
                .start(new UserVertx(mongoUserRepository, objectMapper),
                        configuration.getInt("http.port"));

    }

}
