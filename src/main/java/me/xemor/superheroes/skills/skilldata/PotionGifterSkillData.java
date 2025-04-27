package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.superheroes.skills.skilldata.configdata.Cooldown;
import org.bukkit.configuration.ConfigurationSection;

public class PotionGifterSkillData extends PotionEffectSkillData implements Cooldown {

    @JsonPropertyWithDefault
    private double cooldown = 10;
    @JsonPropertyWithDefault
    private String receiverMessage = "<grey><i>You have received a potion effect.";
    @JsonPropertyWithDefault
    private String giverMessage = "<grey><i>You gave them a potion effect.";
    @JsonPropertyWithDefault
    private String cooldownMessage = "<grey><i>You have <currentcooldown> seconds left until it can be used again!";

    public String getGiverMessage() {
        return giverMessage;
    }

    public double getCooldown() {
        return cooldown;
    }

    public String getReceiverMessage() {
        return receiverMessage;
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }
}
