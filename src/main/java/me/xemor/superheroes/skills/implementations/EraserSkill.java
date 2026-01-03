package me.xemor.superheroes.skills.implementations;

import me.xemor.superheroes.SkillCooldownHandler;
import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.skills.Skill;
import me.xemor.superheroes.skills.skilldata.EraserData;
import me.xemor.superheroes.skills.skilldata.SkillData;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class EraserSkill extends SkillImplementation {

    public EraserSkill(HeroHandler heroHandler) {
        super(heroHandler);
    }

    private final Superhero erased = new Superhero("ERASED", "<gray><b>ERASED", "Their power has been erased");
    private final SkillCooldownHandler skillCooldownHandler =new SkillCooldownHandler();

    @EventHandler
    public void onSight(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            Superhero superhero = heroHandler.getSuperhero(player);
            Collection<SkillData> skillDatas = superhero.getSkillData("ERASER");
            for (SkillData skillData : skillDatas) {
                EraserData eraserData = (EraserData) skillData;
                if (skillCooldownHandler.isCooldownOver(eraserData, player.getUniqueId())) {
                    World world = player.getWorld();
                    Location eyeLocation = player.getEyeLocation();
                    eyeLocation = eyeLocation.clone().add(eyeLocation.getDirection());
                    RayTraceResult rayTraceResult = world.rayTrace(eyeLocation, eyeLocation.getDirection(), eraserData.getRange(), FluidCollisionMode.NEVER, true, 1.0,
                            entity -> entity instanceof Player && !entity.equals(player));
                    if (rayTraceResult == null) {
                        continue;
                    }
                    Entity entity = rayTraceResult.getHitEntity();
                    if (entity != null) {
                        Player hitPlayer = (Player) entity;
                        skillData.ifConditionsTrue(() -> {
                            temporarilyRemoveHero(hitPlayer, player, eraserData);
                            skillCooldownHandler.startCooldown(eraserData, eraserData.getCooldown(), player.getUniqueId());
                        }, player, hitPlayer);
                    }
                }
            }
        }
    }

    public void temporarilyRemoveHero(@NotNull Player player, @Nullable Player remover, EraserData eraserData) {
        final Superhero oldPower = heroHandler.getSuperhero(player);
        heroHandler.setHeroInMemory(player, erased);
        Component removedMessage = MiniMessage.miniMessage().deserialize(eraserData.getRemovedMessage(), Placeholder.unparsed("player", player.getName()));
        if (remover != null) {
            remover.sendMessage(removedMessage);
        }
        player.sendMessage(removedMessage);
        Superheroes.getScheduling().entitySpecificScheduler(player).runDelayed(() -> {
            if (heroHandler.getSuperhero(player) == erased) {
                heroHandler.setHeroInMemory(player, oldPower);
                Component returnedMessage = MiniMessage.miniMessage().deserialize(eraserData.getReturnedMessage(), Placeholder.unparsed("player", player.getName()));
                if (remover != null) {
                    remover.sendMessage(returnedMessage);
                }
                player.sendMessage(returnedMessage);
            }
        }, () -> {}, eraserData.getDuration());
    }
}
