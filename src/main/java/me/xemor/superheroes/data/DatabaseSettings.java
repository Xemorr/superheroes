package me.xemor.superheroes.data;

import me.xemor.configurationdata.JsonPropertyWithDefault;

public class DatabaseSettings {

    @JsonPropertyWithDefault
    private String type = "YAML";
    @JsonPropertyWithDefault
    private String host = "this needs filling with your host name if using mysql";
    @JsonPropertyWithDefault
    private int port = 3306;
    @JsonPropertyWithDefault
    private String name = "this needs filling with the database name if using mysql";
    @JsonPropertyWithDefault
    private String username = "this needs filling with the username of the account being used to connect";
    @JsonPropertyWithDefault
    private String password = "this needs filling with the password being used to connect";

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
