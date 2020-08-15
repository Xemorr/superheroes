package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.List;

public class InstantBreakData extends SkillData {

    private HashSet<Material> instantBreakMaterials = new HashSet<>();
    private Material breakUsing;

    protected InstantBreakData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        List<String> instantBreakMaterialsStr = configurationSection.getStringList("blocks");
        if (instantBreakMaterialsStr == null) {
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
