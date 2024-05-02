package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.PotionEffectData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConsumeSkillData extends SkillData {

    private final Material material;
    private final int hunger;
    private PotionEffectData potionData;

    public ConsumeSkillData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        if (configurationSection.contains("type")) {
            potionData = new PotionEffectData(configurationSection);
        }
        material = Material.valueOf(configurationSection.getString("material", "DIRT"));
        hunger = configurationSection.getInt("hunger", 0);
    }

    public int getHunger() {
        return hunger;
    }

    public Material getMaterial() {
        return material;
    }

    public Optional<PotionEffect> getPotionEffect() {
        return potionData.getPotionEffect();
    }
}
