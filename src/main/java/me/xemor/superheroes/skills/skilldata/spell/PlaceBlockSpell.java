package me.xemor.superheroes.skills.skilldata.spell;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.superheroes.Superheroes;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.libraries.cloplib.operation.OperationType;
import org.lushplugins.unifiedprotection.bukkit.BukkitUnifiedProtection;
import org.lushplugins.unifiedprotection.position.GenericOperationPosition;

public class PlaceBlockSpell extends SpellData {

    @JsonPropertyWithDefault
    private BlockData block;

    @Override
    public boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace) {
        if (clickedBlock != null && blockFace != null) {
            Block placeHere = clickedBlock.getRelative(blockFace);
            if (shouldRespectProtectionPlugins()) {
                boolean shouldPlace = Superheroes.getUnifiedProtection().isOperationAllowed(OperationType.BLOCK_PLACE, placeHere.getLocation(), player);
                if (shouldPlace) {
                    placeHere.setBlockData(block);
                    return true;
                }
            }
            else {
                placeHere.setBlockData(block);
                return true;
            }
        }
        return false;
    }
}
