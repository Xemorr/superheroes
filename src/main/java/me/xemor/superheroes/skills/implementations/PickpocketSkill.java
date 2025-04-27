package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.PickpocketData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class PickpocketSkill extends SkillImplementation {
    public PickpocketSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Superhero superhero = heroHandler.getSuperhero(player);
        Collection<PickpocketData> pickpocketDatas = superhero.getSkillData(PickpocketData.class);
        for (PickpocketData pickpocketData : pickpocketDatas) {
            if (player.isSneaking() != pickpocketData.isSneaking()) {
                return;
            }
            if (e.getRightClicked() instanceof Player otherPlayer) {
                pickpocketData.ifConditionsTrue(() -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 0.5F);
                    otherPlayer.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 0.5F);
                    Inventory inventory = otherPlayer.getInventory();
                    InventoryView inventoryView = player.openInventory(inventory);
                    Superheroes.getScheduling().entitySpecificScheduler(player).runAtFixedRate((task) -> {
                        if (inventoryView == null) {
                            task.cancel();
                            return;
                        }
                        if (!superhero.equals(heroHandler.getSuperhero(player))) {
                            inventoryView.close();
                            task.cancel();
                            return;
                        }
                        if (!otherPlayer.getWorld().equals(player.getWorld())) {
                            inventoryView.close();
                            task.cancel();
                            return;
                        }
                        if (otherPlayer.getLocation().distanceSquared(player.getLocation()) > pickpocketData.getRangeSquared()) {
                            inventoryView.close();
                            task.cancel();
                        }
                    }, () -> {
                    }, 1L, 4L);
                }, player, otherPlayer);
            }
            else if (e.getRightClicked() instanceof Villager villager) {
                pickpocketData.ifConditionsTrue(() -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 0.5F);
                    ItemStack[] contents = villager.getInventory().getContents();
                    villager.getInventory().clear();
                    for (ItemStack item : contents) {
                        if (item != null) villager.getWorld().dropItemNaturally(player.getLocation(), item);
                    }
                }, player, villager);
            }
        }
    }
}
