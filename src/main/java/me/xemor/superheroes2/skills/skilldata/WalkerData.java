package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.skills.Skill;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class WalkerData extends SkillData {

    Random random = new Random();
    List<Material> blocksToPlace = new ArrayList<>();
    HashSet<Material> blocksToReplace = new HashSet<>();
    boolean isSneaking;
    boolean blocksDrop;
    boolean shouldRevert;
    long revertsAfter;

    protected WalkerData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        List<String> blocksToReplaceStr = configurationSection.getStringList("blocksToReplace");
        for (String blockToReplaceStr : blocksToReplaceStr) {
            blocksToReplace.add(Material.valueOf(blockToReplaceStr));
        }
        List<String> blocksToPlaceStr = configurationSection.getStringList("blocksToPlace");
        for (String blockToPlaceStr : blocksToPlaceStr) {
            blocksToPlace.add(Material.valueOf(blockToPlaceStr));
        }
        isSneaking = configurationSection.getBoolean("isSneaking", false);
        blocksDrop = configurationSection.getBoolean("blocksDrop", true);
        shouldRevert = configurationSection.getBoolean("shouldRevert", false);
        revertsAfter = Math.round(configurationSection.getDouble("revertsAfter", 15) * 20);
    }

    public Material getReplacementBlock() {
        return blocksToPlace.get(random.nextInt(blocksToPlace.size()));
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
        return revertsAfter;
    }
}
