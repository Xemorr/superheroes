package me.xemor.superheroes.Events;

import me.xemor.superheroes.Superpowers.Power;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLostPowerEvent extends Event{

    private static final HandlerList HANDLERS = new HandlerList();
    private Player player;
    private Power power;

    public PlayerLostPowerEvent(Player player, Power power) {
        this.player = player;
        this.power = power;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public Power getPower() {
        return power;
    }

}
