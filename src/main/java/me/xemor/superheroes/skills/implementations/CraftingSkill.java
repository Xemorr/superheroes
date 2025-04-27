package me.xemor.superheroes.skills.implementations;

import me.xemor.foliahacks.PlayerPostRespawnFoliaEvent;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.events.SuperheroesReloadEvent;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.CraftingData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Collection;
import java.util.List;

public class CraftingSkill extends SkillImplementation {

    public CraftingSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onPowerGain(PlayerChangedSuperheroEvent e) {
        discoverRecipes(e.getPlayer(), e.getNewHero());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Superheroes.getFoliaHacks().getScheduling().entitySpecificScheduler(e.getPlayer()).runDelayed(
                () -> {
                    Superhero superhero = heroHandler.getSuperhero(e.getPlayer());
                    discoverRecipes(e.getPlayer(), superhero);
                },
                () -> {},
                150L
        );
    }

    @EventHandler
    public void onPostRespawn(PlayerPostRespawnFoliaEvent e) {
        // just in case
        discoverRecipes(e.getPlayer(), heroHandler.getSuperhero(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerLoad(ServerLoadEvent e) {
        discoverForAllLate();
    }

    public void discoverRecipes(Player player, Superhero hero) {
        Collection<SkillData> skills = hero.getSkillData("CRAFTING");
        for (SkillData skill : skills) {
            CraftingData craftingData = (CraftingData) skill;
            NamespacedKey namespacedKey = ((Keyed) craftingData.getRecipe()).getKey();
            player.discoverRecipe(namespacedKey);
        }
    }

    @EventHandler
    public void onPowerLost(PlayerChangedSuperheroEvent e) {
        Superhero superhero = e.getOldHero();
        Collection<SkillData> skills = superhero.getSkillData("CRAFTING");
        for (SkillData skill : skills) {
            CraftingData craftingData = (CraftingData) skill;
            NamespacedKey namespacedKey = ((Keyed) craftingData.getRecipe()).getKey();
            e.getPlayer().undiscoverRecipe(namespacedKey);
        }
    }

    @EventHandler
    public void onReload(SuperheroesReloadEvent e) {
        Collection<Superhero> values = Superheroes.getInstance().getHeroHandler().getNameToSuperhero().values();
        for (Superhero hero : values) {
            Collection<SkillData> skills = hero.getSkillData("CRAFTING");
            for (SkillData skill : skills) {
                CraftingData craftingData = (CraftingData) skill;
                NamespacedKey namespacedKey = ((Keyed) craftingData.getRecipe()).getKey();
                Bukkit.removeRecipe(namespacedKey);
            }
        }
        discoverForAllLate();
    }

    public void discoverForAllLate() {
        Bukkit.getOnlinePlayers().forEach((player) -> Superheroes.getFoliaHacks().getScheduling().entitySpecificScheduler(player).runDelayed(
                () -> {
                    Superhero superhero = heroHandler.getSuperhero(player);
                    discoverRecipes(player, superhero);
                },
                () -> {},
                5L
        ));
    }

    @EventHandler
    public void prepareCrafting(PrepareItemCraftEvent e) {
        Recipe eventRecipe = e.getRecipe();
        NamespacedKey eventKey = null;
        if (eventRecipe instanceof Keyed) {
            eventKey = ((Keyed) eventRecipe).getKey();
            if (!eventKey.getNamespace().equals("superheroes")) {
                return;
            }
        }
        if (eventKey == null) {
            return;
        }
        e.getInventory().setResult(new ItemStack(Material.AIR));
        List<HumanEntity> viewers = e.getViewers();
        for (HumanEntity humanEntity : viewers) {
            if (humanEntity instanceof Player player) {
                Superhero superhero = heroHandler.getSuperhero(player);
                Collection<SkillData> skills = superhero.getSkillData("CRAFTING");
                for (SkillData skill : skills) {
                    CraftingData craftingData = (CraftingData) skill;
                    NamespacedKey namespacedKey = ((Keyed)craftingData.getRecipe()).getKey();
                    if (namespacedKey.equals(eventKey)) {
                        craftingData.ifConditionsTrue(() -> e.getInventory().setResult(new ItemStack(eventRecipe.getResult())), player);
                    }
                }
            }
        }
    }


}
