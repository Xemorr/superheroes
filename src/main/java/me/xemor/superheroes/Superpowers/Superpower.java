package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.event.Listener;

public class Superpower implements Listener {

    PowersHandler powersHandler;

    public Superpower(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    public PowersHandler getPowersHandler() {
        return powersHandler;
    }

}
