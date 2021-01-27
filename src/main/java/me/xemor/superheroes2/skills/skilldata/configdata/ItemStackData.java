package me.xemor.superheroes2.skills.skilldata.configdata;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ItemStackData {

    private ItemStack item;

    public ItemStackData(ConfigurationSection configurationSection) {

    }

    public ItemStack getItem()
    {
        return item;
    }

}
