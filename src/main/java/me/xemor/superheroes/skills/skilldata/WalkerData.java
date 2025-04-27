package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.Duration;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WalkerData extends SkillData {

    @JsonPropertyWithDefault
    private List<Material> blocksToPlace = new ArrayList<>();
    @JsonPropertyWithDefault
    private Set<Material> blocksToReplace = new HashSet<>();
    @JsonPropertyWithDefault
    private SetData<Material> blocksToPlaceOn = new SetData<>();
    @JsonPropertyWithDefault
    @JsonAlias({"sneaking", "issneaking"})
    private boolean isSneaking = false;
    @JsonPropertyWithDefault
    private boolean blocksDrop = true;
    @JsonPropertyWithDefault
    private boolean shouldRevert = false;
    @JsonPropertyWithDefault
    private boolean aboveFloor = false;
    @JsonPropertyWithDefault
    private boolean canPlaceFloating = true;
    @JsonPropertyWithDefault
    private Duration revertsAfter = new Duration(15D);

    public Material getReplacementBlock() {
        return blocksToPlace.get(ThreadLocalRandom.current().nextInt(blocksToPlace.size()));
    }

    public boolean shouldReplace(Material material) {
        return blocksToReplace.contains(material);
    }

    public boolean isSneaking() {
        return isSneaking;
    }

    public boolean doesBlockDrop() {
        return blocksDrop;
    }

    public boolean shouldRevert() {
        return shouldRevert;
    }

    public long getRevertsAfter() {
        return revertsAfter.getDurationInTicks().orElse(15L * 20);
    }

    public boolean isAboveFloor() {
        return aboveFloor;
    }

    public boolean canPlaceFloating() {
        return canPlaceFloating;
    }

    public boolean canPlaceOn(Material type) {
        return blocksToPlaceOn.inSet(type);
    }
}
