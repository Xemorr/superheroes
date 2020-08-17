package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class AerosurferData extends SkillData {

    Material block = Material.GLASS;

    protected AerosurferData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        String blockStr = configurationSection.getString("block");
        if (blockStr != null) {
            block = Material.valueOf(blockStr);
        }
    }

    public Material getBlock() {
        return block;
    }
}
