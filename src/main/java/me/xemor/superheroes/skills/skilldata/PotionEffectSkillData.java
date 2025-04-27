package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.xemor.configurationdata.PotionEffectData;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;

public class PotionEffectSkillData extends SkillData {

    @JsonUnwrapped
    private PotionEffectData potionData;

    public PotionEffectSkillData() {}

    public Optional<PotionEffect> getPotionEffect() {
        if (potionData == null) return Optional.empty();
        else return potionData.createPotion();
    }

}
