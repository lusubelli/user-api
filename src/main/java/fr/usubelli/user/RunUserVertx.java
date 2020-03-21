package fr.usubelli.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import fr.usubelli.user.adapter.MongoUserRepository;
import org.apache.commons.cli.*;

import java.io.File;

public class RunUserVertx {


    public static void main(String[] args) {

        Options options = new Options();

        Option input = new Option("c", "config", true, "configuration file path");
        input.setRequired(false);
        options.addOption(input);

        Option port = new Option("p", "port", true, "port");
        port.setRequired(false);
        options.addOption(port);

        Option authHtpasswdPath = new Option("authhtp", "auth-htpasswd-path", true, "auth htpasswd path");
        authHtpasswdPath.setRequired(false);
        options.addOption(authHtpasswdPath);

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

        Config configuration = ConfigFactory.empty();
        if (cmd.getOptionValue("config") != null) {
            final File configFile = new File(cmd.getOptionValue("config"));
            if (configFile.exists()) {
                configuration = ConfigFactory.parseFile(configFile).resolve();
            }
        }


        final MongoUserRepository mongoUserRepository = new MongoUserRepository(
                getString(configuration, "mongo.host", "localhost"),
                getString(configuration, "mongo.database", "accounting"),
                getString(configuration, "mongo.collection", "user"));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final VertxServer vertxServer = VertxServer.create();
        if (cmd.getOptionValue("auth-htpasswd-path") != null) {
            vertxServer
                    .htpasswd(
                            cmd.getOptionValue("auth-htpasswd-realm", "accounting.user.api"),
                            cmd.getOptionValue("auth-htpasswd-path", "src/main/resources/.htpasswd"));
        }
        if (Boolean.parseBoolean(cmd.getOptionValue("ssl", String.valueOf(false)))) {
            vertxServer
                    .ssl(cmd.getOptionValue("ssl-keystore", "ssl\\localhost.keystore"),
                            cmd.getOptionValue("ssl-password", "password"));
        }
        vertxServer
                .start(new UserVertx(mongoUserRepository, objectMapper),
                        Integer.parseInt(cmd.getOptionValue("port", "8585")));

    }

    private static String getString(final Config configuration, final String key, final String defaultValue) {
        String value;
        try {
            value = configuration.getString(key);
        } catch (ConfigException.Missing e) {
            value = defaultValue;
        }
        return value;
    }

}
