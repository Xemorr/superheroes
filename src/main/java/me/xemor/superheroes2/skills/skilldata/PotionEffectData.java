package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectData extends SkillData {

    PotionEffect potionEffect;

    protected PotionEffectData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        Integer tempPotency = configurationSection.getInt("potency");
        int potency = 0;
        if (tempPotency != null) {
            potency = tempPotency - 1;
        }
        PotionEffectType type = PotionEffectType.getByName(configurationSection.getString("type").toUpperCase());
        Integer duration = configurationSection.getInt("duration");
        if (type.isInstant()) {
            potionEffect = new PotionEffect(type, 1, potency);
        }
        else if (duration != null) {
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
