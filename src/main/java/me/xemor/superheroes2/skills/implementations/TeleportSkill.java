package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.ParticleHandler;
import me.xemor.superheroes2.SkillCooldownHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.data.HeroHandler;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.TeleportData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;

public class TeleportSkill extends SkillImplementation {


    SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();
    public TeleportSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = getPowersHandler().getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("TELEPORT"));
        for (SkillData skillData : skillDatas) {
            TeleportData teleportData = (TeleportData) skillData;
            if ((e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR) == teleportData.isLeftClick()) {
                if ((e.getItem() == null ? Material.AIR : e.getItem().getType()) == teleportData.getTeleportItem()) {
                    if (skillCooldownHandler.isCooldownOver(teleportData, player.getUniqueId())) {
                        doEnderTeleport(player, teleportData);
                        skillCooldownHandler.startCooldown(teleportData, teleportData.getCooldown(), player.getUniqueId());
                    }
                }
            }
        }
    }

    public void doEnderTeleport(Player player, TeleportData teleportData) {
        World world = player.getWorld();
        Location eyeLoc = player.getEyeLocation().clone();
        Vector travelVector = eyeLoc.getDirection().setY(eyeLoc.getDirection().getY() * teleportData.getyAxisMultiplier());
        RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLoc, travelVector, teleportData.getDistance());
        Vector hitPosition;
        if (rayTraceResult == null) {
            hitPosition = eyeLoc.toVector().add(travelVector.multiply(teleportData.getDistance()));
        } else {
            hitPosition = rayTraceResult.getHitPosition();
        }
        Location location = new Location(world, hitPosition.getX(), hitPosition.getY(), hitPosition.getZ());
        Location eyeLocation = player.getEyeLocation();
        location.setYaw(eyeLocation.getYaw());
        location.setPitch(eyeLocation.getPitch());
        player.teleport(location, teleportData.getTeleportCause());
        ParticleHandler particleHandler = new ParticleHandler(player);
        particleHandler.setupFromParticleData(teleportData.getParticleData());
        particleHandler.runTaskTimer(heroHandler.getPlugin(), 0L, 5L);
    }

}
