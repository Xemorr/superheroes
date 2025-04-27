package me.xemor.superheroes.skills.skilldata.spell;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.entity.EntityData;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class LaunchProjectileSpell extends SpellData {

    @JsonPropertyWithDefault
    private EntityData projectile = new EntityData();

    @Override
    public boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace) {
        Vector direction = player.getEyeLocation().getDirection().normalize();
        Entity projectile = this.projectile.spawnEntity(player.getLocation().add(direction.multiply(0.2)));
        projectile.setVelocity(direction);
        if (projectile instanceof AbstractArrow arrow) {
            arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        }
        return true;
    }
}
