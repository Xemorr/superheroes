package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.ElectrifiedData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collection;

public class ElectrifiedSkill extends SkillImplementation {

    public ElectrifiedSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            Player player = (Player) e.getEntity();
            Superhero superhero = powersHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData(Skill.ELECTRIFIED);
            if (!skillDatas.isEmpty()) {
                for (SkillData skillData : skillDatas) {
                    ElectrifiedData electrifiedData = (ElectrifiedData) skillData;
                    e.setDamage(e.getDamage() * electrifiedData.getDamageResistance());
                    player.addPotionEffect(electrifiedData.getPotionEffect());
                }
            }
        }
    }

    @EventHandler
    public void onPowerLoss(PlayerLostSuperheroEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = powersHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.ELECTRIFIED);
        if (!skillDatas.isEmpty()) {
            for (SkillData skillData : skillDatas) {
                ElectrifiedData electrifiedData = (ElectrifiedData) skillData;
                player.removePotionEffect(electrifiedData.getPotionEffect().getType());
            }
        }
    }


}
