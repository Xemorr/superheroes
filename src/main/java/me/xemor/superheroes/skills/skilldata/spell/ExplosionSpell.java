package me.xemor.superheroes.skills.skilldata.spell;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.jetbrains.annotations.Nullable;

public class ExplosionSpell extends SpellData {

    public ExplosionSpell() {}

    @Override
    public boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace) {
        TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.TNT);
        tnt.setFuseTicks(50);
        tnt.setVelocity(player.getEyeLocation().getDirection().multiply(1.4));
        return true;
    }
}
