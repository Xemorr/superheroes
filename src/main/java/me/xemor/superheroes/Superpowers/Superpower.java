package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import org.bukkit.event.Listener;

public class Superpower implements Listener {

    protected PowersHandler powersHandler;

    public Superpower(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    public PowersHandler getPowersHandler() {
        return powersHandler;
    }

}
