package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.LifestealData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collection;

public class LifestealSkill extends SkillImplementation {

    public LifestealSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getDamager() instanceof Player player) {
            Superhero superhero = heroHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("LIFESTEAL"));
            for (SkillData skillData : skillDatas) {
                LifestealData lifestealData = (LifestealData) skillData;
                if (!lifestealData.areConditionsTrue(player, e.getEntity())) {
                    return;
                }
                double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
                if (player.getHealth() >= maxHealth) {
                    return;
                }
                double lifesteal = e.getFinalDamage() * lifestealData.getLifesteal();
                if (player.getHealth() + lifesteal >= maxHealth) {
                    player.setHealth(maxHealth);
                    return;
                }
                player.setHealth(player.getHealth() + lifesteal);
            }
        }
    }
}
