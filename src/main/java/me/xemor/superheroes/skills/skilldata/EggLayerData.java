package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.xemor.configurationdata.Duration;
import me.xemor.configurationdata.ItemStackData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class EggLayerData extends SkillData {

    @JsonUnwrapped
    private ItemStackData toLay = null;
    @JsonPropertyWithDefault
    private Duration tickDelay = new Duration(15D);

    public ItemStack getToLay() {
        return toLay == null ? new ItemStack(Material.EGG) : toLay.item();
    }

    public long getTickDelay() {
        return tickDelay.getDurationInTicks().orElse(15 * 20L);
    }
}
