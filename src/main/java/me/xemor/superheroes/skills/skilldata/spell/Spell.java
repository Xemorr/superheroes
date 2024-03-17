package me.xemor.superheroes.skills.skilldata.spell;

import me.xemor.configurationdata.ConfigurationData;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public abstract class Spell {

    private final int spell;

    public Spell(int spell, ConfigurationSection section) {
        this.spell = spell;
    }

    public abstract boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace);

    public int getSpell() {
        return spell;
    }
}
