package me.xemor.superheroes.reroll;

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
import java.util.*;

public class RerollHandler implements Listener {
    private final CooldownHandler cooldownHandler = new CooldownHandler("", ChatMessageType.ACTION_BAR);
    private boolean itemEnabled;
    private boolean eachHeroRequiresPermission;
    private double itemCooldown;
    private final Map<String, RerollGroup> groupToWeightedHeroes = new HashMap<>();
    private final List<RerollGroup> rerollGroups = new ArrayList<>();

    public RerollHandler() {
        this.loadData();
    }

    public void loadData() {
        File rerollConfig = new File(Superheroes.getInstance().getDataFolder(), "reroll.yml");
        if (!rerollConfig.exists()) {
            Superheroes.getInstance().saveResource("reroll.yml", false);
        }
        YamlConfiguration rerollYAML = YamlConfiguration.loadConfiguration(rerollConfig);
        ConfigurationSection rerollGroupsYAML = rerollYAML.getConfigurationSection("reroll_groups");
        Map<String, Object> groups = rerollGroupsYAML.getValues(false);
        for (Map.Entry<String, Object> group : groups.entrySet()) {
            Object v = group.getValue();
            if (!(v instanceof ConfigurationSection section)) continue;
            RerollGroup rerollGroup = new RerollGroup(section);
            this.groupToWeightedHeroes.put(group.getKey(), rerollGroup);
            this.rerollGroups.add(rerollGroup);
        }
        if (!this.groupToWeightedHeroes.containsKey("default")) {
            RerollGroup defaultGroup = new RerollGroup();
            this.groupToWeightedHeroes.put("default", defaultGroup);
            this.rerollGroups.add(defaultGroup);
        }
        this.itemEnabled = rerollYAML.getBoolean("global_reroll_settings.itemEnabled", true);
        this.eachHeroRequiresPermission = rerollYAML.getBoolean("global_reroll_settings.eachHeroRequiresPermission", false);
        this.itemCooldown = rerollYAML.getDouble("global_reroll_settings.itemCooldown", 1.0);
    }

    @EventHandler
    public void onReload(SuperheroesReloadEvent e) {
        groupToWeightedHeroes.clear();
        rerollGroups.clear();
        this.loadData();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && this.itemEnabled) {
            for (RerollGroup group : this.rerollGroups) {
                if (!group.matchesItem(item) || !this.cooldownHandler.isCooldownOver(e.getPlayer().getUniqueId()))
                    continue;
                item.setAmount(item.getAmount() - 1);
                Superhero newHero = group.chooseHero(player);
                Superheroes.getInstance().getHeroHandler().setHero(player, newHero);
                this.cooldownHandler.startCooldown(this.itemCooldown, player.getUniqueId());
            }
        }
    }

    public boolean isItemEnabled() {
        return itemEnabled;
    }

    public boolean doesHeroRequirePermissions() {
        return eachHeroRequiresPermission;
    }

    @Nullable
    public RerollGroup getWeightedHeroes(@NotNull String groupName) {
        return this.groupToWeightedHeroes.get(groupName.toLowerCase());
    }

    public Collection<Map.Entry<String, RerollGroup>> getIterator() {
        return this.groupToWeightedHeroes.entrySet();
    }
}

