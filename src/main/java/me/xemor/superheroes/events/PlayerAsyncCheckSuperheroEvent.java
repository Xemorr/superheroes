package me.xemor.superheroes.events;

import me.xemor.superheroes.Superhero;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event makes no guarantees about the thread it is called from.
 */
public class PlayerAsyncCheckSuperheroEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private Superhero superhero;
    private final Player player;

    public PlayerAsyncCheckSuperheroEvent(Superhero superhero, Player player) {
        super(!Bukkit.isPrimaryThread());
        this.superhero = superhero;
        this.player = player;
    }

    public Superhero getSuperhero() {
        return superhero;
    }

    public Player getPlayer() {
        return player;
    }

    public void setSuperhero(Superhero superhero) {
        this.superhero = superhero;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
