package me.xemor.superheroes.events;

import me.xemor.superheroes.Superhero;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SuperheroLoadEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Superhero superhero;
    private final ConfigurationSection section;

    private boolean isCancelled = false;

    public SuperheroLoadEvent(Superhero superhero, ConfigurationSection section) {
        this.superhero = superhero;
        this.section = section;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Superhero getSuperhero() {
        return superhero;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
