package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class EraserData extends SkillData {

    private double range;
    private String message;
    private int duration;
    private double cooldown;


    protected EraserData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        message = ChatColor.translateAlternateColorCodes('&', configurationSection.getString("message", ""));
        range = configurationSection.getDouble("range", 30);
        duration = (int) Math.floor(configurationSection.getDouble("duration", 7.5D) * 20);
        cooldown = configurationSection.getDouble("cooldown", 10D);
    }

    public double getRange() {
        return range;
    }

    public String getMessage() {
        return message;
    }

    public int getDuration() {
        return duration;
    }

    public double getCooldown() {
        return cooldown;
    }
}
