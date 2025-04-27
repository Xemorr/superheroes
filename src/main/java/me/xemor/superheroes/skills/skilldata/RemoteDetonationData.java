package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.stream.Collectors;

public class RemoteDetonationData extends CooldownData {

    @JsonPropertyWithDefault
    private boolean spawnsFire = false;
    @JsonPropertyWithDefault
    private boolean breakBlocks = true;
    @JsonPropertyWithDefault
    private boolean removeDetonatedEntity = true;
    @JsonPropertyWithDefault
    private float explosionStrength = 1;
    @JsonPropertyWithDefault
    private SetData<EntityType> explodable = new SetData<>();

    public float getExplosionStrength() {
        return explosionStrength;
    }

    public boolean isExplodable(EntityType entityType) {
        return explodable.inSet(entityType);
    }

    public boolean spawnsFire() {
        return spawnsFire;
    }

    public boolean breakBlocks() {
        return breakBlocks;
    }

    public boolean removeDetonatedEntity() {
        return removeDetonatedEntity;
    }
}
