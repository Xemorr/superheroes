package me.xemor.superheroes2.skills.skilldata.configdata;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.Damageable;
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
        int durability = configurationSection.getInt("durability", 0);
        if (durability != 0 && itemMeta instanceof Damageable) {
            Damageable damageable = (Damageable) itemMeta;
            damageable.setDamage(durability);
        }
        ConfigurationSection attributeSection = configurationSection.getConfigurationSection("attributes");
        if (attributeSection != null) {
            AttributeData attributeData = new AttributeData(attributeSection);
            attributeData.applyAttributes(itemMeta);
        }
        ConfigurationSection enchantSection = configurationSection.getConfigurationSection("enchants");
        if (enchantSection != null) {
            EnchantmentData enchantmentData = new EnchantmentData(enchantSection);
            enchantmentData.applyEnchantments(itemMeta);
        }
    }

    public ItemMeta getItemMeta()
    {
        return itemMeta;
    }

}
