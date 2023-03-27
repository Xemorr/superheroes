package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

public class HeartStealData extends SkillData {

    private int heartsGained;
    private SetData<EntityType> entities;
    private int maxHearts;

    public HeartStealData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        heartsGained = configurationSection.getInt("heartsgained", 2);
        maxHearts = configurationSection.getInt("maxhearts", 24);
        entities = new SetData<>(EntityType.class, "entities", configurationSection);
    }

    public int getHeartsGained() {
        return heartsGained;
    }

    public SetData<EntityType> getEntities() {
        return entities;
    }

    public int getMaxHearts() {
        return maxHearts;
    }
}
