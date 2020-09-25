package me.xemor.superheroes2;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.xemor.superheroes2.events.PlayerGainedSuperheroEvent;
import me.xemor.superheroes2.events.PlayerLostSuperheroEvent;
import me.xemor.superheroes2.skills.Skill;
import me.xemor.superheroes2.skills.skilldata.CraftingData;
import me.xemor.superheroes2.skills.skilldata.SkillData;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RecipeHandler implements Listener {

    private HashMap<NamespacedKey, Superhero> recipeToPower = new HashMap<>();
    private PowersHandler powersHandler;


    public RecipeHandler(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    @EventHandler
    public void prepareCrafting(PrepareItemCraftEvent e) {
        Superhero power = null;
        Recipe eventRecipe = e.getRecipe();
        if (eventRecipe instanceof Keyed) {
            power = recipeToPower.get(((Keyed)eventRecipe).getKey());
        }
        if (power == null) {
            return;
        }
        e.getInventory().setResult(new ItemStack(Material.AIR));
        List<HumanEntity> viewers = e.getViewers();
        for (HumanEntity humanEntity : viewers) {
            if (humanEntity instanceof Player) {
                Player player = (Player) humanEntity;
                if (power.equals(powersHandler.getSuperhero(player))) {
                    e.getInventory().setResult(eventRecipe.getResult());
                }
            }
        }
    }

    @EventHandler
    public void onPowerGain(PlayerGainedSuperheroEvent e) {
        Collection<NamespacedKey> recipeKeys = getRecipesFromSuperhero(e.getHero());
        e.getPlayer().discoverRecipes(recipeKeys);
    }

    public Collection<NamespacedKey> getRecipesFromSuperhero(Superhero superhero) {
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.CRAFTING);
        List<NamespacedKey> recipes = new ArrayList<>();
        for (SkillData skillData : skillDatas) {
            CraftingData craftingData = (CraftingData) skillData;
            recipes.add(((Keyed) craftingData.getRecipe()).getKey());
        }
        return recipes;
    }

    @EventHandler
    public void onPowerLost(PlayerLostSuperheroEvent e) {
        Collection<NamespacedKey> recipeKeys = getRecipesFromSuperhero(e.getHero());
        e.getPlayer().undiscoverRecipes(recipeKeys);
    }

    public void registerRecipe(Recipe recipe, Superhero superhero) {
        if (recipe instanceof Keyed) {
            NamespacedKey key = ((Keyed)recipe).getKey();
            recipeToPower.put(key, superhero);
        }
        Bukkit.addRecipe(recipe);
    }
}
