package me.xemor.superheroes.Superpowers.Sorcerer;

import me.xemor.superheroes.CooldownHandler;
import me.xemor.superheroes.PowersHandler;
import me.xemor.superheroes.RecipeHandler;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.Superpowers.Power;
import me.xemor.superheroes.Superpowers.Superpower;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Sorcerer extends Superpower {

    private static final HashSet<Spell> spells = new HashSet<>();
    private static final HashMap<Spell, Integer> spellToCost = new HashMap<>();
    private static final HashMap<Spell, Integer> spellToCooldown = new HashMap<>();
    private CooldownHandler cooldownHandler = new CooldownHandler("&5Spell &7Cooldown: %s seconds");
    private String moreRedstone = ChatColor.translateAlternateColorCodes('&', "&cRedstone &7Required: ");

    static {
        spells.addAll(Arrays.asList(new Spell[]{Spell.FIREBALL, Spell.LIGHTNING, Spell.EGG, Spell.SNOWBALL, Spell.ARROW, Spell.WATER, Spell.FIRE, Spell.LAVA}));
    }

    public Sorcerer(PowersHandler powersHandler, RecipeHandler recipeHandler, Superheroes superheroes) {
        super(powersHandler);
        handleRecipes(recipeHandler, superheroes);
        //FIREBALL
        spellToCost.put(Spell.FIREBALL, 4);
        spellToCooldown.put(Spell.FIREBALL, 6);
        //SNOWBALL
        spellToCost.put(Spell.SNOWBALL, 0);
        spellToCooldown.put(Spell.SNOWBALL, 0);
        //ARROW
        spellToCost.put(Spell.ARROW, 3);
        spellToCooldown.put(Spell.ARROW, 6);
        //LIGHTNING
        spellToCost.put(Spell.LIGHTNING, 6);
        spellToCooldown.put(Spell.LIGHTNING, 15);
        //EGG
        spellToCost.put(Spell.EGG, 1);
        spellToCooldown.put(Spell.EGG, 1);
        //WATER
        spellToCost.put(Spell.WATER, 0);
        spellToCooldown.put(Spell.WATER, 3);
        //LAVA
        spellToCost.put(Spell.LAVA, 4);
        spellToCooldown.put(Spell.LAVA, 5);
        //FIRE
        spellToCost.put(Spell.FIRE, 1);
        spellToCooldown.put(Spell.FIRE, 1);
    }

    public void handleRecipes(RecipeHandler recipeHandler, Superheroes superheroes) {
        ShapelessRecipe spellRecycle = new ShapelessRecipe(new NamespacedKey(superheroes, "spellRecycle"), new ItemStack(Material.WRITABLE_BOOK));
        spellRecycle.addIngredient(Material.WRITTEN_BOOK);
        recipeHandler.registerRecipe(spellRecycle, Power.Sorcerer);
        ShapedRecipe cheapWritableBook = new ShapedRecipe(new NamespacedKey(superheroes, "cheapWritableBook"), new ItemStack(Material.WRITABLE_BOOK));
        cheapWritableBook.shape("   ", "   ", "PPP");
        cheapWritableBook.setIngredient('P', Material.PAPER);
        recipeHandler.registerRecipe(cheapWritableBook, Power.Sorcerer);
    }

    @EventHandler
    public void bookWrite(PlayerEditBookEvent e) {
        if (e.isSigning()) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) == Power.Sorcerer) {
                BookMeta bookMeta = e.getNewBookMeta();
                if (spells.contains(Spell.valueOf(bookMeta.getTitle().toUpperCase()))) {
                    String newName = ChatColor.DARK_PURPLE + e.getNewBookMeta().getTitle().toUpperCase();
                    bookMeta.setTitle(newName);
                    bookMeta.setDisplayName(newName);
                    e.setNewBookMeta(bookMeta);
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            if (powersHandler.getPower(player) == Power.Sorcerer) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.hasItemMeta()) {
                    ItemMeta itemMeta = item.getItemMeta();
                    if (itemMeta.hasDisplayName()) {
                        if (item.getType() == Material.WRITTEN_BOOK) {
                            e.setUseItemInHand(Event.Result.DENY);
                            if (cooldownHandler.isCooldownOver(player.getUniqueId())) {
                                handleSpells(player, itemMeta, e);
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleSpells(Player player, ItemMeta itemMeta, PlayerInteractEvent e) {
        Spell spell = Spell.valueOf(ChatColor.stripColor(itemMeta.getDisplayName().toUpperCase()));
        int cost = spellToCost.get(spell);
        int cooldown = spellToCooldown.get(spell);
        if (player.getInventory().containsAtLeast(new ItemStack(Material.REDSTONE), cost)) {
            if (spell == Spell.FIREBALL) {
                player.launchProjectile(Fireball.class);
            }
            else if (spell == Spell.SNOWBALL) {
                player.launchProjectile(Snowball.class);
            }
            else if (spell == Spell.ARROW) {
                SpectralArrow arrow = player.launchProjectile(SpectralArrow.class);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            }
            else if (spell == Spell.LIGHTNING) {
                strikeLightning(player, 30);
            }
            else if (spell == Spell.EGG) {
                player.launchProjectile(Egg.class);
            }
            else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Block clickedBlock = e.getClickedBlock();
                    Block placeHere = clickedBlock.getRelative(e.getBlockFace());
                    if (spell == Spell.WATER) {
                        placeHere.setType(Material.WATER);
                    }
                    else if (spell == Spell.LAVA) {
                        placeHere.setType(Material.LAVA);
                    }
                    else if (spell == Spell.FIRE) {
                        placeHere.setType(Material.FIRE);
                    }
            }
            else {
                cost = 0;
                cooldown = 0;
            }
            payUpThen(player, cost);
            cooldownHandler.startCooldown(cooldown, player.getUniqueId());
        }
        else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(moreRedstone + cost));
        }
    }

    public void payUpThen(Player player, int cost) {
        int paidFor = 0;
        for (ItemStack item : player.getInventory()) {
            if (item == null) {
                continue;
            }
            if (item.getType() == Material.REDSTONE) {
                if (item.getAmount() >= (cost - paidFor)) {
                    item.setAmount(item.getAmount() - (cost - paidFor));
                    break;
                }
                else {
                    paidFor += item.getAmount();
                    item.setType(Material.AIR);
                }
            }
        }
    }

    private void strikeLightning(Player player, int blocksToTravel) {
        World world = player.getWorld();
        Location eyeLoc = player.getEyeLocation().clone();
        Vector travelVector = eyeLoc.getDirection();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(eyeLoc, travelVector, blocksToTravel);
        Vector hitPosition;
        if (rayTraceResult == null || rayTraceResult.getHitPosition() == null) {
            hitPosition = eyeLoc.toVector().add(travelVector.multiply(blocksToTravel));
        } else {
            hitPosition = rayTraceResult.getHitPosition();
        }
        Location location = new Location(world, hitPosition.getX(), hitPosition.getY(), hitPosition.getZ());
        world.strikeLightning(location);
    }


}
