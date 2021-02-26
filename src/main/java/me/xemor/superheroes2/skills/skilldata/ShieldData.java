package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;

public class ShieldData extends SkillData {

    private int cooldown;

    protected ShieldData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        cooldown = (int) Math.round(configurationSection.getDouble("cooldown", 100) * 20);
    }

    public int getCooldown() {
        return cooldown;
    }
}
