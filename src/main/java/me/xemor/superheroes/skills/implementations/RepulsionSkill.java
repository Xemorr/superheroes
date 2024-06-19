package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.RepulsionData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
        Collection<SkillData> skillDatas = heroHandler.getSuperhero(player).getSkillData(Skill.getSkill("REPULSION"));
        for (SkillData skillData : skillDatas) {
            RepulsionData repulsionData = (RepulsionData) skillData;
            double multiplier = repulsionData.getMultiplier();
            double radius = repulsionData.getRadius();
            Superheroes.getScheduling().entitySpecificScheduler(player).runAtFixedRate(
                    (task) -> {
                        if (!player.isOnline()) {
                            task.cancel();
                            return;
                        }
                        if (!superhero.equals(heroHandler.getSuperhero(player))) {
                            task.cancel();
                            return;
                        }
                        if (player.isSneaking()) {
                            List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);
                            nearbyEntities
                                    .stream()
                                    .filter(entity -> !entity.equals(player))
                                    .filter(entity -> !repulsionData.inBlacklist(entity.getType()))
                                    .filter(entity -> skillData.areConditionsTrue(player, entity))
                                    .forEach(entity -> {
                                                Vector distanceVelocity = entity.getLocation().subtract(player.getLocation()).toVector().normalize().multiply(0.1).multiply(multiplier);
                                                try {
                                                    distanceVelocity.checkFinite();
                                                } catch (IllegalArgumentException notFinite) {
                                                    return;
                                                }
                                                Vector newVelocity = entity.getVelocity().add(distanceVelocity);
                                                entity.setVelocity(newVelocity);
                                            }
                                    );
                        }
                        else {
                            task.cancel();
                        }
                    },
                    () -> {},
                    1L, 1L
            );
        }
    }
}
