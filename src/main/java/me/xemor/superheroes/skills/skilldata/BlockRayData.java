package me.xemor.superheroes.skills.skilldata;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BlockRayData extends SkillData {

    private final int maxDistance;
    private final boolean shouldRevert;
    private final List<Material> blocksToPlace;
    private final List<Material> blocksToReplace;
    private final BlockRayMode blockRayMode;
    private final long revertsAfter;


    public BlockRayData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        blocksToPlace = configurationSection.getStringList("blocksToPlace").stream().map(Material::valueOf).collect(Collectors.toList());
        blocksToReplace = configurationSection.getStringList("blocksToReplace").stream().map(Material::valueOf).collect(Collectors.toList());
        blockRayMode = BlockRayMode.valueOf(configurationSection.getString("blockRayMode"));
        maxDistance = configurationSection.getInt("maxDistance", 20);
        shouldRevert = configurationSection.getBoolean("shouldRevert", false);
        revertsAfter = Math.round(configurationSection.getLong("revertsAfter", 15L) * 20);
    }

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
        return revertsAfter;
    }

    public boolean shouldRevert() {
        return shouldRevert;
    }

}
