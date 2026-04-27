package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.skilldata.JetpackData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class JetpackSkill extends SkillImplementation {

    public JetpackSkill(HeroHandler heroHandler) {
        super(heroHandler);
        Superheroes.getScheduling().globalRegionalScheduler().runAtFixedRate(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Superheroes.getScheduling().entitySpecificScheduler(player).run(() -> applyJetpack(player), () -> {});
            }
        }, 1L, 1L);
    }

    @EventHandler
    public void onGlide(PlayerToggleFlightEvent e) {
        Superhero superhero = heroHandler.getSuperhero(e.getPlayer());
        List<JetpackData> skillData = superhero.getSkillData(JetpackData.class);
        for (JetpackData data : skillData) {
            if (e.getPlayer().isJumping() && e.getPlayer().isGliding() && (!data.shouldDisableInWater() || !e.getPlayer().isInWater())) {
                e.setCancelled(true);
            }
        }
    }

    public void applyJetpack(Player player) {
        Superhero superhero = this.heroHandler.getSuperhero(player);
        if (player.getCurrentInput().isJump()) {
            List<JetpackData> skillData = superhero.getSkillData(JetpackData.class);
            for (JetpackData data : skillData) {
                player.setAllowFlight(true);
                if ((!data.shouldDisableInWater() || !player.isInWater())) {
                    if (data.glidingMode()) player.setGliding(true);
                    Vector targetVelocity;
                    if (data.isVerticalOnly()) {
                        targetVelocity = player.getVelocity().clone().setY(data.getVerticalMagnitude());
                    }
                    else {
                        Vector direction = player.getEyeLocation().getDirection().normalize();
                        targetVelocity = new Vector(
                                direction.getX() * data.getHorizontalMagnitude(),
                                direction.getY() * data.getVerticalMagnitude(),
                                direction.getZ() * data.getHorizontalMagnitude()
                        );
                    }
                    Vector currentVelocity = player.getVelocity();

                    Vector velocityDifference = targetVelocity.clone().subtract(currentVelocity);

                    Vector accelerationStep = new Vector(
                            velocityDifference.getX() * data.getHorizontalAccelerationMultiplier(),
                            velocityDifference.getY() * data.getVerticalAccelerationMultiplier(),
                            velocityDifference.getZ() * data.getHorizontalAccelerationMultiplier()
                    );

                    if (data.isVerticalOnly()) {
                        accelerationStep.setX(0);
                        accelerationStep.setZ(0);
                    }

                    player.setVelocity(currentVelocity.add(accelerationStep));
                }
            }
        }
        else {
            List<JetpackData> skillData = superhero.getSkillData(JetpackData.class);
            for (JetpackData data : skillData) {
                player.setAllowFlight(false);
            }
        }
    }

}
