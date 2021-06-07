package me.xemor.superheroes2.skills.conditions;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class HealthCondition extends Condition implements HeroCondition {

    double minimumHealth;
    double maximumHealth;

    public HealthCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        minimumHealth = configurationSection.getDouble("minimumHealthPercentage", 0) / 100;
        maximumHealth = configurationSection.getDouble("maximumHealthPercentage", 100) / 100;
    }

    @Override
    public boolean isTrue(Player player) {
        double healthPercentage = player.getHealth() / player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        return healthPercentage >= minimumHealth && healthPercentage <= maximumHealth;
    }
}
