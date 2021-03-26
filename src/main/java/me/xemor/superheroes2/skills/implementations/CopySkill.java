package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CopySkill extends SkillImplementation {
    public CopySkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player copier = (Player) e.getDamager();
            heroHandler.getSuperhero(copier);
        }
    }
}
