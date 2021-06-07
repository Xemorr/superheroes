package me.xemor.superheroes2.skills.conditions;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public abstract class Condition {

    private final int condition;
    private final ConfigurationSection configurationSection;

    public Condition(int condition, ConfigurationSection configurationSection) {
        this.condition = condition;
        this.configurationSection = configurationSection;
    }

    @Nullable
    public static Condition create(int condition, ConfigurationSection configurationSection) {
        try {
            return Conditions.getClass(condition).getConstructor(int.class, ConfigurationSection.class).newInstance(condition, configurationSection);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ConfigurationSection getData() {
        return configurationSection;
    }

    public int getCondition() {
        return condition;
    }

}
