package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.SneakData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.Collection;

public class SneakSkill extends SkillImplementation {

    public SneakSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onSneak(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() instanceof Player) {
            Player player = (Player) e.getTarget();
            Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.SNEAK);
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
