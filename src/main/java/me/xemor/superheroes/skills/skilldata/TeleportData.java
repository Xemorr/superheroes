package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.ItemComparisonData;
import me.xemor.configurationdata.particles.ParticleData;
import me.xemor.superheroes.skills.skilldata.configdata.CooldownData;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class TeleportData extends CooldownData {

    @JsonPropertyWithDefault
    @JsonAlias("action")
    private Set<Action> actions = Set.of(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK);
    @JsonPropertyWithDefault
    private int distance = 30;
    @JsonPropertyWithDefault
    private double yAxisMultiplier = 1;
    @JsonPropertyWithDefault
    private PlayerTeleportEvent.TeleportCause teleportCause = PlayerTeleportEvent.TeleportCause.ENDER_PEARL;
    @JsonPropertyWithDefault
    private ParticleData particle = null;
    @JsonPropertyWithDefault
    private ItemComparisonData teleportItem = new ItemComparisonData(Set.of(Material.AIR));

    public boolean isValidAction(Action action) {
        return actions.contains(action);
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

    public @Nullable ParticleData getParticle() {
        return particle;
    }

    public boolean isValidItem(ItemStack item) {
        return teleportItem.matches(item);
    }
}
