package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class SlimeData extends SkillData {

    double speedMultiplier;

    public SlimeData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        double multiplier = configurationSection.getDouble("speedMultiplier", 1);
        speedMultiplier = multiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}
