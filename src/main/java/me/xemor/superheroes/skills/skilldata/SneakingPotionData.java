package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.PotionEffectData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class SneakingPotionData extends SkillData {

    private final PotionEffectData potionData;

    public SneakingPotionData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        potionData = new PotionEffectData(configurationSection);
    }

    public Optional<PotionEffect> getPotionEffect() {
        return potionData.getPotionEffect().map((e) -> new PotionEffect(e.getType(), 20000000, e.getAmplifier()));
    }

}
