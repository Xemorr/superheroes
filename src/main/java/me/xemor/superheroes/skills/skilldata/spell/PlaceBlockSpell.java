package me.xemor.superheroes.skills.skilldata.spell;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlaceBlockSpell extends SpellData {

    @JsonPropertyWithDefault
    private BlockData block;

    @Override
    public boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace) {
        if (clickedBlock != null && blockFace != null) {
            Block placeHere = clickedBlock.getRelative(blockFace);
            placeHere.setBlockData(block);
            return true;
        }
        return false;
    }
}
