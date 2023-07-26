package me.xemor.superheroes.skills.skilldata;

import me.xemor.superheroes.skills.skilldata.configdata.Cooldown;
import org.bukkit.configuration.ConfigurationSection;

public class PotionGifterSkillData extends PotionEffectSkillData implements Cooldown {

    private final double cooldown;
    private final String receiverMessage;
    private final String giverMessage;
    private final String cooldownMessage;

    public PotionGifterSkillData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        cooldown = configurationSection.getDouble("cooldown", 10);
        receiverMessage = configurationSection.getString("receiverMessage", "<grey><i>You have received a potion effect.");
        giverMessage = configurationSection.getString("giverMessage", "<grey><i>You gave them a potion effect.");
        cooldownMessage = configurationSection.getString("cooldownMessage", "<grey><i>You have <currentcooldown> seconds left until it can be used again!");
    }

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
