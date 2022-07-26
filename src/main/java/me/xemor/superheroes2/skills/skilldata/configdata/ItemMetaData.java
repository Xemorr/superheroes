package me.xemor.superheroes2.skills.skilldata.configdata;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMetaData {

    private final ItemMeta itemMeta;
    private final static LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build();


    public ItemMetaData(ConfigurationSection configurationSection, ItemMeta baseMeta) {
        itemMeta = baseMeta.clone();
        String displayName = configurationSection.getString("displayName");
        if (displayName != null) {
            itemMeta.setDisplayName(legacySerializer.serialize(MiniMessage.miniMessage().deserialize(displayName)));
        }
        List<String> lore = configurationSection.getStringList("lore");
        lore = lore.stream().map(string -> legacySerializer.serialize(MiniMessage.miniMessage().deserialize(string))).collect(Collectors.toList());
        itemMeta.setLore(lore);
        boolean isUnbreakable = configurationSection.getBoolean("isUnbreakable", false);
        itemMeta.setUnbreakable(isUnbreakable);
        int durability = configurationSection.getInt("durability", 0);
        if (durability != 0 && itemMeta instanceof Damageable damageable) damageable.setDamage(durability);

        ConfigurationSection attributeSection = configurationSection.getConfigurationSection("attributes");
        if (attributeSection != null) {
            ItemAttributeData itemAttributeData = new ItemAttributeData(attributeSection);
            itemAttributeData.applyAttributes(itemMeta);
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
