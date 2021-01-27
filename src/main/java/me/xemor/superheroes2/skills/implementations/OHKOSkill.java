package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.OHKOData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collection;

public class OHKOSkill extends SkillImplementation {
    public OHKOSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            Player player = (Player) e.getDamager();
            Superhero superhero = powersHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData(Skill.OHKO);
            for (SkillData skillData : skillDatas) {
                OHKOData ohkoData = (OHKOData) skillData;
                LivingEntity livingEntity = (LivingEntity) e.getEntity();
                if (ohkoData.getDisplayName() == null || livingEntity.getCustomName().equals(ohkoData.getDisplayName())) {
                    if (ohkoData.getEntityTypes().contains(e.getEntity().getType())) {
                        livingEntity.damage(livingEntity.getHealth(), e.getDamager());
                    }
                }
            }
        }
    }

}
