package me.xemor.superheroes;

import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    PowersHandler powersHandler;

    public JoinListener(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (powersHandler.getPower(e.getPlayer()) == null) {
            powersHandler.setRandomPower(e.getPlayer());
        }
    }


}
