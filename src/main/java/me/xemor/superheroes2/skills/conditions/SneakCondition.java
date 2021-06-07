package me.xemor.superheroes2.skills.conditions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SneakCondition extends Condition implements HeroCondition, TargetCondition {

    private final boolean sneak;

    public SneakCondition(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        sneak = configurationSection.getBoolean("sneak", true);
    }

    @Override
    public boolean isTrue(Player player) {
        return player.isSneaking() == sneak;
    }

    @Override
    public boolean isTrue(Player player, Entity entity) {
        if (entity instanceof Player) {
            Player otherPlayer = (Player) entity;
            return otherPlayer.isSneaking() == sneak;
        }
        return true;
    }
}
