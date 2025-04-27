package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.xemor.configurationdata.PotionEffectData;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SneakingPotionData extends SkillData {

    @JsonUnwrapped
    private PotionEffectData potionData = null;

    public Optional<PotionEffect> getPotionEffect() {
        if (potionData == null) return Optional.empty();
        else return potionData.createPotion();
    }

}
