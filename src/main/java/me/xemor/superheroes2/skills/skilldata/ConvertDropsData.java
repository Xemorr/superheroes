package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.configdata.ItemStackData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConvertDropsData extends SkillData {

    Map<Material, ItemStack> dropToNewDrop = new HashMap<>();
    List<Material> ignoredBlocks;

    protected ConvertDropsData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        final ConfigurationSection convertMapSection = configurationSection.getConfigurationSection("convertMap");
        for (Map.Entry<String, Object> mappings : convertMapSection.getValues(false).entrySet()) {
            final Material material = Material.valueOf(mappings.getKey());
            final ConfigurationSection itemSection = (ConfigurationSection) mappings.getValue();
            final ItemStack resultantItem = new ItemStackData(itemSection).getItem();
            dropToNewDrop.put(material, resultantItem);
        }
        ignoredBlocks = configurationSection.getStringList("ignoredBlocks").stream().map(Material::valueOf).collect(Collectors.toList());
    }

    public Map<Material, ItemStack> getDropToNewDrop() {
        return dropToNewDrop;
    }

    public List<Material> getIgnoredBlocks() {
        return ignoredBlocks;
    }
}
