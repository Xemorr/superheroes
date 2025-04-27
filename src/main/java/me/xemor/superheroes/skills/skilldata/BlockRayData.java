package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.CompulsoryJsonProperty;
import me.xemor.configurationdata.Duration;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BlockRayData extends SkillData {

    @JsonPropertyWithDefault
    private int maxDistance = 20;
    @JsonPropertyWithDefault
    private boolean shouldRevert = false;
    @JsonPropertyWithDefault
    private List<Material> blocksToPlace = Collections.emptyList();
    @JsonPropertyWithDefault
    private List<Material> blocksToReplace = Collections.emptyList();
    @JsonPropertyWithDefault
    private BlockRayMode blockRayMode;
    @JsonPropertyWithDefault
    private Duration revertsAfter = new Duration(15D);

    public int getMaxDistance() {
        return maxDistance;
    }

    public List<Material> getBlocksToPlace() {
        return blocksToPlace;
    }

    public Material getRandomBlockToPlace() {
        Random random = new Random();
        int index = random.nextInt(blocksToPlace.size());
        return blocksToPlace.get(index);
    }

    public List<Material> getBlocksToReplace() {
        return blocksToReplace;
    }

    public BlockRayMode getBlockRayMode() {
        return blockRayMode;
    }

    public long getRevertsAfter() {
        return revertsAfter.getDurationInTicks().orElse(15 * 20L);
    }

    public boolean shouldRevert() {
        return shouldRevert;
    }

}
