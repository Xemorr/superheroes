package me.xemor.superheroes2.skills.skilldata.Spell;

import me.xemor.superheroes2.skills.skilldata.SkillData;
import me.xemor.superheroes2.skills.skilldata.configdata.Cooldown;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.stream.Collectors;

public class SpellData extends SkillData implements Cooldown {

    private Spell spell;
    private Material fuel;
    private String spellName;
    private String displayName;
    private String cooldownMessage;
    private String moreFuelMessage;
    private double cooldown;
    private int cost;
    private List<String> lore;
    private TransmutationData transmutationData;


    public SpellData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        spell = Spell.valueOf(configurationSection.getString("spell", "SNOWBALL"));
        fuel = Material.valueOf(configurationSection.getString("fuel", "REDSTONE"));
        cooldown = configurationSection.getDouble("cooldown", 1);
        cost = configurationSection.getInt("cost", 1);
        spellName = configurationSection.getString("spellName", spell.toString());
        cooldownMessage = configurationSection.getString("cooldownMessage", "%spellName% has %currentcooldown% seconds remaining.");
        cooldownMessage = replaceVariables(cooldownMessage);
        moreFuelMessage = configurationSection.getString("moreFuelMessage", "This spell needs %fuelneeded% more %fuel%");
        moreFuelMessage = replaceVariables(moreFuelMessage);
        final String displayNameFormat = ChatColor.translateAlternateColorCodes('&', configurationSection.getString("displayNameFormat", "&5%spellName%"));
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
        input = ChatColor.translateAlternateColorCodes('&', input);
        input = input.replaceAll("%spellName%", spellName);
        input = input.replaceAll("%cooldown%", Double.toString(cooldown));
        input = input.replaceAll("%fuel%", fuel.toString().toLowerCase());
        input = input.replaceAll("%cost%", Integer.toString(cost));
        return input;
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
