package me.xemor.superheroes.skills.skilldata.spell;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceBlockSpell extends Spell {
    private final Material resultantBlock;

    public PlaceBlockSpell(Material material, int spell, ConfigurationSection configurationSection) {
        super(spell, configurationSection);
        this.resultantBlock = material;
    }

    public Material getResultantBlock() {
        return resultantBlock;
    }

    @Override
    public boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace) {
        if (clickedBlock != null && blockFace != null) {
            Block placeHere = clickedBlock.getRelative(blockFace);
            placeHere.setType(resultantBlock);
            return true;
        }
        return false;
    }
}
