package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class Frozone extends Superpower {

    public Frozone(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.isSneaking()) {
            return;
        }
        if (powersHandler.getPower(player) == Power.Frozone) {
            Location location = e.getTo();
            World world = player.getWorld();
            Block block = world.getBlockAt(location.clone().subtract(0, 1, 0));
            if (block.getType() == Material.WATER) {
                block.setType(Material.FROSTED_ICE);
            }
            Collection<LivingEntity> nearbyLivingEntities = world.getNearbyLivingEntities(location, 10, (entity) -> !player.equals(entity));
            for (LivingEntity livingEntity : nearbyLivingEntities) {
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
            }
        }
    }

}
