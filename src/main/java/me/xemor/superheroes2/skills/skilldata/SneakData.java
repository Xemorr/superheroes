package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;

public class SneakData extends SkillData {

    private boolean mustSneak;
    private boolean needsInvisibility;

    protected SneakData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        mustSneak = configurationSection.getBoolean("mustSneak", true);
        needsInvisibility = configurationSection.getBoolean("needsInvisibility", false);
    }

    public boolean mustSneak() {
        return mustSneak;
    }

    public boolean needsInvisibility() {
        return needsInvisibility;
    }
}
