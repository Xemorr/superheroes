package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.SkillCooldownHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.SummonSkillData;
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

    SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();

    public SummonSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onPunch(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("SUMMON"));
        for (SkillData skillData : skillDatas) {
            SummonSkillData summonData = (SummonSkillData) skillData;
            if (summonData.getAction().contains(e.getAction())) {
                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    if (summonData.mustSneak() == player.isSneaking()) {
                        if (skillCooldownHandler.isCooldownOver(summonData, player.getUniqueId())) {
                            Entity entity = summonEntity(player, summonData.getEntityType(), summonData);
                            if (entity == null) {
                                return;
                            }
                            skillCooldownHandler.startCooldown(summonData, player.getUniqueId());
                            if (summonData.doesRepel()) {
                                player.setVelocity(player.getEyeLocation().getDirection().multiply(-0.5));
                            }
                            if (summonData.getPotionEffect() != null) {
                                player.addPotionEffect(summonData.getPotionEffect());
                            }
                        }
                    }
                }
            }
        }
    }

    public Entity summonEntity(Player player, EntityType entityType, SummonSkillData summonSkillData) {
        World world = player.getWorld();
        Location eyeLoc = player.getEyeLocation().clone();
        Vector travelVector = eyeLoc.getDirection();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLoc, travelVector, summonSkillData.getRange());
        Vector hitPosition;
        if (rayTraceResult == null) {
            hitPosition = eyeLoc.toVector().add(travelVector.multiply(summonSkillData.getRange()));
        } else {
            hitPosition = rayTraceResult.getHitPosition();
        }
        Location location = new Location(world, hitPosition.getX(), hitPosition.getY(), hitPosition.getZ());
        Block block = location.getBlock();
        if (summonSkillData.areConditionsTrue(player, block)) {
            Entity entity;
            if (entityType == EntityType.LIGHTNING) entity = world.strikeLightning(location);
            else entity = world.spawnEntity(location, entityType);
            return entity;
        }
        return null;
    }
}
