package me.xemor.superheroes2.skills;

import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.SlimeData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class SlimeSkill extends SkillImplementation {

    public SlimeSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = powersHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.SLIME);
        if (skillDatas != null) {
            for (SkillData skillData : skillDatas) {
                if (skillData instanceof SlimeData) {
                    SlimeData slimeData = (SlimeData) skillData;
                    World world = e.getPlayer().getWorld();
                    if (isOnGround(world, e.getTo()) && !isOnGround(world, e.getFrom())) {
                        Vector velocity = e.getPlayer().getVelocity();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (velocity.getY() < -0.26 && !e.getPlayer().isSneaking()) {
                                    velocity.setY(velocity.getY() * -1);
                                    velocity.add(e.getPlayer().getEyeLocation().clone().getDirection().setY(0).multiply(slimeData.getSpeedMultiplier()));
                                    e.getPlayer().setVelocity(velocity);
                                }
                            }
                        }.runTaskLater(powersHandler.getPlugin(), 2L);
                    }
                }
            }
        }
    }

    public boolean isOnGround(World world, Location location) {
        return world.getBlockAt(location.clone().subtract(0, 1, 0)).getType().isSolid();
    }

}
