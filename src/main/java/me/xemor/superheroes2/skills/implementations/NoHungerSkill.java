package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class NoHungerSkill extends SkillImplementation {
    public NoHungerSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onSaturation(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (e.getFoodLevel() <= player.getFoodLevel()) {
                if (heroHandler.getSuperhero(player).hasSkill(Skill.getSkill("NOHUNGER"))) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
