package me.xemor.superheroes;


import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class CooldownHandler {

    private final HashMap<UUID, Long> cooldownMap = new HashMap<>();
    private final String cooldownMessage;
    private final ChatMessageType chatMessageType;

    public CooldownHandler(String cooldownMsg, ChatMessageType chatMessageType) {
        cooldownMessage = cooldownMsg;
        this.chatMessageType = chatMessageType;
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
        if (!cooldownMessage.equals("") && seconds > 0) {
            Component component = MiniMessage.miniMessage().deserialize(cooldownMessage, Placeholder.unparsed("currentcooldown",String.valueOf(seconds)));
            Audience player = Superheroes.getBukkitAudiences().player(Bukkit.getPlayer(uuid));
            if (chatMessageType == ChatMessageType.ACTION_BAR) {
                player.sendActionBar(component);
            }
            else player.sendMessage(component);
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
