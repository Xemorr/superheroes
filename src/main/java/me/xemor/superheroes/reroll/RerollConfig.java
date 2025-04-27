package me.xemor.superheroes.reroll;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;

import java.util.HashMap;
import java.util.Map;

public class RerollConfig {

    @JsonPropertyWithDefault
    @JsonAlias("config_version")
    private String configVersion = "";

    @JsonPropertyWithDefault
    @JsonAlias("global_reroll_settings")
    private GlobalRerollSettings globalRerollSettings = new GlobalRerollSettings();

    @JsonPropertyWithDefault
    @JsonAlias("reroll_groups")
    private Map<String, RerollGroup> rerollGroups = new HashMap<>();

    public GlobalRerollSettings getGlobalRerollSettings() {
        return globalRerollSettings;
    }

    public Map<String, RerollGroup> getRerollGroups() {
        return rerollGroups;
    }

    public Map<String, RerollGroup> rerollGroups() {
        if (!this.rerollGroups.containsKey("default")) {
            RerollGroup defaultGroup = new RerollGroup();
            this.rerollGroups.put("default", defaultGroup);
        }
        return rerollGroups;
    }

}
