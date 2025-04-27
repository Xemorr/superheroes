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
            Collection<ShieldData> shieldDatas = heroHandler.getSuperhero(player).getSkillData(ShieldData.class);
            for (ShieldData shieldData : shieldDatas) {
                shieldData.ifConditionsTrue(() -> {
                    Superheroes.getScheduling().entitySpecificScheduler(player).runDelayed(() -> {
                        player.setCooldown(Material.SHIELD, (int) shieldData.getCooldown());
                    }, () -> {}, 1L);
                }, player, e.getDamager());
            }
        }
    }
}
