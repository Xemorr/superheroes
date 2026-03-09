package me.xemor.superheroes.language;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.superheroes.Superheroes;

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
        if (gainedHero == null) Superheroes.getInstance().getLogger().severe("GainedHero is not set in the config.yml");
        return gainedHero;
    }

    public String getNoPermission() {
        if (noPermission == null) Superheroes.getInstance().getLogger().severe("noPermission is not set in the config.yml");
        return noPermission;
    }

    public String getCurrentHero() {
        if (currentHero == null) Superheroes.getInstance().getLogger().severe("currentHero is not set in the config.yml");
        return currentHero;
    }

    public String getHeroCommandCooldown() {
        if (heroCommandCooldown == null) Superheroes.getInstance().getLogger().severe("heroCommandCooldown is not set in the config.yml");
        return heroCommandCooldown;
    }

    public String getInvalidHeroMessage() {
        if (invalidHeroMessage == null) Superheroes.getInstance().getLogger().severe("invalidHeroMessage is not set in the config.yml");
        return invalidHeroMessage;
    }

    public String getInvalidPlayerMessage() {
        if (invalidPlayerMessage == null) Superheroes.getInstance().getLogger().severe("invalidPlayerMessage is not set in the config.yml");
        return invalidPlayerMessage;
    }

    public String getInvalidCommandMessage() {
        if (invalidCommandMessage == null) Superheroes.getInstance().getLogger().severe("invalidCommandMessage is not set in the config.yml");
        return invalidCommandMessage;
    }

    public String getInvalidRerollGroupMessage() {
        if (invalidRerollGroupMessage == null) Superheroes.getInstance().getLogger().severe("invalidRerollGroupMessage is not set in the config.yml");
        return invalidRerollGroupMessage;
    }
}
