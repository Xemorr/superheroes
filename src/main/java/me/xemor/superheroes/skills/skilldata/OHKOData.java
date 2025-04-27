package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.stream.Collectors;

public class OHKOData extends SkillData {

    @JsonPropertyWithDefault
    @JsonAlias("entitytypes")
    private SetData<EntityType> entityTypes;
    @JsonAlias("displayname")
    private String displayName;

    public boolean inEntityTypes(EntityType entityType) {
        return entityTypes.inSet(entityType);
    }

    public String getDisplayName() {
        return displayName;
    }

}
