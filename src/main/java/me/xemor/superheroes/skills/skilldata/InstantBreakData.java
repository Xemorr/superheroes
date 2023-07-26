package me.xemor.superheroes.skills.skilldata;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.List;

public class InstantBreakData extends SkillData {

    private HashSet<Material> instantBreakMaterials = new HashSet<>();
    private final Material breakUsing;

    public InstantBreakData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        List<String> instantBreakMaterialsStr = configurationSection.getStringList("blocks");
        if (instantBreakMaterialsStr.isEmpty()) {
            instantBreakMaterials = null;
        }
        else {
            for (String materialStr : instantBreakMaterialsStr) {
                instantBreakMaterials.add(Material.valueOf(materialStr));
            }
        }
        breakUsing = Material.valueOf(configurationSection.getString("breakUsing"));
    }

    public boolean canBreak(Material type) {
        if (instantBreakMaterials != null) {
            return instantBreakMaterials.contains(type);
        }
        return true;
    }

    public Material getBreakUsing() {
        return breakUsing;
    }
}
