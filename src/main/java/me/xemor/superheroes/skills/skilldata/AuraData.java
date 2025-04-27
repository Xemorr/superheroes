package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.xemor.configurationdata.CompulsoryJsonProperty;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.PotionEffectData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AuraData extends SkillData {

    @JsonPropertyWithDefault
    private double radius = 5;
    @JsonUnwrapped
    private PotionEffectData potionData = null;

    public Optional<PotionEffect> getPotionEffect() {
        if (potionData == null) return Optional.empty();
        else return potionData.createPotion();
    }

    public double getDiameter() {
        return radius * 2;
    }
}
