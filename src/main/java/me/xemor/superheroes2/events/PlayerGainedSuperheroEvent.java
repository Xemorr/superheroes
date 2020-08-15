package me.xemor.superheroes2.events;

import me.xemor.superheroes2.Superhero;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerGainedSuperheroEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Player player;
    private Superhero hero;

    public PlayerGainedSuperheroEvent(Player player, Superhero hero) {
        this.player = player;
        this.hero = hero;
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

    public Superhero getHero() {
        return hero;
    }
}
