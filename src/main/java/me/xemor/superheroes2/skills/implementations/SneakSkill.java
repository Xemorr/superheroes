package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.SneakData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.Collection;

public class SneakSkill extends SkillImplementation {

    public SneakSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onSneak(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() instanceof Player) {
            Player player = (Player) e.getTarget();
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("SNEAK"));
            for (SkillData skillData : skillDatas) {
                SneakData sneakData = (SneakData) skillData;
                if ((sneakData.mustSneak() && player.isSneaking()) || !sneakData.mustSneak()) {
                    if (sneakData.needsInvisibility() && player.isInvisible() || !sneakData.needsInvisibility()) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

}
