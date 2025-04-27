package me.xemor.superheroes.skills.skilldata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import me.xemor.configurationdata.CompulsoryJsonProperty;
import me.xemor.configurationdata.ItemStackData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.recipes.ShapedRecipeData;
import me.xemor.configurationdata.recipes.ShapelessRecipeData;
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

@JsonIgnoreProperties(value = {"isShaped"})
public class CraftingData extends SkillData {

    @CompulsoryJsonProperty
    private ShapelessRecipeData shapeless;
    @CompulsoryJsonProperty
    private ShapedRecipeData shaped;

    public Recipe getRecipe() {
        if (shaped != null) return shaped.getRecipeAndLazyRegister();
        else if (shapeless != null) return shapeless.getRecipeAndLazyRegister();
        else { Superheroes.getInstance().getLogger().severe("Crafting Recipe without shapeless or shaped section!"); return null; }
    }
}
