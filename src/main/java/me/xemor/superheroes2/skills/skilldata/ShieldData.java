package me.xemor.superheroes2.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class ShieldData extends SkillData {

    private int cooldown;

    public ShieldData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        cooldown = (int) Math.round(configurationSection.getDouble("cooldown", 100) * 20);
    }

    public int getCooldown() {
        return cooldown;
    }
}
