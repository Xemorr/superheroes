package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.Events.PlayerLostPowerEvent;
import me.xemor.superheroes.PowersHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class Strongman extends Superpower {
    public Strongman(PowersHandler powersHandler) {
        super(powersHandler);
    }

    private String tooMuscular = ChatColor.translateAlternateColorCodes('&', Power.Strongman.getNameColourCode() + " &fis too strong to sit in a vehicle!");

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (powersHandler.getPower(e.getPlayer()) == Power.Strongman) {
            Entity topEntity = getTopEntity(e.getPlayer());
            if (!e.getRightClicked().equals(topEntity)) {
                topEntity.addPassenger(e.getRightClicked());
            }
            if (e.getRightClicked() instanceof Vehicle) {
                e.getPlayer().sendMessage(tooMuscular);
            }
        }
    }

    public Entity getTopEntity(Entity entity) {
        if (entity.getPassengers().size() > 0) {
            return getTopEntity(entity.getPassengers().get(0));
        }
        else {
            return entity;
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) == Power.Strongman) {
                while (player.getPassengers().size() > 0) {
                    Entity topEntity = getTopEntity(player);
                    Vector velocity = player.getEyeLocation().getDirection().normalize();
                    velocity.setX(velocity.getX() * 2.5);
                    velocity.setZ(velocity.getZ() * 2.5);
                    topEntity.setVelocity(velocity);
                    topEntity.getVehicle().removePassenger(topEntity);
                }
            }
        }
    }

    @EventHandler
    public void onLostPower(PlayerLostPowerEvent e) {
        Player player = e.getPlayer();
        if (e.getPower() == Power.Strongman) {
            while (player.getPassengers().size() > 0) {
                Entity topEntity = getTopEntity(player);
                topEntity.getVehicle().removePassenger(topEntity);
            }
        }
    }
}
