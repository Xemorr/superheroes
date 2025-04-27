package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

public class KillPotionData extends PotionEffectSkillData {

    @JsonPropertyWithDefault
    private SetData<EntityType> entities;

    public SetData<EntityType> getEntities() {
        return entities;
    }
}
