package me.xemor.superheroes.skills.implementations;

import me.xemor.configurationdata.SoundData;
import me.xemor.configurationdata.particles.ParticleData;
import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.GunData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;

public class GunSkill extends SkillImplementation {

    final SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public GunSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void useGun(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(e.getPlayer()).getSkillData("GUN");
            Player player = e.getPlayer();
            for (SkillData skillData : skillDatas) {
                GunData gunData = (GunData) skillData;
                if (gunData.getItemComparison().matches(e.getItem())) {
                    if (skillCooldownHandler.isCooldownOver(gunData, player.getUniqueId())) {
                        Location currentLocation = player.getEyeLocation();
                        Vector increment = player.getEyeLocation().getDirection();
                        World world = player.getWorld();
                        SoundData shootSound = gunData.getShootSound();
                        world.playSound(player.getEyeLocation(), shootSound.sound(), shootSound.volume(), shootSound.pitch());
                        RayTraceResult rayTraceResult = world.rayTrace(currentLocation, increment, gunData.getMaxDistance(), FluidCollisionMode.NEVER, true, gunData.getBulletSize(),
                                (entity) -> (entity instanceof LivingEntity || entity instanceof EnderCrystal)&& !player.equals(entity));
                        skillCooldownHandler.startCooldown(gunData, gunData.getCooldown(), player.getUniqueId());
                        ParticleData trailData = gunData.getTrailParticle();
                        for (int i = 0; i < gunData.getMaxDistance(); i++) {
                            trailData.spawnParticle(currentLocation);
                            currentLocation = currentLocation.add(increment);
                        }
                        if (rayTraceResult == null) {
                            return;
                        }
                        if (rayTraceResult.getHitEntity() instanceof EnderCrystal enderCrystal) {
                            world.createExplosion(enderCrystal.getLocation(), 6);
                            enderCrystal.remove();
                            return;
                        }
                        LivingEntity livingEntity = (LivingEntity) rayTraceResult.getHitEntity();
                        if (livingEntity == null) {
                            return;
                        }
                        skillData.ifConditionsTrue(() -> {
                            if (livingEntity instanceof EnderDragon) {
                                double newHealth = livingEntity.getHealth() - gunData.getDamage();
                                if (newHealth < 0) return;
                                livingEntity.setHealth(newHealth);
                            }
                            livingEntity.damage(gunData.getDamage(), player); //doesn't work on edragon for some reason
                            ParticleData hitParticle = gunData.getHitParticle();
                            hitParticle.spawnParticle(livingEntity.getLocation().add(0, 1, 0));
                        }, player, livingEntity);
                    }
                }
            }
        }
    }
}
