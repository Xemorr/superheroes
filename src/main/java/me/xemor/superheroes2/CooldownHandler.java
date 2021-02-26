package me.xemor.superheroes2;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

public class CooldownHandler {

    private HashMap<UUID, Long> cooldownMap = new HashMap<>();
    private String cooldownMessage;

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
        if (cooldownMap.containsKey(uuid)) {
            if (cooldownMap.get(uuid) <= System.currentTimeMillis()) {
                return true;
            }
            long seconds = ((cooldownMap.get(uuid) - System.currentTimeMillis()) / 1000);
            if (!cooldownMessage.equals("")) {
                Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.format(cooldownMessage, seconds)));
            }
            return false;
        }
        else {
            return true;
        }
    }

}
