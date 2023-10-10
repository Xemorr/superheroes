package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.PotionEffectData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AuraData extends SkillData {

    private final double diameter;
    @NotNull
    private final PotionEffectData potionData;

    public AuraData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        diameter = configurationSection.getDouble("radius", 5) * 2;
        potionData = new PotionEffectData(configurationSection);
    }

    public Optional<PotionEffect> getPotionEffect() {
        return potionData.getPotionEffect();
    }

    public double getDiameter() {
        return diameter;
    }
}
