package me.xemor.superheroes.skills.skilldata.spell;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class LightningSpell extends SpellData {

    @Override
    public boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace) {
        Location location = findLookingLocation(player, 30);
        player.getWorld().strikeLightning(location);
        return true;
    }

    private Location findLookingLocation(Player player, int blocksToTravel) {
        World world = player.getWorld();
        Location eyeLoc = player.getEyeLocation().clone();
        Vector travelVector = eyeLoc.getDirection();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLoc, travelVector, blocksToTravel);
        Vector hitPosition;
        if (rayTraceResult == null) {
            hitPosition = eyeLoc.toVector().add(travelVector.multiply(blocksToTravel));
        } else {
            rayTraceResult.getHitPosition();
            hitPosition = rayTraceResult.getHitPosition();
        }
        return new Location(world, hitPosition.getX(), hitPosition.getY(), hitPosition.getZ());
    }
}
