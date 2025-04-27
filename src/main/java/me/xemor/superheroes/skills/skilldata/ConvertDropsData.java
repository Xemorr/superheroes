package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.xemor.configurationdata.ItemStackData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConvertDropsData extends SkillData {

    @JsonPropertyWithDefault
    @JsonAlias("convertMap")
    private Map<Material, ItemStack> dropToNewDrop = new HashMap<>();
    @JsonPropertyWithDefault
    private List<Material> ignoredBlocks = Collections.emptyList();
    @JsonPropertyWithDefault
    private boolean ignoreSilkTouch = false;

    public boolean shouldIgnoreSilkTouch() {
        return ignoreSilkTouch;
    }

    public Map<Material, ItemStack> getDropToNewDrop() {
        return dropToNewDrop;
    }

    public List<Material> getIgnoredBlocks() {
        return ignoredBlocks;
    }
}