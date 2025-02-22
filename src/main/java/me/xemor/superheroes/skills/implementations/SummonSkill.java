package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.SummonSkillData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;

public class SummonSkill extends SkillImplementation {

    final SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public SummonSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onPunch(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SummonSkillData> summonDatas = superhero.getSkillData(SummonSkillData.class);
        for (SummonSkillData summonData : summonDatas) {
            if (summonData.getAction().contains(e.getAction())) {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    if (summonData.mustSneak() == player.isSneaking()) {
                        if (skillCooldownHandler.isCooldownOver(summonData, player.getUniqueId())) {
                            Location location = getRaytraceLocation(player, summonData);
                            summonData.ifConditionsTrue(() -> {
                                summonEntity(location, summonData.getEntityType());
                                skillCooldownHandler.startCooldown(summonData, player.getUniqueId());
                                if (summonData.doesRepel()) {
                                    player.setVelocity(player.getEyeLocation().getDirection().multiply(-0.5));
                                }
                                summonData.getPotionEffect().ifPresent(player::addPotionEffect);
                            }, player, location);
                        }
                    }
                }
            }
        }
    }

    public Location getRaytraceLocation(Player player, SummonSkillData summonData) {
        World world = player.getWorld();
        Location eyeLoc = player.getEyeLocation().clone();
        Vector travelVector = eyeLoc.getDirection();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLoc, travelVector, summonData.getRange());
        Vector hitPosition;
        if (rayTraceResult == null) {
            hitPosition = eyeLoc.toVector().add(travelVector.multiply(summonData.getRange()));
        } else {
            hitPosition = rayTraceResult.getHitPosition();
        }
        return new Location(world, hitPosition.getX(), hitPosition.getY(), hitPosition.getZ());
    }

    public void summonEntity(Location location, EntityType entityType) {
        World world = location.getWorld();
        if (entityType == EntityType.LIGHTNING_BOLT) world.strikeLightning(location);
        else world.spawnEntity(location, entityType);
    }
}
