package me.xemor.superheroes.skills.skilldata.spell;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.jetbrains.annotations.Nullable;

public class LaunchProjectileSpell extends Spell {

    private final Class<? extends Projectile> entityClass;

    public LaunchProjectileSpell(Class<? extends Projectile> entityClass, int spell, ConfigurationSection section) {
        super(spell, section);
        this.entityClass = entityClass;
    }

    @Override
    public boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace) {
        Entity entity = player.launchProjectile(entityClass);
        if (entity instanceof AbstractArrow arrow) {
            arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        }
        return true;
    }
}
