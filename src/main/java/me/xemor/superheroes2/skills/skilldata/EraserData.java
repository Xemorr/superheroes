package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.skilldata.configdata.CooldownData;
import org.bukkit.configuration.ConfigurationSection;

public class EraserData extends CooldownData {

    private double range;
    private String removedMessage;
    private String returnedMessage;
    private int duration;

    public EraserData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection, "You need to wait %currentcooldown% seconds before you can erase their power again!", 10);
        removedMessage = configurationSection.getString("removedMessage", "%player% has had their power erased temporarily!");
        returnedMessage = configurationSection.getString("returnedMessage", "%player% has had their power reinstated!");
        range = configurationSection.getDouble("range", 30);
        duration = (int) Math.floor(configurationSection.getDouble("duration", 7.5D) * 20);
    }

    public double getRange() {
        return range;
    }

    public String getRemovedMessage() {
        return removedMessage;
    }

    public int getDuration() {
        return duration;
    }

    public String getReturnedMessage() {
        return returnedMessage;
    }
}
