package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

public class KillPotionData extends PotionEffectSkillData {

    private final SetData<EntityType> entities;

    public KillPotionData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        entities = new SetData<>(EntityType.class, "entities", configurationSection);
    }

    public SetData<EntityType> getEntities() {
        return entities;
    }
}
