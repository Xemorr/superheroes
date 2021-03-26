package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.RepulsionData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;

public class RepulsionSkill extends SkillImplementation {
    public RepulsionSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.REPULSION);
        for (SkillData skillData : skillDatas) {
            RepulsionData repulsionData = (RepulsionData) skillData;
            double multiplier = repulsionData.getMultiplier();
            double radius = repulsionData.getRadius();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }
                    if (!superhero.equals(heroHandler.getSuperhero(player))) {
                        cancel();
                        return;
                    }
                    if (player.isSneaking()) {
                        List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);
                        nearbyEntities.stream().filter(entity -> !entity.equals(player) && !repulsionData.inBlacklist(entity.getType())).forEach(entity -> entity.setVelocity(entity.getVelocity().add(entity.getLocation().subtract(player.getLocation()).toVector().normalize().multiply(0.1).multiply(multiplier)))
                        );
                    }
                    else {
                        cancel();
                        return;
                    }
                }
            }.runTaskTimer(heroHandler.getPlugin(), 0L, 1L);
        }
    }
}
