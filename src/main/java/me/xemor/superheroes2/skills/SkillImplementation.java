package me.xemor.superheroes2.skills;

import me.xemor.superheroes2.PowersHandler;
import org.bukkit.event.Listener;

public abstract class SkillImplementation implements Listener {

    protected PowersHandler powersHandler;

    public SkillImplementation(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    public PowersHandler getPowersHandler() {
        return powersHandler;
    }

}
