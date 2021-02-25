package me.xemor.superheroes2.skills.skilldata.configdata;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMetaData {

    private ItemMeta itemMeta;

    public ItemMetaData(ConfigurationSection configurationSection, ItemMeta baseMeta) {
        itemMeta = baseMeta.clone();
        String displayName = configurationSection.getString("displayName");
        if (displayName != null) {
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }
        List<String> lore = configurationSection.getStringList("lore");
        lore = lore.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList());
        itemMeta.setLore(lore);
        boolean isUnbreakable = configurationSection.getBoolean("isUnbreakable", false);
        itemMeta.setUnbreakable(isUnbreakable);
    }

    public ItemMeta getItemMeta()
    {
        return itemMeta;
    }

}
