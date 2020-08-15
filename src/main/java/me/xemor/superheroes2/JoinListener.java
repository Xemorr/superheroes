package me.xemor.superheroes2;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    PowersHandler powersHandler;

    public JoinListener(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        if (powersHandler.getSuperhero(e.getPlayer()) == null) {
            powersHandler.setRandomHero(e.getPlayer());
        }
    }


}
