package me.xemor.superheroes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.xemor.superheroes.Events.PlayerGainedPowerEvent;
import me.xemor.superheroes.Events.PlayerLostPowerEvent;
import me.xemor.superheroes.Superpowers.Power;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RecipeHandler implements Listener {

    private HashMap<NamespacedKey, Power> recipeToPower = new HashMap<>();
    private Multimap<Power, NamespacedKey> powerToRecipe = HashMultimap.create();
    private PowersHandler powersHandler;


    public RecipeHandler(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    @EventHandler
    public void prepareCrafting(PrepareItemCraftEvent e) {
        Power power = null;
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
                if (powersHandler.getPower(player) == power) {
                    e.getInventory().setResult(eventRecipe.getResult());
                }
            }
        }
    }

    @EventHandler
    public void onPowerGain(PlayerGainedPowerEvent e) {
        Collection<NamespacedKey> recipeKeys = powerToRecipe.get(e.getPower());
        e.getPlayer().discoverRecipes(recipeKeys);
    }

    @EventHandler
    public void onPowerLost(PlayerLostPowerEvent e) {
        Collection<NamespacedKey> recipeKeys = powerToRecipe.get(e.getPower());
        e.getPlayer().undiscoverRecipes(recipeKeys);
    }

    public void registerRecipe(Recipe recipe, Power power) {
        if (recipe instanceof Keyed) {
            NamespacedKey key = ((Keyed)recipe).getKey();
            recipeToPower.put(key, power);
            powerToRecipe.put(power, key);
        }
        Bukkit.addRecipe(recipe);
    }
}
