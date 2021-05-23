package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public abstract class SkillData {

    private final int skill;
    private ConfigurationSection configurationSection;

    public SkillData(int skill, ConfigurationSection configurationSection) {
        this.skill = skill;
        this.configurationSection = configurationSection;
    }

    @Nullable
    public static SkillData create(int skill, ConfigurationSection configurationSection) {
        try {
            return Skill.getClass(skill).getConstructor(int.class, ConfigurationSection.class).newInstance(skill, configurationSection);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ConfigurationSection getData() {
        return configurationSection;
    }

    public int getSkill() {
        return skill;
    }

}
