package me.xemor.superheroes2.skills.skilldata;

import me.xemor.superheroes2.ParticleData;
import me.xemor.superheroes2.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportData extends SkillData {

    private boolean leftClick;
    private int distance;
    private double cooldown;
    private double yAxisMultiplier;
    private PlayerTeleportEvent.TeleportCause teleportCause;
    private ParticleData particleData;
    private String teleportCooldownMessage;
    private Material teleportItem;

    protected TeleportData(Skill skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        leftClick = configurationSection.getBoolean("leftClick", true);
        distance = configurationSection.getInt("distance", 30);
        yAxisMultiplier = configurationSection.getDouble("yAxisDistanceMultiplier", 1);
        teleportCause = PlayerTeleportEvent.TeleportCause.valueOf(configurationSection.getString("teleportCause", "ENDER_PEARL"));
        cooldown = configurationSection.getDouble("cooldown", 10D);
        teleportCooldownMessage = ChatColor.translateAlternateColorCodes('&', configurationSection.getString("teleportCooldownMessage","&5&lTeleport &7has %s seconds until it can be used again!"));
        ConfigurationSection particleSection = configurationSection.getConfigurationSection("particle");
        if (particleSection == null) {
            particleSection = configurationSection;
        }
        particleData = new ParticleData(particleSection);
        teleportItem = Material.valueOf(configurationSection.getString("teleportItem", "AIR"));
    }

    public boolean isLeftClick() {
        return leftClick;
    }

    public int getDistance() {
        return distance;
    }

    public double getCooldown() {
        return cooldown;
    }

    public double getyAxisMultiplier() {
        return yAxisMultiplier;
    }

    public PlayerTeleportEvent.TeleportCause getTeleportCause() {
        return teleportCause;
    }

    public String getTeleportCooldownMessage() {
        return teleportCooldownMessage;
    }

    public ParticleData getParticleData() {
        return particleData;
    }

    public Material getTeleportItem() {
        return teleportItem;
    }
}
