package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.Material;

public class InstantBreakData extends SkillData {

    @JsonPropertyWithDefault
    @JsonAlias("blocks")
    private SetData<Material> instantBreakable = new SetData<>();
    @JsonPropertyWithDefault
    @JsonAlias({"breakusing", "breakUsing"})
    private Material simulateBreakingUsing;

    public boolean canBreak(Material type) {
        return instantBreakable.inSet(type);
    }

    public Material getSimulateBreakingUsing() {
        return simulateBreakingUsing;
    }
}
