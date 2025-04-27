package me.xemor.superheroes.skills.skilldata.configdata;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.configuration.ConfigurationSection;

public abstract class CooldownData extends SkillData implements Cooldown {

    @JsonPropertyWithDefault
    private double cooldown = 0;
    @JsonPropertyWithDefault
    private String cooldownMessage = "You have <currentcooldown> seconds remaining before you can use the ability again!";

    @Override
    public double getCooldown() {
        return cooldown;
    }

    @Override
    public String getCooldownMessage() {
        return cooldownMessage;
    }

}
