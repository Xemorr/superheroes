package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.ItemStackData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ConvertItemData extends SkillData {

    @JsonPropertyWithDefault
    private ItemStack inputItem;
    @JsonPropertyWithDefault
    private ItemStack outputItem;
    @JsonPropertyWithDefault
    private double chance = 1;

    public ItemStack getInputItem() {
        return inputItem;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public double getChance() {
        return chance;
    }
}
