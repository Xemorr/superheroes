package me.xemor.superheroes2.skills.skilldata.configdata;

import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.configuration.ConfigurationSection;

public abstract class CooldownData extends SkillData implements Cooldown {

    private final double cooldown;
    private final String cooldownMessage;

    protected CooldownData(Skill skill, ConfigurationSection configurationSection, String defaultCooldownMessage, int defaultCooldown) {
        super(skill, configurationSection);
        cooldown = configurationSection.getDouble("cooldown", defaultCooldown);
        cooldownMessage = configurationSection.getString("cooldownMessage", defaultCooldownMessage);
    }

    @Override
    public double getCooldown() {
        return cooldown;
    }

    @Override
    public String getCooldownMessage() {
        return cooldownMessage;
    }

}
