package me.xemor.superheroes;

import me.xemor.superheroes.skills.skilldata.configdata.Cooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillCooldownHandler {

    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build();
    private final Map<Cooldown, HashMap<UUID, Long>> cooldownMap = new HashMap<>();

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
            Component cooldownMessage = MiniMessage.miniMessage().deserialize(skillData.getCooldownMessage(), Placeholder.unparsed("currentcooldown", String.valueOf(seconds)));
            String serializedMessage = legacySerializer.serialize(cooldownMessage);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(serializedMessage));
            return false;
        }
        else {
            return true;
        }
    }
}
