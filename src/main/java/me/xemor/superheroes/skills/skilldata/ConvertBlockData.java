package me.xemor.superheroes.skills.skilldata;

import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.stream.Collectors;

public class ConvertBlockData extends CooldownData {

    private final List<Material> inputBlocks;
    private final Material outputBlock;

    public ConvertBlockData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection, "There are <s> seconds before you can transmutate the block again!", 0);
        outputBlock = Material.valueOf(configurationSection.getString("outputBlock", "GOLD_BLOCK"));
        inputBlocks = configurationSection.getStringList("inputBlocks").stream().map(Material::valueOf).collect(Collectors.toList());
    }

    public List<Material> getInputBlocks() {
        return inputBlocks;
    }

    public Material getOutputBlock() {
        return outputBlock;
    }
}
