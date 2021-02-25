package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.CooldownHandler;
import me.xemor.superheroes2.PowersHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.GunData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.configdata.ParticleData;
import me.xemor.superheroes2.skills.skilldata.configdata.SoundData;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;

public class GunSkill extends SkillImplementation {

    CooldownHandler cooldownHandler = new CooldownHandler("&8&lGun &7has %s seconds left until it can be used again!");

    public GunSkill(PowersHandler powersHandler) {
        super(powersHandler);
    }

    @EventHandler
    public void useGun(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Collection<SkillData> skillDatas = powersHandler.getSuperhero(e.getPlayer()).getSkillData(Skill.GUN);
            Player player = e.getPlayer();
            for (SkillData skillData : skillDatas) {
                GunData gunData = (GunData) skillData;
                ItemStack gun = gunData.getItemStackData().getItem();
                if (gun.isSimilar(e.getItem())) {
                    if (cooldownHandler.isCooldownOver(gunData, player.getUniqueId(), gunData.getCooldownMessage())) {
                        Location currentLocation = player.getEyeLocation();
                        Vector increment = player.getEyeLocation().getDirection();
                        World world = player.getWorld();
                        SoundData shootSound = gunData.getShootSoundData();
                        world.playSound(player.getEyeLocation(), shootSound.getSound(), shootSound.getVolume(), shootSound.getPitch());
                        RayTraceResult rayTraceResult = world.rayTrace(currentLocation, increment, gunData.getMaxDistance(), FluidCollisionMode.NEVER, true, gunData.getBulletSize(),
                                (entity) -> (entity instanceof LivingEntity || entity instanceof EnderCrystal)&& !player.equals(entity));
                        cooldownHandler.startCooldown(gunData, gunData.getCooldown(), player.getUniqueId());
                        ParticleData trailData = gunData.getTrailParticle();
                        for (int i = 0; i < gunData.getMaxDistance(); i++) {
                            world.spawnParticle(trailData.getParticle(), currentLocation, trailData.getNumberOfParticles());
                            currentLocation = currentLocation.add(increment);
                        }
                        if (rayTraceResult == null) {
                            return;
                        }
                        if (rayTraceResult.getHitEntity() instanceof EnderCrystal) {
                            EnderCrystal enderCrystal = (EnderCrystal) rayTraceResult.getHitEntity();
                            world.createExplosion(enderCrystal.getLocation(), 6);
                            enderCrystal.remove();
                            return;
                        }
                        LivingEntity livingEntity = (LivingEntity) rayTraceResult.getHitEntity();
                        if (livingEntity == null) {
                            return;
                        }
                        if (livingEntity instanceof EnderDragon) {
                            livingEntity.setHealth(livingEntity.getHealth() - gunData.getDamage());
                            return;
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
