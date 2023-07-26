package me.xemor.superheroes.skills.skilldata;

import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.configuration.ConfigurationSection;

public class CreeperData extends CooldownData {

    private final int fuse;
    private final float creeperPower;
    private final double upwardsVelocity;
    private final int slowfallDuration;

    public CreeperData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection, "<dark_green><bold>Creeper <white>Cooldown: <s> seconds", 10);
        creeperPower = (float) configurationSection.getDouble("creeper_power", 1);
        fuse = (int) Math.ceil(configurationSection.getDouble("fuse", 2) * 20);
        slowfallDuration = (int) Math.ceil(configurationSection.getDouble("slowfall_duration", 7) * 20);
        upwardsVelocity = configurationSection.getDouble("upwardsVelocity", 2.5);
    }

    public int getFuse() {
        return fuse;
    }

    public float getCreeperPower() {
        return creeperPower;
    }

    public double getUpwardsVelocity() {
        return upwardsVelocity;
    }

    public int getSlowfallDuration() {
        return slowfallDuration;
    }
}
