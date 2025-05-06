package me.xemor.superheroes.language;

import me.xemor.configurationdata.JsonPropertyWithDefault;

public class ChatLanguageSettings {

    @JsonPropertyWithDefault
    private String gainedHero;
    @JsonPropertyWithDefault
    private String noPermission;
    @JsonPropertyWithDefault
    private String currentHero;
    @JsonPropertyWithDefault
    private String heroCommandCooldown;
    @JsonPropertyWithDefault
    private String invalidHeroMessage;
    @JsonPropertyWithDefault
    private String invalidPlayerMessage;
    @JsonPropertyWithDefault
    private String invalidCommandMessage;
    @JsonPropertyWithDefault
    private String invalidRerollGroupMessage;

    public String getGainedHero() {
        return gainedHero;
    }

    public String getNoPermission() {
        return noPermission;
    }

    public String getCurrentHero() {
        return currentHero;
    }

    public String getHeroCommandCooldown() {
        return heroCommandCooldown;
    }

    public String getInvalidHeroMessage() {
        return invalidHeroMessage;
    }

    public String getInvalidPlayerMessage() {
        return invalidPlayerMessage;
    }

    public String getInvalidCommandMessage() {
        return invalidCommandMessage;
    }

    public String getInvalidRerollGroupMessage() {
        return invalidRerollGroupMessage;
    }
}
