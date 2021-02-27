package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.configdata.CooldownData;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class EraserData extends CooldownData {

    private double range;
    private String message;
    private int duration;

    protected EraserData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection, "You need to wait %s seconds before you can erase their power again!", 10);
        message = ChatColor.translateAlternateColorCodes('&', configurationSection.getString("message", ""));
        range = configurationSection.getDouble("range", 30);
        duration = (int) Math.floor(configurationSection.getDouble("duration", 7.5D) * 20);
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
}
