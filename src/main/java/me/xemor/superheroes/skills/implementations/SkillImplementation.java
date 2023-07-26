package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.data.HeroHandler;
import org.bukkit.event.Listener;

public abstract class SkillImplementation implements Listener {

    protected final HeroHandler heroHandler;

    public SkillImplementation(HeroHandler heroHandler) {
        this.heroHandler = heroHandler;
    }

    public HeroHandler getPowersHandler() {
        return heroHandler;
    }

}
