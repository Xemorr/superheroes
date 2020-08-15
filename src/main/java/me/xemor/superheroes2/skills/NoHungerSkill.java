package me.xemor.superheroes2.skills;

import me.xemor.superheroes2.PowersHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class NoHungerSkill extends SkillImplementation {
    public NoHungerSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onSaturation(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (e.getFoodLevel() <= player.getFoodLevel()) {
                if (powersHandler.getSuperhero(player).hasSkill(Skill.NOHUNGER)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
