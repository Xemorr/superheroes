package me.xemor.superheroes.skills.skilldata;

import org.bukkit.configuration.ConfigurationSection;

public class SneakData extends SkillData {

    private final boolean mustSneak;
    private final boolean needsInvisibility;

    public SneakData(int skill, ConfigurationSection configurationSection) {
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
