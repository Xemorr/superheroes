package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.skilldata.configdata.PotionEffectData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectSkillData extends SkillData {

    PotionEffectData potionData;

    public PotionEffectSkillData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        potionData = new PotionEffectData(configurationSection, PotionEffectType.REGENERATION, 4, 0);

    }

    public PotionEffect getPotionEffect() {
        return potionData.getPotionEffect();
    }

}
