package me.xemor.superheroes2.skills.conditions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class GlidingCondition extends Condition implements HeroCondition {

    private final boolean shouldGlide;

    public GlidingCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        shouldGlide = configurationSection.getBoolean("shouldGlide", true);
    }

    @Override
    public boolean isTrue(Player player) {
        return player.isGliding() == shouldGlide;
    }
}
