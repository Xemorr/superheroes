package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class ClimbData extends SkillData {

    private final double proximity;
    private final double climbSpeed;
    private final boolean debug;

    public ClimbData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        double proximity = configurationSection.getDouble("proximity", 0.2);
        this.proximity = proximity * proximity;
        climbSpeed = configurationSection.getDouble("speed", 0.2);
        debug = configurationSection.getBoolean("debug", false);
    }

    public double getProximity() {
        return proximity;
    }

    public double getClimbSpeed() {
        return climbSpeed;
    }

    public boolean isDebug() {
        return debug;
    }
}
