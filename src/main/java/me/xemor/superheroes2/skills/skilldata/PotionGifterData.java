package me.xemor.superheroes2.skills.skilldata;

import de.themoep.minedown.MineDown;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.configdata.Cooldown;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.configuration.ConfigurationSection;

public class PotionGifterData extends PotionEffectData implements Cooldown {

    private double cooldown;
    private BaseComponent[] receiverMessage;
    private BaseComponent[] giverMessage;
    private String cooldownMessage;

    protected PotionGifterData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        cooldown = configurationSection.getDouble("cooldown", 10);
        receiverMessage = MineDown.parse(configurationSection.getString("receiverMessage", "&7&oYou have received a potion effect."));
        giverMessage = MineDown.parse(configurationSection.getString("giverMessage", "&7&oYou gave them a potion effect."));
        cooldownMessage = configurationSection.getString("cooldownMessage", "&7&oYou have %s seconds left until it can be used again!");
    }

    public BaseComponent[] getGiverMessage() {
        return giverMessage;
    }

    public double getCooldown() {
        return cooldown;
    }

    public BaseComponent[] getReceiverMessage() {
        return receiverMessage;
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }
}
