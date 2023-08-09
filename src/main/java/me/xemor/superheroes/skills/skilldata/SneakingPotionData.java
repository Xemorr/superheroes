package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.PotionEffectData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SneakingPotionData extends SkillData {

    private final PotionEffectData potionData;

    public SneakingPotionData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        potionData = new PotionEffectData(configurationSection, PotionEffectType.REGENERATION, 100000000, 0);

    }

    public PotionEffect getPotionEffect() {
        return potionData.getPotionEffect();
    }

}
