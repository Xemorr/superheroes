package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.xemor.configurationdata.CompulsoryJsonProperty;
import me.xemor.configurationdata.ItemStackData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class BlockDropsData extends SkillData {

    @JsonPropertyWithDefault
    @JsonAlias("convertMap")
    private Multimap<Material, ItemStack> dropToNewDrop = HashMultimap.create();
    @JsonPropertyWithDefault
    private boolean replaceDrops = false;

    /*
    public BlockDropsData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        final ConfigurationSection convertMapSection = configurationSection.getConfigurationSection("convertMap");
        replaceDrops = configurationSection.getBoolean("replaceDrops", false);
        for (Map.Entry<String, Object> mappings : convertMapSection.getValues(false).entrySet()) {
            final Material material = Material.valueOf(mappings.getKey());
            final ConfigurationSection materialSection = (ConfigurationSection) mappings.getValue();
            for (Object itemObject : materialSection.getValues(false).values()) {
                ConfigurationSection itemSection = (ConfigurationSection) itemObject;
                final ItemStack resultantItem = new ItemStackData(itemSection).getItem();
                dropToNewDrop.put(material, resultantItem);
            }
        }
    }
     */

    public Collection<ItemStack> getDrops(Material materialBeingBroken) {
        return dropToNewDrop.get(materialBeingBroken);
    }

    public boolean shouldReplaceDrops() {
        return replaceDrops;
    }
}
