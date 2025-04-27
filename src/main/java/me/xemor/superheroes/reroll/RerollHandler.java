package me.xemor.superheroes.reroll;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.xemor.configurationdata.ConfigurationData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.superheroes.CooldownHandler;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.events.SuperheroesReloadEvent;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RerollHandler implements Listener {
    @JsonIgnore
    private final CooldownHandler cooldownHandler = new CooldownHandler("", ChatMessageType.ACTION_BAR);

    private RerollConfig rerollConfig;

    public RerollHandler() {
        this.loadData();
    }

    public void loadData() {
        File rerollConfig = new File(Superheroes.getInstance().getDataFolder(), "reroll.yml");

        if (!rerollConfig.exists()) {
            Superheroes.getInstance().saveResource("reroll.yml", false);
        }

        try {
            this.rerollConfig = ConfigurationData.setupObjectMapperForConfigurationData(new ObjectMapper(new YAMLFactory()))
                    .readValue(rerollConfig, RerollConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && rerollConfig.getGlobalRerollSettings().isItemEnabled()) {
            for (RerollGroup group : rerollConfig.rerollGroups().values()) {
                if (!group.matchesItem(item) || !this.cooldownHandler.isCooldownOver(e.getPlayer().getUniqueId()))
                    continue;
                item.setAmount(item.getAmount() - 1);
                Superhero newHero = group.chooseHero(player);
                Superheroes.getInstance().getHeroHandler().setHero(player, newHero);
                cooldownHandler.startCooldown(rerollConfig.getGlobalRerollSettings().getItemCooldown(), player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onReload(SuperheroesReloadEvent e) {
        this.loadData();
    }

    @Nullable
    public RerollGroup getWeightedHeroes(@NotNull String groupName) {
        return rerollConfig.rerollGroups().get(groupName.toLowerCase());
    }

    public RerollConfig getRerollConfig() {
        return rerollConfig;
    }

    public Collection<Map.Entry<String, RerollGroup>> getIterator() {
        return rerollConfig.rerollGroups().entrySet();
    }
}

