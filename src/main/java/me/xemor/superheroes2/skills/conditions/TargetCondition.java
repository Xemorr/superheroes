package me.xemor.superheroes2.skills.conditions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface TargetCondition {

    boolean isTrue(Player player, Entity entity);

}
