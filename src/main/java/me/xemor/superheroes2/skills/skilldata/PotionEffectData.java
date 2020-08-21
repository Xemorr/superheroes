package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectData extends SkillData {

    PotionEffect potionEffect;

    protected PotionEffectData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        int potency = configurationSection.getInt("potency", 0);
        if (potency != 0) {
            potency--;
        }
        PotionEffectType type = PotionEffectType.getByName(configurationSection.getString("type").toUpperCase());
        int duration = configurationSection.getInt("duration", 0);
        if (type.isInstant()) {
            potionEffect = new PotionEffect(type, 1, potency);
        }
        else if (duration != 0) {
            potionEffect = new PotionEffect(type, duration * 20, potency);
        }
        else {
            potionEffect = new PotionEffect(type, Integer.MAX_VALUE, potency);
        }
    }

    public PotionEffect getPotionEffect() {
        return potionEffect;
    }
}
