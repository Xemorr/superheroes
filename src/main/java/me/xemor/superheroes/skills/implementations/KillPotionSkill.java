package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.KillPotionData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Collection;

public class KillPotionSkill extends SkillImplementation {
    public KillPotionSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        Player player = e.getEntity().getKiller();
        if (player == null) return;
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("KILLPOTION"));
        for (SkillData skillData : skillDatas) {
            KillPotionData killPotionData = (KillPotionData) skillData;
            if (!killPotionData.getEntities().inSet(e.getEntityType())) return;
            killPotionData.ifConditionsTrue(() -> killPotionData.getPotionEffect().ifPresent(player::addPotionEffect), player, e.getEntity());
        }
    }
}
