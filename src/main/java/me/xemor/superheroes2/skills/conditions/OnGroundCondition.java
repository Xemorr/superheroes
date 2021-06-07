package me.xemor.superheroes2.skills.conditions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class OnGroundCondition extends Condition implements HeroCondition, TargetCondition {

    private boolean grounded;

    public OnGroundCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        grounded = configurationSection.getBoolean("grounded", true);
    }

    @Override
    public boolean isTrue(Player player) {
        return isOnGround(player) == grounded;
    }

    @Override
    public boolean isTrue(Player player, Entity entity) {
        return isOnGround(entity) == grounded;
    }

    public boolean isOnGround(Entity entity) {
        Location location = entity.getLocation();
        Block block = location.getBlock();
        return !block.getRelative(BlockFace.DOWN).isPassable();
    }
}
