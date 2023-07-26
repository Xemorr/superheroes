package me.xemor.superheroes.skills.skilldata;

import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import me.xemor.superheroes.skills.skilldata.configdata.ParticleData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportData extends CooldownData {

    private final boolean leftClick;
    private final int distance;
    private final double yAxisMultiplier;
    private final PlayerTeleportEvent.TeleportCause teleportCause;
    private final ParticleData particleData;
    private final Material teleportItem;

    public TeleportData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection, "<purple><bold>Teleport <grey>has <s> seconds until it can be used again!", 10);
        leftClick = configurationSection.getBoolean("leftClick", true);
        distance = configurationSection.getInt("distance", 30);
        yAxisMultiplier = configurationSection.getDouble("yAxisDistanceMultiplier", 1);
        teleportCause = PlayerTeleportEvent.TeleportCause.valueOf(configurationSection.getString("teleportCause", "ENDER_PEARL"));
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

    public double getyAxisMultiplier() {
        return yAxisMultiplier;
    }

    public PlayerTeleportEvent.TeleportCause getTeleportCause() {
        return teleportCause;
    }

    public ParticleData getParticleData() {
        return particleData;
    }

    public Material getTeleportItem() {
        return teleportItem;
    }
}
