package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.AuraData;
import me.xemor.superheroes.skills.skilldata.SkillData;
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
        runnable(e.getPlayer(), Superheroes.getInstance().getHeroHandler().getSuperhero(e.getPlayer()));
    }

    @EventHandler
    public void onPowerGain(PlayerChangedSuperheroEvent e) {
        runnable(e.getPlayer(), e.getOldHero());
    }

    public void runnable(Player player, Superhero oldHero) {
        Superheroes.getScheduling().entitySpecificScheduler(player).runAtFixedRate((task) -> {
            if (player == null) {
                task.cancel();
                return;
            }
            if (!player.isOnline()) {
                task.cancel();
                return;
            }
            Collection<SkillData> skillDatas = oldHero.getSkillData(Skill.getSkill("AURA"));
            if (skillDatas.isEmpty()) {
                task.cancel();
                return;
            }
            for (SkillData skillData : skillDatas) {
                AuraData auraData = (AuraData) skillData;
                World world = player.getWorld();
                Location location = player.getLocation();
                double diameter = auraData.getDiameter();
                Collection<Entity> nearbyLivingEntities = world.getNearbyEntities(location, diameter, diameter, diameter, (entity) -> !player.equals(entity) && entity instanceof LivingEntity);
                for (Entity entity : nearbyLivingEntities) {
                    if (skillData.areConditionsTrue(player, entity)) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        auraData.getPotionEffect().ifPresent(livingEntity::addPotionEffect);
                    }
                }
            }
        }, () -> {}, 10L, 10L);
    }
}
