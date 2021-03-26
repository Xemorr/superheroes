package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.BlockRayData;
import me.xemor.superheroes2.skills.skilldata.BlockRayMode;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
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
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.BLOCKRAY);
        for (SkillData skillData : skillDatas) {
            BlockRayData blockRayData = (BlockRayData) skillData;
            World world = player.getWorld();
            Location eyeLocation = player.getEyeLocation();
            RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLocation, eyeLocation.getDirection(), blockRayData.getMaxDistance());
            if (rayTraceResult == null) {
                return;
            }
            Block block = rayTraceResult.getHitBlock();
            if (blockRayData.getBlocksToReplace().contains(block.getType()) && blockRayData.getBlockRayMode() == BlockRayMode.THEBLOCK) {
                block.setType(blockRayData.getRandomBlockToPlace());
                return;
            }
            if (blockRayData.getBlocksToReplace().contains(block.getType()) && blockRayData.getBlockRayMode() == BlockRayMode.ABOVEBLOCK) {
                Block aboveBlock = block.getRelative(BlockFace.UP);
                if (blockRayData.getBlocksToReplace().contains(aboveBlock.getType())) {
                    aboveBlock.setType(blockRayData.getRandomBlockToPlace());
                }
            }
        }
    }
}
