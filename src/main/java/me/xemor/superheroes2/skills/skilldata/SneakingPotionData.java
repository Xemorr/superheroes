package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.skilldata.configdata.PotionEffectData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SneakingPotionData extends SkillData {

    PotionEffectData potionData;

    public SneakingPotionData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        potionData = new PotionEffectData(configurationSection, PotionEffectType.REGENERATION, 100000000, 0);

    }

    public PotionEffect getPotionEffect() {
        return potionData.getPotionEffect();
    }

}
