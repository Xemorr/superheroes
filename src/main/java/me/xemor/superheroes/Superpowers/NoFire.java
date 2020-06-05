package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;

public class NoFire extends Superpower {

    public NoFire(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onFire(EntityCombustEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            Power power = powersHandler.getPower(player);
            if (power == Power.Pyromaniac || power == Power.LavaWalker) {
                e.setCancelled(true);
            }
        }
    }

}
