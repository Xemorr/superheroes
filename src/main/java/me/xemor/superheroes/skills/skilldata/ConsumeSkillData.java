package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.PotionEffectData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConsumeSkillData extends SkillData {

    @JsonPropertyWithDefault
    private Material material = Material.DIRT;
    @JsonPropertyWithDefault
    private int hunger = 0;
    @JsonUnwrapped
    private PotionEffectData potionData;

    public int getHunger() {
        return hunger;
    }

    public Material getMaterial() {
        return material;
    }

    public Optional<PotionEffect> getPotionEffect() {
        if (potionData == null) return Optional.empty();
        else return potionData.createPotion();
    }
}
