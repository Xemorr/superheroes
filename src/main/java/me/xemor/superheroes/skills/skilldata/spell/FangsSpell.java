package me.xemor.superheroes.skills.skilldata.spell;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.jetbrains.annotations.Nullable;

public class FangsSpell extends Spell {

    public FangsSpell(int spell, ConfigurationSection section) {
        super(spell, section);
    }

    @Override
    public boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace) {
        World world = player.getWorld();
        for (int i = 1; i < 10; i++) {
            EvokerFangs fangs = (EvokerFangs) world.spawnEntity(player.getLocation().add(player.getEyeLocation().getDirection().multiply(i)), EntityType.EVOKER_FANGS);
            fangs.setOwner(player);
        }
        return true;
    }
}
