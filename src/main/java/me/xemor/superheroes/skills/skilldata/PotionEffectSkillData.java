package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.PotionEffectData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class PotionEffectSkillData extends SkillData {

    final PotionEffectData potionData;

    public PotionEffectSkillData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        potionData = new PotionEffectData(configurationSection);
    }

    public Optional<PotionEffect> getPotionEffect() {
        return potionData.getPotionEffect();
    }

}
