package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.Material;

public class ConvertBlockData extends CooldownData {

    @JsonPropertyWithDefault
    private SetData<Material> inputBlocks = new SetData<>();
    @JsonPropertyWithDefault
    private Material outputBlock = Material.GOLD_BLOCK;

    public boolean isInputBlock(Material material) {
        return inputBlocks.inSet(material);
    }

    public Material getOutputBlock() {
        return outputBlock;
    }
}
