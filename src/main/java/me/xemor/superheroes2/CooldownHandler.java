package me.xemor.superheroes2;

import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

public class CooldownHandler {

    private final HashMap<UUID, Long> cooldownMap = new HashMap<>();
    private final String cooldownMessage;

    public CooldownHandler(String cooldownMsg) {
        cooldownMessage = ChatColor.translateAlternateColorCodes('&', cooldownMsg);
    }

    public void startCooldown(double cooldown, UUID uuid) {
        cooldownMap.put(uuid, (long) (cooldown * 1000) + System.currentTimeMillis());
    }

    public boolean hasUsedItemBefore(UUID uuid) {
        return cooldownMap.containsKey(uuid);
    }

    public boolean isCooldownOver(UUID uuid) {
        return isCooldownOver(uuid, cooldownMessage);
    }

    public boolean isCooldownOver(UUID uuid, String cooldownMessage) {
        long seconds = getCurrentCooldown(uuid);
        if (!cooldownMessage.equals("")) {
            Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, MineDown.parse(cooldownMessage, "currentcooldown", String.valueOf(seconds)));
        }
        return seconds <= 0;
    }

    public long getCurrentCooldown(UUID uuid) {
        if (cooldownMap.containsKey(uuid)) {
            long timeAtCooldownStart = cooldownMap.get(uuid);
            if (timeAtCooldownStart <= System.currentTimeMillis()) {
                return 0;
            }
            return ((timeAtCooldownStart - System.currentTimeMillis()) / 1000);
        }
        else {
            return 0;
        }
    }

}
