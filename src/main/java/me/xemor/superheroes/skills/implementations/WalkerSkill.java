package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.WalkerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class WalkerSkill extends SkillImplementation {

    public WalkerSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("WALKER"));
        if (player.isInsideVehicle()) {
            return;
        }
        for (SkillData skillData : skillDatas) {
            WalkerData walkerData = (WalkerData) skillData;
            if (walkerData.isSneaking() != e.getPlayer().isSneaking()) {
                continue;
            }
            Location location = e.getTo();
            if (e.getTo() == null) {
                continue;
            }
            World world = player.getWorld();
            Block block;
            if (walkerData.isAboveFloor()) {
                block = world.getBlockAt(location);
            }
            else {
                block = world.getBlockAt(location.clone().subtract(0, 1, 0));
            }
            Material belowBlock = block.getRelative(BlockFace.DOWN).getType();
            if (!walkerData.canPlaceFloating()) {
                if (!belowBlock.isSolid()) {
                    continue;
                }
            }
            if (!walkerData.canPlaceOn(belowBlock)) {
                continue;
            }
            Material originalMaterial = block.getType();
            if (walkerData.shouldReplace(block.getType())) {
                if (skillData.areConditionsTrue(player, block.getLocation())) {
                    Material newMaterial = walkerData.getReplacementBlock();
                    block.setType(newMaterial, false);
                    block.setMetadata("blockDrops", new FixedMetadataValue(heroHandler.getPlugin(), walkerData.doesBlockDrop()));
                    if (walkerData.shouldRevert()) {
                        revertRunnable(walkerData, block, newMaterial, originalMaterial);
                    }
                }
            }
        }
    }

    public void revertRunnable(WalkerData walkerData, Block block, Material newMaterial, Material originalMaterial) {
        Superheroes.getScheduling().regionSpecificScheduler(block.getLocation()).runDelayed(() -> {
            block.removeMetadata("blockDrops", heroHandler.getPlugin());
            if (newMaterial == block.getType()) {
                block.setType(originalMaterial);
            }
        }, walkerData.getRevertsAfter());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().hasMetadata("blockDrops")) {
            e.setDropItems(e.getBlock().getMetadata("blockDrops").get(0).asBoolean());
        }
    }
}
