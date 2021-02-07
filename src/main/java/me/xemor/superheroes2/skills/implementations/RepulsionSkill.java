package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.RepulsionData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class RepulsionSkill extends SkillImplementation {
    public RepulsionSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        Collection<SkillData> skillDatas = powersHandler.getSuperhero(player).getSkillData(Skill.REPULSION);
        for (SkillData skillData : skillDatas) {
            RepulsionData repulsionData = (RepulsionData) skillData;
            double multiplier = repulsionData.getMultiplier();
            double radius = repulsionData.getRadius();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isSneaking()) {
                        player.getNearbyEntities(radius, radius, radius).stream().filter(entity -> !entity.equals(player) && !repulsionData.inBlacklist(entity.getType())).forEach(
                                entity -> entity.setVelocity(entity.getVelocity().add(entity.getLocation().subtract(player.getLocation()).toVector().multiply(multiplier)))
                        );
                    }
                }
            }.runTaskTimer(powersHandler.getPlugin(), 0L, 10L);
        }
    }
}
