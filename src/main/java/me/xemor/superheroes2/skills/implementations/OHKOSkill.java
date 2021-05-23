package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
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
    public OHKOSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            Player player = (Player) e.getDamager();
            Superhero superhero = heroHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("OHKO"));
            for (SkillData skillData : skillDatas) {
                OHKOData ohkoData = (OHKOData) skillData;
                LivingEntity livingEntity = (LivingEntity) e.getEntity();
                if (ohkoData.getDisplayName() == null || ohkoData.getDisplayName().equals(livingEntity.getCustomName())) {
                    if (ohkoData.getEntityTypes().contains(e.getEntity().getType())) {
                        e.setDamage(livingEntity.getHealth() * 100);
                    }
                }
            }
        }
    }

}
