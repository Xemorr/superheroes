package me.xemor.superheroes2;

import de.themoep.minedown.MineDown;
import me.xemor.superheroes2.skills.skilldata.configdata.Cooldown;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class SkillCooldownHandler {

    private final HashMap<Cooldown, HashMap<UUID, Long>> cooldownMap = new HashMap<>();

    public void startCooldown(Cooldown skillData, double cooldown, UUID uuid) {
        HashMap<UUID, Long> hashMap = cooldownMap.getOrDefault(skillData, new HashMap<>());
        hashMap.put(uuid, (long) (cooldown * 1000) + System.currentTimeMillis());
        cooldownMap.put(skillData, hashMap);
    }

    public void startCooldown(Cooldown skillData, UUID uuid) {
        startCooldown(skillData, skillData.getCooldown(), uuid);
    }

    public boolean isCooldownOver(Cooldown skillData, UUID uuid) {
        HashMap<UUID, Long> hashMap = cooldownMap.getOrDefault(skillData, new HashMap<>());
        if (hashMap.containsKey(uuid)) {
            if (hashMap.get(uuid) <= System.currentTimeMillis()) {
                return true;
            }
            long seconds = ((hashMap.get(uuid) - System.currentTimeMillis()) / 1000);
            MineDown cooldownMessage = new MineDown(skillData.getCooldownMessage());
            cooldownMessage = cooldownMessage.replace("currentcooldown", String.valueOf(seconds));
            Bukkit.getPlayer(uuid).spigot().sendMessage(ChatMessageType.ACTION_BAR, cooldownMessage.toComponent());
            return false;
        }
        else {
            return true;
        }
    }
}
