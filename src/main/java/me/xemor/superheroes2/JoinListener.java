package me.xemor.superheroes2;

import org.bukkit.configuration.ConfigurationSection;
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
        ConfigurationSection powerOnStart = powersHandler.getPlugin().getConfig().getConfigurationSection("powerOnStart");
        boolean enabled;
        if (powerOnStart == null) enabled = true;
        else enabled = powerOnStart.getBoolean("isEnabled");
        if (powersHandler.getSuperhero(e.getPlayer()) == null) {
            if (enabled) {
                powersHandler.setRandomHero(e.getPlayer());
            }
            else {
                powersHandler.setHero(e.getPlayer(), powersHandler.getNoPower());
            }
        }
    }


}
