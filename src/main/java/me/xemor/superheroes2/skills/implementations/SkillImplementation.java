package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.data.HeroHandler;
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
