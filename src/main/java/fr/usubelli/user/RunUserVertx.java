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

        Option portOption = new Option("p", "port", true, "port");
        portOption.setRequired(false);
        options.addOption(portOption);

        Option authHtpasswdPath = new Option("authhtp", "auth-htpasswd-path", true, "auth htpasswd path");
        authHtpasswdPath.setRequired(false);
        options.addOption(authHtpasswdPath);

        Option sslActivatedOption = new Option("ssl", "ssl", true, "ssl is activated");
        sslActivatedOption.setRequired(false);
        options.addOption(sslActivatedOption);

        Option sslKeystoreOption = new Option("sslk", "ssl-keystore", true, "ssl keystore mandatory if ssl is activated");
        sslKeystoreOption.setRequired(false);
        options.addOption(sslKeystoreOption);

        Option sslPasswordOption = new Option("sslp", "ssl-password", true, "ssl password mandatory if ssl is activated");
        sslPasswordOption.setRequired(false);
        options.addOption(sslPasswordOption);

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

        final String configPath = cmd.getOptionValue("config", "src/main/config/application.conf");
        final String authHtPasswdPath = cmd.getOptionValue("auth-htpasswd-path", "src/main/resources/.htpasswd");
        final String authHtpasswdRealm = cmd.getOptionValue("auth-htpasswd-realm", "accounting.user.api");
        final boolean sslActivated = Boolean.parseBoolean(cmd.getOptionValue("ssl", String.valueOf(false)));
        final String sslKeystore = cmd.getOptionValue("ssl-keystore", "ssl\\localhost.keystore");
        final String sslPassword = cmd.getOptionValue("ssl-password", "password");
        final int port = Integer.parseInt(cmd.getOptionValue("port", "8585"));

        System.out.println(String.format("Loading config from %s", configPath));
        Config configuration = ConfigFactory.empty();
        final File configFile = new File(configPath);
        if (configFile.exists()) {
            configuration = ConfigFactory.parseFile(configFile).resolve();
        }

        final MongoUserRepository mongoUserRepository = new MongoUserRepository(
                getString(configuration, "mongo.host", "localhost"),
                getString(configuration, "mongo.database", "accounting"),
                getString(configuration, "mongo.collection", "user"));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final VertxServer vertxServer = VertxServer.create();
        if (authHtPasswdPath != null) {
            vertxServer.htpasswd(authHtpasswdRealm, authHtPasswdPath);
        }
        if (sslActivated) {
            vertxServer.ssl(sslKeystore, sslPassword);
        }
        vertxServer.start(new UserVertx(mongoUserRepository, objectMapper), port);

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
