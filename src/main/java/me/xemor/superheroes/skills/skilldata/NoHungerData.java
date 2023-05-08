package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class NoHungerData extends SkillData {

    private final double minimumHunger;

    public NoHungerData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        minimumHunger = configurationSection.getDouble("minimumHunger", 19);
    }

    public double getMinimumHunger() {
        return minimumHunger;
    }
}
