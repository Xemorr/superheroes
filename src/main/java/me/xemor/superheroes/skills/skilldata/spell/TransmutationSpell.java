package me.xemor.superheroes.skills.skilldata.spell;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class TransmutationSpell extends Spell {

    private final List<Material> transmutableBlocks;
    private final Material resultantBlock;

    public TransmutationSpell(int spell, ConfigurationSection configurationSection) {
        super(spell, configurationSection);
        resultantBlock = Material.valueOf(configurationSection.getString("transmutationData.resultantBlock", "REDSTONE_BLOCK"));
        transmutableBlocks = configurationSection.getStringList("transmutationData.transmutableBlocks").stream().map(Material::valueOf).collect(Collectors.toList());
    }

    public List<Material> getTransmutatableBlocks() {
        return transmutableBlocks;
    }

    public Material getResultantBlock() {
        return resultantBlock;
    }

    @Override
    public boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace) {
        if (clickedBlock != null) {
            if (transmutableBlocks.contains(clickedBlock.getType())) {
                clickedBlock.setType(resultantBlock);
                return true;
            }
        }
        return false;
    }
}
