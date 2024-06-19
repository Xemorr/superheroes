package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.BlockRayData;
import me.xemor.superheroes.skills.skilldata.BlockRayMode;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.Collection;

public class BlockRaySkill extends SkillImplementation {

    public BlockRaySkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onLookChange(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("BLOCKRAY"));
        for (SkillData skillData : skillDatas) {
            BlockRayData blockRayData = (BlockRayData) skillData;
            World world = player.getWorld();
            Location eyeLocation = player.getEyeLocation();
            RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLocation, eyeLocation.getDirection(), blockRayData.getMaxDistance());
            if (rayTraceResult == null) {
                continue;
            }
            Block block = rayTraceResult.getHitBlock();
            if (block == null) {
                continue;
            }
            if (blockRayData.getBlocksToReplace().contains(block.getType())) {
                final Block toChange;
                if (blockRayData.getBlockRayMode() == BlockRayMode.ABOVEBLOCK) {
                    toChange = block.getRelative(BlockFace.UP);
                }
                else {
                    toChange = block;
                }
                if (skillData.areConditionsTrue(player, toChange.getLocation())) {
                    if (blockRayData.getBlocksToReplace().contains(toChange.getType()) && blockRayData.getBlocksToReplace().contains(block.getType())) {
                        final Material originalType = toChange.getType();
                        final Material newType = blockRayData.getRandomBlockToPlace();
                        toChange.setType(newType);
                        if (blockRayData.shouldRevert()) {
                            Superheroes.getScheduling().regionSpecificScheduler(toChange.getLocation()).runDelayed(() -> {
                                if (toChange.getType() == newType) {
                                    toChange.setType(originalType);
                                }
                            }, blockRayData.getRevertsAfter());
                        }
                    }
                }
            }
        }
    }
}
