package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class ClimbData extends SkillData {

    private final double proximity;
    private final double climbSpeed;
    private final boolean debug;
    private final SetData<Material> blocks;
    private final boolean whitelist;

    public ClimbData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        double proximity = configurationSection.getDouble("proximity", 0.2);
        this.proximity = proximity * proximity;
        climbSpeed = configurationSection.getDouble("speed", 0.2);
        debug = configurationSection.getBoolean("debug", false);
        blocks = new SetData<>(Material.class, "blocks", configurationSection);
        whitelist = configurationSection.getBoolean("whitelist", true);
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

    public SetData<Material> getBlocks() {
        return blocks;
    }

    public boolean isWhitelist() {
        return whitelist;
    }
}
