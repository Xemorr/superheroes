package me.xemor.superheroes.skills.skilldata.spell;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.superheroes.skills.skilldata.SkillData;
import me.xemor.superheroes.skills.skilldata.configdata.Cooldown;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "skill")
public abstract class SpellData extends SkillData implements Cooldown {

    @JsonPropertyWithDefault
    private Material fuel = Material.REDSTONE;
    @JsonPropertyWithDefault
    private String spellName = this.getClass().getName();
    @JsonPropertyWithDefault
    @JsonAlias("displayNameFormat")
    private String displayName = "<purple><spellName>";
    @JsonPropertyWithDefault
    private String cooldownMessage = "<spellName> has <currentcooldown> seconds remaining.";
    @JsonPropertyWithDefault
    private String moreFuelMessage = "This spell needs <fuelneeded> more <fuel>";
    @JsonPropertyWithDefault
    private double cooldown = 1;
    @JsonPropertyWithDefault
    private int cost = 1;
    @JsonPropertyWithDefault
    @JsonAlias("loreFormat")
    private List<String> lore = List.of();

    private String replaceVariables(String input) {
        return LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().build().serialize(MiniMessage.miniMessage().deserialize(input,
                Placeholder.unparsed("spellname", spellName),
                Placeholder.unparsed("cooldown", String.valueOf(cooldown)),
                Placeholder.unparsed("fuel", String.valueOf(fuel)),
                Placeholder.unparsed("cost", String.valueOf(cost))));
    }

    public abstract boolean castSpell(Player player, @Nullable Block clickedBlock, @Nullable BlockFace blockFace);

    public Material getFuel() {
        return fuel;
    }

    public double getCooldown() {
        return cooldown;
    }

    public int getCost() {
        return cost;
    }

    public String getSpellName() {
        return spellName;
    }

    public List<String> getLore() {
        return lore.stream().map(this::replaceVariables).toList();
    }

    public String getDisplayName() {
        return replaceVariables(displayName);
    }

    public String getMoreFuelMessage() {
        return moreFuelMessage;
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }
}
