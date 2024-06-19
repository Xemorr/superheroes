package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.PickpocketData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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
        Collection<SkillData> skillDatas = superhero.getSkillData(Skill.getSkill("PICKPOCKET"));
        if (e.getRightClicked() instanceof Player) {
            for (SkillData skillData : skillDatas) {
                PickpocketData pickpocketData = (PickpocketData) skillData;
                if (player.isSneaking() != pickpocketData.isSneaking()) {
                    return;
                }
                Player otherPlayer = (Player) e.getRightClicked();
                if (skillData.areConditionsTrue(player, otherPlayer)) {
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 0.5F);
                    otherPlayer.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1F, 0.5F);
                    Inventory inventory =  otherPlayer.getInventory();
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
                    }, () -> {}, 1L, 4L);
                }
            }
        }
    }
}
