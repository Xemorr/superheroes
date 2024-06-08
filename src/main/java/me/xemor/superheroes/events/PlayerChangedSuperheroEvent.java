package me.xemor.superheroes.events;

import me.xemor.superheroes.Superhero;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerChangedSuperheroEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private Superhero newHero;
    private final Superhero oldHero;
    private boolean cancelled = false;
    private boolean writtentodisk = false;
    private Cause cause = Cause.OTHER;

    public PlayerChangedSuperheroEvent(Player player, Superhero newHero, Superhero oldHero) {
        this.player = player;
        this.newHero = newHero;
        this.oldHero = oldHero;
    }

    public PlayerChangedSuperheroEvent(Player player, Superhero newHero, Superhero oldHero, Cause cause, boolean writtenToDisk) {
        this.player = player;
        this.newHero = newHero;
        this.oldHero = oldHero;
        this.cause = cause;
        this.writtentodisk = writtenToDisk;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
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

    public Cause getCause() {
        return cause;
    }

    public void setNewHero(Superhero newHero) {
        this.newHero = newHero;
    }

    public Superhero getOldHero() {
        return oldHero;
    }

    public boolean isWrittenToDisk() {
        return writtentodisk;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public enum Cause {
        WORLDGUARD, COMMAND, REROLL, OTHER;
    }
}
