package me.xemor.superheroes2.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class WeatherDamageData extends SkillData {

    private double damage;
    private boolean checkShelter;

    public WeatherDamageData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        this.damage = configurationSection.getDouble("damage", 1.0);
        this.checkShelter = configurationSection.getBoolean("checkShelter", true);
    }

    public double getDamage() {
        return damage;
    }

    public boolean checkShelter() {
        return checkShelter;
    }
}
