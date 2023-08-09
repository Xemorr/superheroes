package me.xemor.superheroes.skills.implementations;

import me.xemor.configurationdata.ParticleData;
import me.xemor.configurationdata.SoundData;
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
            Collection<SkillData> skillDatas = heroHandler.getSuperhero(e.getPlayer()).getSkillData(Skill.getSkill("GUN"));
            Player player = e.getPlayer();
            for (SkillData skillData : skillDatas) {
                GunData gunData = (GunData) skillData;
                if (gunData.getItemStackData().matches(e.getItem())) {
                    if (skillCooldownHandler.isCooldownOver(gunData, player.getUniqueId())) {
                        Location currentLocation = player.getEyeLocation();
                        Vector increment = player.getEyeLocation().getDirection();
                        World world = player.getWorld();
                        SoundData shootSound = gunData.getShootSoundData();
                        world.playSound(player.getEyeLocation(), shootSound.getSound(), shootSound.getVolume(), shootSound.getPitch());
                        RayTraceResult rayTraceResult = world.rayTrace(currentLocation, increment, gunData.getMaxDistance(), FluidCollisionMode.NEVER, true, gunData.getBulletSize(),
                                (entity) -> (entity instanceof LivingEntity || entity instanceof EnderCrystal)&& !player.equals(entity));
                        skillCooldownHandler.startCooldown(gunData, gunData.getCooldown(), player.getUniqueId());
                        ParticleData trailData = gunData.getTrailParticle();
                        for (int i = 0; i < gunData.getMaxDistance(); i++) {
                            world.spawnParticle(trailData.getParticle(), currentLocation, trailData.getNumberOfParticles());
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
                        if (!skillData.areConditionsTrue(player, livingEntity)) {
                            return;
                        }
                        if (livingEntity instanceof EnderDragon) {
                            livingEntity.setHealth(livingEntity.getHealth() - gunData.getDamage());
                        }
                        livingEntity.damage(gunData.getDamage(), player); //doesn't work on edragon for some reason
                        ParticleData hitParticle = gunData.getHitParticle();
                        world.spawnParticle(hitParticle.getParticle(), livingEntity.getLocation().add(0, 1, 0), hitParticle.getNumberOfParticles());
                    }
                }
            }
        }
    }
}
