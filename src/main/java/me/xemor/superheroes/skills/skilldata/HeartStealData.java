package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

public class HeartStealData extends SkillData {

    @JsonPropertyWithDefault
    @JsonAlias("heartsgained")
    private int heartsGained = 2;
    @JsonPropertyWithDefault
    private SetData<EntityType> entities = new SetData<>();
    @JsonPropertyWithDefault
    @JsonAlias("maxhearts")
    private int maxHearts = 24;

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
