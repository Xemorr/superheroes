package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.skilldata.configdata.PotionEffectData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AuraData extends SkillData {

    private double diameter;
    private PotionEffectData potionData;

    public AuraData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        diameter = configurationSection.getDouble("radius", 5) * 2;
        potionData = new PotionEffectData(configurationSection, PotionEffectType.REGENERATION, 200, 0);
    }

    public PotionEffect getPotionEffect() {
        return potionData.getPotionEffect();
    }

    public double getDiameter() {
        return diameter;
    }
}
