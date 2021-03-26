package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import org.bukkit.event.Listener;

public abstract class SkillImplementation implements Listener {

    protected HeroHandler heroHandler;

    public SkillImplementation(HeroHandler heroHandler) {
        this.heroHandler = heroHandler;
    }

    public HeroHandler getPowersHandler() {
        return heroHandler;
    }

}
