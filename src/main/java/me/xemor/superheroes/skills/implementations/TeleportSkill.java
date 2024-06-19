package me.xemor.superheroes.skills.implementations;

import io.papermc.lib.PaperLib;
import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.TeleportData;
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
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportSkill extends SkillImplementation {


    final SkillCooldownHandler skillCooldownHandler = new SkillCooldownHandler();
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
                        if (skillData.areConditionsTrue(player)) {
                            doEnderTeleport(player, teleportData);
                            skillCooldownHandler.startCooldown(teleportData, teleportData.getCooldown(), player.getUniqueId());
                        }
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
        PaperLib.teleportAsync(player, location, teleportData.getTeleportCause());
        AtomicInteger count = new AtomicInteger(0);
        if (teleportData.getParticleData() != null) {
            Superheroes.getScheduling().regionSpecificScheduler(location).runAtFixedRate((task) -> {
                teleportData.getParticleData().spawnParticle(location);
                count.addAndGet(1);
                if (count.get() == 5) {
                    task.cancel();
                }
            }, 1L, 1L);
        }
    }

}