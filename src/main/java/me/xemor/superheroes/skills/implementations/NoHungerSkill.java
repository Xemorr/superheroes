package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.NoHungerData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Collection;

public class NoHungerSkill extends SkillImplementation {

    public NoHungerSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onSaturation(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player player) {
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData("NOHUNGER");
            for (SkillData skillData : skillDatas) {
                if (skillData instanceof NoHungerData data) {
                    if ((player.getFoodLevel() <= data.getMinimumHunger() && (e.getFoodLevel() - player.getFoodLevel() <= 0))) {
                        skillData.ifConditionsTrue(() -> e.setCancelled(true), player);
                    }
                }
            }
        }
    }
}
