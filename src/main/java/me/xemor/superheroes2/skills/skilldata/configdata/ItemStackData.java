package me.xemor.superheroes2.skills.skilldata.configdata;

import me.xemor.superheroes2.Superheroes2;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemStackData {

    private ItemStack item;

    public ItemStackData(ConfigurationSection configurationSection) {
        Material material = Material.valueOf(configurationSection.getString("type", "STONE").toUpperCase());
        int amount = configurationSection.getInt("amount", 1);
        item = new ItemStack(material, amount);
        ConfigurationSection metadataSection = configurationSection.getConfigurationSection("metadata");
        item.setItemMeta(Bukkit.getItemFactory().getItemMeta(material));
        if (metadataSection != null) {
            ItemMetaData itemMetaData = new ItemMetaData(metadataSection, item.getItemMeta());
            item.setItemMeta(itemMetaData.getItemMeta());
        }
        long specialID = configurationSection.getLong("specialID", -1);
        if (specialID != -1) {
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(JavaPlugin.getPlugin(Superheroes2.class), "specialID"), PersistentDataType.LONG, specialID);
            item.setItemMeta(itemMeta);
        }
    }

    public ItemStack getItem()
    {
        return item;
    }

}
