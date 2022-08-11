package me.xemor.superheroes.skills.skilldata.Spell;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.stream.Collectors;

public class TransmutationData {

    private List<Material> transmutableBlocks;
    private Material resultantBlock;

    public TransmutationData(ConfigurationSection configurationSection) {
        resultantBlock = Material.valueOf(configurationSection.getString("resultantBlock", "REDSTONE_BLOCK"));
        transmutableBlocks = configurationSection.getStringList("transmutableBlocks").stream().map(Material::valueOf).collect(Collectors.toList());
    }

    public List<Material> getTransmutatableBlocks() {
        return transmutableBlocks;
    }

    public Material getResultantBlock() {
        return resultantBlock;
    }

}
