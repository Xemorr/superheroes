package me.xemor.superheroes.skills.skilldata;

import me.xemor.configurationdata.ItemStackData;
import me.xemor.superheroes.Superheroes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;
import java.util.Map;

public class CraftingData extends SkillData {

    private final Recipe recipe;

    public CraftingData(int skill, ConfigurationSection configurationSection) {
        super(skill, configurationSection);
        boolean isShaped = configurationSection.getBoolean("isShaped", true);
        NamespacedKey namespacedKey = new NamespacedKey(Superheroes.getInstance(), configurationSection.getCurrentPath());
        ConfigurationSection resultSection = configurationSection.getConfigurationSection("result");
        ItemStack result;
        if (resultSection != null) {
            result = new ItemStackData(resultSection).getItem();
        }
        else {
            result = new ItemStack(Material.STONE, 1);
        }
        if (isShaped) {
            ConfigurationSection recipeKeys = configurationSection.getConfigurationSection("recipeKeys");
            ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, result);
            List<String> recipeShape = configurationSection.getStringList("recipe");
            shapedRecipe.shape(recipeShape.toArray(new String[0]));
            for (Map.Entry<String, Object> entry : recipeKeys.getValues(false).entrySet()) {
                Material material = Material.valueOf(entry.getKey().toUpperCase());
                String symbol = (String) entry.getValue();
                shapedRecipe.setIngredient(symbol.charAt(0), material);
            }
            recipe = shapedRecipe;
        }
        else {
            ShapelessRecipe shapelessRecipe = new ShapelessRecipe(namespacedKey, result);
            ConfigurationSection ingredients = configurationSection.getConfigurationSection("ingredients");
            for (Map.Entry<String, Object> entry : ingredients.getValues(false).entrySet()) {
                Integer x = (Integer) entry.getValue();
                shapelessRecipe.addIngredient(x, Material.valueOf(entry.getKey()));
            }
            recipe = shapelessRecipe;
        }
        Bukkit.addRecipe(recipe);
    }

    public Recipe getRecipe() {
        return recipe;
    }

}
