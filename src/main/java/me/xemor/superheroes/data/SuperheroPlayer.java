package me.xemor.superheroes.data;

import me.xemor.superheroes.Superhero;
import me.xemor.superheroes.Superheroes;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SuperheroPlayer {

    private final UUID uuid;
    private Superhero superhero;
    private long heroCommandTimestamp;

    public SuperheroPlayer(@NotNull UUID uuid, @NotNull Superhero superhero, long heroCommandTimestamp) {
        this.uuid = uuid;
        this.superhero = superhero;
        this.heroCommandTimestamp = heroCommandTimestamp;
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    @NotNull
    public Superhero getSuperhero() {
        return superhero;
    }

    public long getHeroCommandTimestamp() {
        return heroCommandTimestamp;
    }

    public void setSuperhero(@NotNull Superhero superhero) {
        this.superhero = superhero;
    }

    public void setHeroCommandTimestamp(long heroCommandTimestamp) {
        this.heroCommandTimestamp = heroCommandTimestamp;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof SuperheroPlayer superheroPlayer) {
            return uuid == superheroPlayer.uuid && superhero.equals(superheroPlayer.getSuperhero()) && heroCommandTimestamp == superheroPlayer.getHeroCommandTimestamp();
        }
        return false;
    }

    public boolean isCooldownOver() {
        return getCooldownLeft() <= 0;
    }

    public long getCooldownLeft() {
        long cooldown = Superheroes.getInstance().getConfigHandler().getHeroCommandCooldown() * 1000;
        return cooldown - (System.currentTimeMillis() - this.getHeroCommandTimestamp());
    }

    public boolean handleCooldown(Player player, Audience audience) {
        long seconds = this.getCooldownLeft() / 1000;
        if (!player.hasPermission("superheroes.hero.select.bypasscooldown")  && !this.isCooldownOver()) {
            Component message = MiniMessage.miniMessage().deserialize(Superheroes.getInstance().getConfigHandler().getHeroCooldownMessage(),
                    Placeholder.unparsed("player", player.getName()),
                    Placeholder.unparsed("currentcooldown", String.valueOf(Math.round(seconds))),
                    Placeholder.unparsed("cooldown", String.valueOf(Superheroes.getInstance().getConfigHandler().getHeroCommandCooldown())));
            audience.sendMessage(message);
            return false;
        }
        setHeroCommandTimestamp(System.currentTimeMillis());
        return true;
    }
}
