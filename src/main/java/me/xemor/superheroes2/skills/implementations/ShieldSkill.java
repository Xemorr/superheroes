package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.ShieldData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
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
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("SHIELD"));
            for (SkillData skillData : skillDatas) {
                ShieldData shieldData = (ShieldData) skillData;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setCooldown(Material.SHIELD, shieldData.getCooldown());
                    }
                }.runTaskLater(heroHandler.getPlugin(), 1L);
            }
        }
    }
}
