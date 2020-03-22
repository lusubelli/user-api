package fr.usubelli.common;

import org.apache.commons.cli.CommandLine;

public class VertxConfiguration {

    private final CommandLine command;

    VertxConfiguration(CommandLine command) {
        this.command = command;
    }

    public String getConfigPath() {
        return command.getOptionValue("config", "src/main/config/application.conf");
    }

    public String getAuthHtpasswdPath() {
        return command.getOptionValue("auth-htpasswd-path", "src/main/resources/.htpasswd");
    }

    public String getAuthHtpasswdRealm() {
        return command.getOptionValue("auth-htpasswd-realm", "accounting.api");
    }

    public String getSslKeystorePath() {
        return command.getOptionValue("ssl-keystore", null);
    }

    public String getSslPassword() {
        return command.getOptionValue("ssl-password", null);
    }

    public int getPort() {
        int port;
        try {
            port = Integer.parseInt(command.getOptionValue("port", String.valueOf(8585)));
        } catch (NumberFormatException e) {
            port = 8585;
        }
        return port;
    }
}
