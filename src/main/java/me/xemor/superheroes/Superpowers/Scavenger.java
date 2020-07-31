package me.xemor.superheroes.Superpowers;

import me.xemor.superheroes.Events.PlayerGainedPowerEvent;
import me.xemor.superheroes.Events.PlayerLostPowerEvent;
import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.RecipeHandler;
import me.xemor.superheroes.Superheroes;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Scavenger extends Superpower {
    Superheroes superheroes;

    List<NamespacedKey> recipeKeys = new ArrayList<>();
    private final static String swordDisplayName = ChatColor.translateAlternateColorCodes('&', "&6Scavenger's Sword");
    private final static String helmetDisplayName = ChatColor.translateAlternateColorCodes('&', "&6Scavenger's Helmet");
    private final static String chestplateDisplayName = ChatColor.translateAlternateColorCodes('&', "&6Scavenger's Chestplate");
    private final static String leggingsDisplayName = ChatColor.translateAlternateColorCodes('&', "&6Scavenger's Leggings");
    private final static String bootsDisplayName = ChatColor.translateAlternateColorCodes('&', "&6Scavenger's Boots");
    private final static String pickaxeDisplayName = ChatColor.translateAlternateColorCodes('&', "&6Scavenger's Pickaxe");
    private final static String axeDisplayName = ChatColor.translateAlternateColorCodes('&', "&6Scavenger's Axe");
    private final static String shovelDisplayName = ChatColor.translateAlternateColorCodes('&', "&6\"Diamond\" Shovel");
    private final static String bowDisplayName = ChatColor.translateAlternateColorCodes('&', "&6Scavenger's Bow");
    private final static String berrySoupDisplayName = ChatColor.translateAlternateColorCodes('&', "&cBerry &fSoup");

    public Scavenger(PowersHandler powersHandler, RecipeHandler recipeHandler, Superheroes superheroes) {
        super(powersHandler);
        this.superheroes = superheroes;
        setupRecipes(recipeHandler);
    }

    public void setupRecipes(RecipeHandler recipeHandler) {
        Recipe[] recipes = new Recipe[]{makeLeatherRecipe(), makeDiamondSwordRecipe(), makeDiamondHelmetRecipe(),
                makeDiamondChestplateRecipe(), makeDiamondLeggingsRecipe(), makeDiamondBootsRecipe(), makeBowRecipe(),
        makeDiamondAxeRecipe(), makeDiamondPickaxeRecipe(), makeDiamondShovelRecipe(), makeString(), makeBerrySoup()};
        for (Recipe recipe : recipes) {
            recipeHandler.registerRecipe(recipe, Power.Scavenger);
            recipeKeys.add(((Keyed) recipe).getKey());
        }
    }

    @EventHandler
    public void gainPower(PlayerGainedPowerEvent e) {
        if (e.getPower() == Power.Scavenger) {
            e.getPlayer().discoverRecipes(recipeKeys);
        }
    }

    @EventHandler
    public void lostPower(PlayerLostPowerEvent e) {
        if (e.getPower() == Power.Scavenger) {
            e.getPlayer().undiscoverRecipes(recipeKeys);
        }
    }

    public Recipe makeLeatherRecipe() {
        ItemStack leather = new ItemStack(Material.LEATHER);
        ShapelessRecipe leatherRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "leatherRecipe"), leather);
        leatherRecipe.addIngredient(Material.ROTTEN_FLESH);
        return leatherRecipe;
    }

    public Recipe makeDiamondSwordRecipe() {
        ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = diamondSword.getItemMeta();
        itemMeta.setDisplayName(swordDisplayName);
        diamondSword.setItemMeta(itemMeta);
        ShapelessRecipe swordRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "swordRecipe"), diamondSword);
        swordRecipe.addIngredient(Material.IRON_SWORD);
        swordRecipe.addIngredient(Material.DIAMOND);
        return swordRecipe;
    }

    public Recipe makeDiamondHelmetRecipe() {
        ItemStack diamondHelmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta itemMeta = diamondHelmet.getItemMeta();
        itemMeta.setDisplayName(helmetDisplayName);
        diamondHelmet.setItemMeta(itemMeta);
        ShapelessRecipe helmetRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "helmetRecipe"), diamondHelmet);
        helmetRecipe.addIngredient(Material.IRON_HELMET);
        helmetRecipe.addIngredient(2, Material.DIAMOND);
        return helmetRecipe;
    }

    public Recipe makeDiamondChestplateRecipe() {
        ItemStack diamondChestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta itemMeta = diamondChestplate.getItemMeta();
        itemMeta.setDisplayName(chestplateDisplayName);
        diamondChestplate.setItemMeta(itemMeta);
        ShapelessRecipe chestplateRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "chestplateRecipe"), diamondChestplate);
        chestplateRecipe.addIngredient(Material.IRON_CHESTPLATE);
        chestplateRecipe.addIngredient(5, Material.DIAMOND);
        return chestplateRecipe;
    }

    public Recipe makeDiamondLeggingsRecipe() {
        ItemStack diamondLeggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemMeta itemMeta = diamondLeggings.getItemMeta();
        itemMeta.setDisplayName(leggingsDisplayName);
        diamondLeggings.setItemMeta(itemMeta);
        ShapelessRecipe leggingsRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "leggingsRecipe"), diamondLeggings);
        leggingsRecipe.addIngredient(Material.IRON_LEGGINGS);
        leggingsRecipe.addIngredient(3, Material.DIAMOND);
        return leggingsRecipe;
    }

    public Recipe makeDiamondBootsRecipe() {
        ItemStack diamondBoots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta itemMeta = diamondBoots.getItemMeta();
        itemMeta.setDisplayName(bootsDisplayName);
        diamondBoots.setItemMeta(itemMeta);
        ShapelessRecipe bootsRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "bootsRecipe"), diamondBoots);
        bootsRecipe.addIngredient(Material.IRON_BOOTS);
        bootsRecipe.addIngredient(2, Material.DIAMOND);
        return bootsRecipe;
    }

    public Recipe makeDiamondPickaxeRecipe() {
        ItemStack diamondPickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta itemMeta = diamondPickaxe.getItemMeta();
        itemMeta.setDisplayName(pickaxeDisplayName);
        diamondPickaxe.setItemMeta(itemMeta);
        ShapelessRecipe diamondPickaxeRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "diamondPickaxeRecipe"), diamondPickaxe);
        diamondPickaxeRecipe.addIngredient(Material.IRON_PICKAXE);
        diamondPickaxeRecipe.addIngredient(2, Material.DIAMOND);
        return diamondPickaxeRecipe;
    }

    public Recipe makeDiamondAxeRecipe() {
        ItemStack diamondAxe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta itemMeta = diamondAxe.getItemMeta();
        itemMeta.setDisplayName(axeDisplayName);
        diamondAxe.setItemMeta(itemMeta);
        ShapelessRecipe diamondAxeRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "diamondAxeRecipe"), diamondAxe);
        diamondAxeRecipe.addIngredient(Material.IRON_AXE);
        diamondAxeRecipe.addIngredient(2, Material.DIAMOND);
        return diamondAxeRecipe;
    }

    public Recipe makeDiamondShovelRecipe() {
        ItemStack diamondShovel = new ItemStack(Material.DIAMOND_SHOVEL);
        ItemMeta itemMeta = diamondShovel.getItemMeta();
        itemMeta.setDisplayName(shovelDisplayName);
        diamondShovel.setItemMeta(itemMeta);
        ShapelessRecipe diamondShovelRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "diamondShovelRecipe"), diamondShovel);
        diamondShovelRecipe.addIngredient(Material.IRON_SHOVEL);
        diamondShovelRecipe.addIngredient(Material.LAPIS_LAZULI);
        return diamondShovelRecipe;
    }

    public Recipe makeBowRecipe() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta itemMeta = bow.getItemMeta();
        itemMeta.setDisplayName(bowDisplayName);
        bow.setItemMeta(itemMeta);
        ShapedRecipe bowRecipe = new ShapedRecipe(new NamespacedKey(superheroes, "bowRecipe"), bow);
        bowRecipe.shape(" ||", "| S", " ||");
        bowRecipe.setIngredient('|', Material.STICK);
        bowRecipe.setIngredient('S', Material.STRING);
        return bowRecipe;
    }

    public Recipe makeString() {
        ItemStack string = new ItemStack(Material.STRING, 2);
        ShapelessRecipe stringRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "stringRecipe"), string);
        stringRecipe.addIngredient(Material.WHITE_WOOL);
        return stringRecipe;
    }

    public Recipe makeBerrySoup() {
        ItemStack berrySoup = new ItemStack(Material.BEETROOT_SOUP);
        ItemMeta itemMeta = berrySoup.getItemMeta();
        itemMeta.setDisplayName(berrySoupDisplayName);
        berrySoup.setItemMeta(itemMeta);
        ShapelessRecipe berrySoupRecipe = new ShapelessRecipe(new NamespacedKey(superheroes, "berrySoupRecipe"), berrySoup);
        berrySoupRecipe.addIngredient(Material.BOWL);
        berrySoupRecipe.addIngredient(2, Material.SWEET_BERRIES);
        return berrySoupRecipe;
    }
}
