package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.SlimeData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class SlimeSkill extends SkillImplementation {

    public SlimeSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("SLIME"));
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                if (skillData instanceof SlimeData slimeData) {
                    World world = e.getPlayer().getWorld();
                    if (isOnGround(world, e.getTo()) && !isOnGround(world, e.getFrom())) {
                        if (slimeData.areConditionsTrue(player)) {
                            Vector velocity = e.getPlayer().getVelocity();
                            Superheroes.getScheduling().entitySpecificScheduler(e.getPlayer()).runDelayed(() -> {
                                    if (velocity.getY() < -0.26 && !e.getPlayer().isSneaking()) {
                                        velocity.setY(velocity.getY() * -1);
                                        velocity.add(e.getPlayer().getEyeLocation().clone().getDirection().setY(0).multiply(slimeData.getSpeedMultiplier()));
                                        e.getPlayer().setVelocity(velocity);
                                    }
                                    }, () -> {}, 2L);
                        }
                    }
                }
            }
        }
    }

    public boolean isOnGround(World world, Location location) {
        return world.getBlockAt(location.clone().subtract(0, 1, 0)).getType().isSolid();
    }

}
