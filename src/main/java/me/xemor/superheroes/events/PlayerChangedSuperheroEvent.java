package me.xemor.superheroes.events;

import me.xemor.superheroes.Superhero;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerChangedSuperheroEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private Player player;
    private Superhero newHero;
    private Superhero oldHero;

    public PlayerChangedSuperheroEvent(Player player, Superhero newHero, Superhero oldHero) {
        this.player = player;
        this.newHero = newHero;
        this.oldHero = oldHero;
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

    public Superhero getNewHero() {
        return newHero;
    }

    public Superhero getOldHero() {
        return oldHero;
    }
}
