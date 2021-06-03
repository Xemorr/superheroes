package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.AuraData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class AuraSkill extends SkillImplementation {

    public AuraSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        runnable(e.getPlayer());
    }

    @EventHandler
    public void onPowerGain(PlayerGainedSuperheroEvent e) {
        runnable(e.getPlayer());
    }

    public void runnable(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player == null) {
                    cancel();
                    return;
                }
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                Superhero superhero = heroHandler.getSuperhero(player);
                if (superhero == null) {
                    cancel();
                    return;
                }
                Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("AURA"));
                if (skillDatas.isEmpty()) {
                    cancel();
                    return;
                }
                for (SkillData skillData : skillDatas) {
                    AuraData auraData = (AuraData) skillData;
                    World world = player.getWorld();
                    Location location = player.getLocation();
                    double diameter = auraData.getDiameter();
                    Collection<Entity> nearbyLivingEntities = world.getNearbyEntities(location, diameter, diameter, diameter, (entity) -> !player.equals(entity) && entity instanceof LivingEntity);
                    for (Entity entity : nearbyLivingEntities) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.addPotionEffect(auraData.getPotionEffect());
                    }
                }
            }
        }.runTaskTimer(heroHandler.getPlugin(), 10L, 10L);

    }
}
