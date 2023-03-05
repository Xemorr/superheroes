package me.xemor.superheroes.events;

import me.xemor.superheroes.Superhero;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SuperheroPlayerJoinEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Superhero superhero;
    private final Player player;

    public SuperheroPlayerJoinEvent(Superhero superhero, Player player) {
        this.superhero = superhero;
        this.player = player;
    }

    public Superhero getSuperhero() {
        return superhero;
    }

    public Player getPlayer() {
        return player;
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
