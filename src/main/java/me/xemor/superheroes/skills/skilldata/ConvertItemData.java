package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.ItemStackData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ConvertItemData extends SkillData {

    private ItemStack inputItem;
    private ItemStack outputItem;
    private final double chance;

    public ConvertItemData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        final ConfigurationSection inputItemSection = configurationSection.getConfigurationSection("inputItem");
        if (inputItemSection != null) {
            inputItem = new ItemStackData(inputItemSection).getItem();
        }
        final ConfigurationSection outputItemSection = configurationSection.getConfigurationSection("outputItem");
        if (outputItemSection != null) {
            outputItem = new ItemStackData(outputItemSection).getItem();
        }
        chance = configurationSection.getDouble("chance", 1);
    }

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
