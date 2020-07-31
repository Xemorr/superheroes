package me.xemor.superheroes;

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

import java.util.HashMap;
import java.util.List;

public class RecipeHandler implements Listener {

    private HashMap<NamespacedKey, Power> hashMap = new HashMap<>();
    private PowersHandler powersHandler;


    public RecipeHandler(PowersHandler powersHandler) {
        this.powersHandler = powersHandler;
    }

    @EventHandler
    public void prepareCrafting(PrepareItemCraftEvent e) {
        Power power = null;
        Recipe eventRecipe = e.getRecipe();
        if (eventRecipe instanceof Keyed) {
            power = hashMap.get(((Keyed)eventRecipe).getKey());
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

    public void registerRecipe(Recipe recipe, Power power) {
        if (recipe instanceof Keyed) {
            hashMap.put(((Keyed)recipe).getKey(), power);
        }
        Bukkit.addRecipe(recipe);
    }
}
