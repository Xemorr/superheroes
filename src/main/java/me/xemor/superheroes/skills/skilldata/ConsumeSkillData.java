package me.xemor.superheroes.skills.skilldata;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class ConsumeSkillData extends PotionEffectSkillData {

    private final Material material;
    private final int hunger;

    public ConsumeSkillData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        material = Material.valueOf(configurationSection.getString("material", "DIRT"));
        hunger = configurationSection.getInt("hunger", 0);
    }

    public int getHunger() {
        return hunger;
    }

    public Material getMaterial() {
        return material;
    }

}
