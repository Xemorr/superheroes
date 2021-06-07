package me.xemor.superheroes2.skills.conditions;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface BlockCondition {

    boolean isTrue(Player player, Block block);

}
