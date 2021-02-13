package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class CreeperData extends SkillData {

    private int fuse;
    private float creeperPower;
    private double cooldown;
    private double upwardsVelocity;
    private int slowfallDuration;
    private String cooldownMessage;

    protected CreeperData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        cooldown = configurationSection.getDouble("cooldown", 10);
        creeperPower = (float) configurationSection.getDouble("creeper_power", 1);
        fuse = (int) Math.ceil(configurationSection.getDouble("fuse", 2) * 20);
        slowfallDuration = (int) Math.ceil(configurationSection.getDouble("slowfall_duration", 7) * 20);
        upwardsVelocity = configurationSection.getDouble("upwardsVelocity", 2.5);
        cooldownMessage = ChatColor.translateAlternateColorCodes('&', configurationSection.getString("cooldownMessage", "&2&lCreeper &fCooldown: %s seconds"));
    }

    public int getFuse() {
        return fuse;
    }

    public float getCreeperPower() {
        return creeperPower;
    }

    public double getCooldown() {
        return cooldown;
    }

    public double getUpwardsVelocity() {
        return upwardsVelocity;
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }

    public int getSlowfallDuration() {
        return slowfallDuration;
    }
}
