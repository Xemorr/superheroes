package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class EraserData extends SkillData {

    private double range;
    private String message;

    protected EraserData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        message = ChatColor.translateAlternateColorCodes('&', configurationSection.getString("message", ""));
        range = configurationSection.getDouble("range", 30);
    }

    public double getRange() {
        return range;
    }

    public String getMessage() {
        return message;
    }
}
