package me.xemor.superheroes.skills.skilldata;

import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.skills.skilldata.configdata.Cooldown;
import me.xemor.superheroes.skills.skilldata.spell.Spell;
import me.xemor.superheroes.skills.skilldata.spell.Spells;
import me.xemor.superheroes.skills.skilldata.spell.TransmutationSpell;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.stream.Collectors;

public class SpellData extends SkillData implements Cooldown {

    private final Spell spell;
    private final Material fuel;
    private final String spellName;
    private final String displayName;
    private String cooldownMessage;
    private String moreFuelMessage;
    private final double cooldown;
    private final int cost;
    private final List<String> lore;


    public SpellData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        int spellID = Spells.getSpell(configurationSection.getString("spell", "SNOWBALL"));
        if (spellID == -1) {
            Superheroes.getInstance().getLogger().severe("Invalid spell entered at " + configurationSection.getCurrentPath() + ".spell");
        }
        spell = Spells.createSpell(spellID, configurationSection);
        fuel = Material.valueOf(configurationSection.getString("fuel", "REDSTONE"));
        cooldown = configurationSection.getDouble("cooldown", 1);
        cost = configurationSection.getInt("cost", 1);
        spellName = configurationSection.getString("spellName", spell.toString());
        cooldownMessage = configurationSection.getString("cooldownMessage", "<spellName> has <currentcooldown> seconds remaining.");
        // This is necessary as we can't use the replaceVariables function for cooldownMessage due to it being used by the SkillCooldownHandler later
        cooldownMessage = cooldownMessage.replace("<spellName>", spellName);
        moreFuelMessage = configurationSection.getString("moreFuelMessage", "This spell needs <fuelneeded> more <fuel>");
        final String displayNameFormat = configurationSection.getString("displayNameFormat", "<purple><spellName>");
        displayName = replaceVariables(displayNameFormat);
        final List<String> loreFormat = configurationSection.getStringList("loreFormat");
        lore = loreFormat.stream()
                .map(this::replaceVariables)
                .collect(Collectors.toList());
    }

    private String replaceVariables(String input) {
        return LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().build().serialize(MiniMessage.miniMessage().deserialize(input,
                Placeholder.unparsed("spellname", spellName),
                Placeholder.unparsed("cooldown", String.valueOf(cooldown)),
                Placeholder.unparsed("fuel", String.valueOf(fuel)),
                Placeholder.unparsed("cost", String.valueOf(cost))));
    }

    public Spell getSpell() {
        return spell;
    }

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
        return lore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMoreFuelMessage() {
        return moreFuelMessage;
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }
}
