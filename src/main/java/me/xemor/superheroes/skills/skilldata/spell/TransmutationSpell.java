package me.xemor.superheroes.skills.skilldata.spell;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.CompulsoryJsonProperty;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import me.xemor.superheroes.Superheroes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.libraries.cloplib.operation.OperationType;

public class TransmutationSpell extends SpellData {

    @JsonPropertyWithDefault
    private SetData<Material> transmutableBlocks = new SetData<>();
    @JsonPropertyWithDefault
    @JsonAlias("resultantBlock")
    private BlockData block = Bukkit.createBlockData(Material.REDSTONE_BLOCK);

    @Override
    public boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace) {
        if (clickedBlock != null) {
            if (transmutableBlocks.inSet(clickedBlock.getType())) {
                clickedBlock.setBlockData(block);
                return true;
            }
        }
        return false;
    }
}
