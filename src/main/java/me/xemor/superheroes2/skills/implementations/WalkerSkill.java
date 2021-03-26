package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.WalkerData;
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
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.WALKER);
        for (SkillData skillData : skillDatas) {
            WalkerData walkerData = (WalkerData) skillData;
            if (walkerData.isSneaking() != e.getPlayer().isSneaking()) {
                continue;
            }
            Location location = e.getTo();
            World world = player.getWorld();
            Block block;
            if (walkerData.isAboveFloor()) {
                block = world.getBlockAt(location);
            }
            else {
                block = world.getBlockAt(location.clone().subtract(0, 1, 0));
            }
            if (!walkerData.canPlaceFloating()) {
                if (block.getRelative(BlockFace.DOWN).getType().isSolid()) {
                    continue;
                }
            }
            Material originalMaterial = block.getType();
            if (walkerData.shouldReplace(block.getType())) {
                Material newMaterial = walkerData.getReplacementBlock();
                block.setType(newMaterial);
                block.setMetadata("blockDrops", new FixedMetadataValue(heroHandler.getPlugin(), walkerData.doesBlockDrop()));
                if (walkerData.shouldRevert()) {
                    revertRunnable(walkerData, block, newMaterial, originalMaterial);
                }
            }
        }
    }

    public void revertRunnable(WalkerData walkerData, Block block, Material newMaterial, Material originalMaterial) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (newMaterial == block.getType()) {
                    block.setType(originalMaterial);
                    block.removeMetadata("blockDrops", heroHandler.getPlugin());
                }
            }
        }.runTaskLater(heroHandler.getPlugin(), walkerData.getRevertsAfter());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().hasMetadata("blockDrops")) {
            e.setDropItems(e.getBlock().getMetadata("blockDrops").get(0).asBoolean());
        }
    }
}
