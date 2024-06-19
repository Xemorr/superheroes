package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.ShieldData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class ShieldSkill extends SkillImplementation {
    public ShieldSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player player) {
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("SHIELD"));
            for (SkillData skillData : skillDatas) {
                ShieldData shieldData = (ShieldData) skillData;
                if (shieldData.areConditionsTrue(player, e.getDamager())) {
                    Superheroes.getScheduling().entitySpecificScheduler(player).runDelayed(() -> {
                        player.setCooldown(Material.SHIELD, shieldData.getCooldown());
                    }, () -> {}, 1L);
                }
            }
        }
    }
}
