package me.xemor.superheroes2;

import me.xemor.superheroes2.skills.skilldata.SkillData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

public class CooldownHandler {

    private HashMap<SkillData, HashMap<UUID, Long>> cooldownMap = new HashMap<>();
    private String cooldownMessage;

    public CooldownHandler(String cooldownMsg) {
        cooldownMessage = ChatColor.translateAlternateColorCodes('&', cooldownMsg);
    }

    public void startCooldown(SkillData skillData, double cooldown, UUID uuid) {
        HashMap<UUID, Long> hashMap = cooldownMap.getOrDefault(skillData, new HashMap<>());
        hashMap.put(uuid, (long) (cooldown * 1000) + System.currentTimeMillis());
        cooldownMap.put(skillData, hashMap);
    }

    public boolean isCooldownOver(SkillData skillData, UUID uuid) {
        return isCooldownOver(skillData, uuid, cooldownMessage);
    }

    public boolean isCooldownOver(SkillData skillData, UUID uuid, String cooldownMessage) {
        HashMap<UUID, Long> hashMap = cooldownMap.getOrDefault(skillData, new HashMap<>());
        if (hashMap.containsKey(uuid)) {
            if (hashMap.get(uuid) <= System.currentTimeMillis()) {
                return true;
            }
            long seconds = ((hashMap.get(uuid) - System.currentTimeMillis()) / 1000);
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
