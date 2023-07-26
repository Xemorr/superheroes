package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class SlimeData extends SkillData {

    final double speedMultiplier;

    public SlimeData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        speedMultiplier = configurationSection.getDouble("speedMultiplier", 1);
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}
