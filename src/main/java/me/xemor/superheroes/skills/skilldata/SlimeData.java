package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class SlimeData extends SkillData {

    double speedMultiplier = 1;

    public SlimeData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        Double multiplier = configurationSection.getDouble("speedMultiplier");
        if (multiplier != null) {
            speedMultiplier = multiplier;
        }
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}
