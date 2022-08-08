package me.xemor.superheroes2.skills.skilldata.Spell;

import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.configdata.Cooldown;
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
    private TransmutationData transmutationData;


    public SpellData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        spell = Spell.valueOf(configurationSection.getString("spell", "SNOWBALL"));
        fuel = Material.valueOf(configurationSection.getString("fuel", "REDSTONE"));
        cooldown = configurationSection.getDouble("cooldown", 1);
        cost = configurationSection.getInt("cost", 1);
        spellName = configurationSection.getString("spellName", spell.toString());
        cooldownMessage = configurationSection.getString("cooldownMessage", "<spellName> has <currentcooldown> seconds remaining.");
        cooldownMessage = replaceVariables(cooldownMessage);
        moreFuelMessage = configurationSection.getString("moreFuelMessage", "This spell needs <fuelneeded> more <fuel>");
        moreFuelMessage = replaceVariables(moreFuelMessage);
        final String displayNameFormat = configurationSection.getString("displayNameFormat", "<purple><spellName>");
        displayName = replaceVariables(displayNameFormat);
        final List<String> loreFormat = configurationSection.getStringList("loreFormat");
        lore = loreFormat.stream()
                .map(this::replaceVariables)
                .collect(Collectors.toList());
        final ConfigurationSection transmutationSection = configurationSection.getConfigurationSection("transmutationData");
        if (transmutationSection != null) {
            transmutationData = new TransmutationData(transmutationSection);
        }
    }

    private String replaceVariables(String input) {
        return LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().build().serialize(MiniMessage.miniMessage().deserialize(input,
                Placeholder.unparsed("spellName", spellName),
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

    public TransmutationData getTransmutationData() {
        return transmutationData;
    }

    public String getMoreFuelMessage() {
        return moreFuelMessage;
    }

    public String getCooldownMessage() {
        return cooldownMessage;
    }
}
