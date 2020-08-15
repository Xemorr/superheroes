package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;

public class SlimeData extends SkillData {

    double speedMultiplier = 1;

    protected SlimeData(Skill skill, ConfigurationSection configurationSection) {
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
