package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.skills.Skill;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.RayTraceResult;

public class BeastControlSkill extends Skill implements Listener {



    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            World world = e.getPlayer().getWorld();
            Location eyeLocation = e.getPlayer().getEyeLocation();
            RayTraceResult result = world.rayTraceEntities(eyeLocation, eyeLocation.getDirection(), 1.0);
            Entity entity = result.getHitEntity();
            if (entity instanceof Creature creature) {
            }
        }
    }
}
