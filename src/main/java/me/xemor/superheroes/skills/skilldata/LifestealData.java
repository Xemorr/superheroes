package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class LifestealData extends SkillData {

    private final double lifesteal;

    public LifestealData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        lifesteal = configurationSection.getDouble("lifestealPercentage", 5) / 100;
    }

    public double getLifesteal() {
        return lifesteal;
    }
}
