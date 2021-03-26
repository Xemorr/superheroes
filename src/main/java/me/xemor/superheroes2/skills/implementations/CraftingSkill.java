package me.xemor.superheroes2.skills.implementations;

import me.xemor.superheroes2.HeroHandler;
import me.xemor.superheroes2.Superhero;
import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.CraftingData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Collection;
import java.util.List;

public class CraftingSkill extends SkillImplementation {
    public CraftingSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onPowerGain(PlayerGainedSuperheroEvent e) {
        Superhero superhero = heroHandler.getSuperhero(e.getPlayer());
        Collection<SkillData> skills = superhero.getSkillData(Skill.CRAFTING);
        for (SkillData skill : skills) {
            CraftingData craftingData = (CraftingData) skill;
            NamespacedKey namespacedKey = ((Keyed) craftingData.getRecipe()).getKey();
            e.getPlayer().discoverRecipe(namespacedKey);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Superhero superhero = heroHandler.getSuperhero(e.getPlayer());
        Collection<SkillData> skills = superhero.getSkillData(Skill.CRAFTING);
        for (SkillData skill : skills) {
            CraftingData craftingData = (CraftingData) skill;
            NamespacedKey namespacedKey = ((Keyed) craftingData.getRecipe()).getKey();
            e.getPlayer().discoverRecipe(namespacedKey);
        }
    }

    @EventHandler
    public void onPowerLost(PlayerLostSuperheroEvent e) {
        Superhero superhero = heroHandler.getSuperhero(e.getPlayer());
        Collection<SkillData> skills = superhero.getSkillData(Skill.CRAFTING);
        for (SkillData skill : skills) {
            CraftingData craftingData = (CraftingData) skill;
            NamespacedKey namespacedKey = ((Keyed) craftingData.getRecipe()).getKey();
            e.getPlayer().undiscoverRecipe(namespacedKey);
        }
    }

    @EventHandler
    public void prepareCrafting(PrepareItemCraftEvent e) {
        Recipe eventRecipe = e.getRecipe();
        NamespacedKey eventKey = null;
        if (eventRecipe instanceof Keyed) {
            eventKey = ((Keyed) eventRecipe).getKey();
            if (!eventKey.getNamespace().equals("superheroes2")) {
                return;
            }
        }
        if (eventKey == null) {
            return;
        }
        e.getInventory().setResult(new ItemStack(Material.AIR));
        List<HumanEntity> viewers = e.getViewers();
        for (HumanEntity humanEntity : viewers) {
            if (humanEntity instanceof Player) {
                Player player = (Player) humanEntity;
                Superhero superhero = heroHandler.getSuperhero(player);
                Collection<SkillData> skills = superhero.getSkillData(Skill.CRAFTING);
                if (eventRecipe == null) {
                    return;
                }
                for (SkillData skill : skills) {
                    CraftingData craftingData = (CraftingData) skill;
                    NamespacedKey namespacedKey = ((Keyed)craftingData.getRecipe()).getKey();
                    if (namespacedKey.equals(eventKey)) {
                        e.getInventory().setResult(new ItemStack(eventRecipe.getResult()));
                    }
                }
            }
        }
    }


}
