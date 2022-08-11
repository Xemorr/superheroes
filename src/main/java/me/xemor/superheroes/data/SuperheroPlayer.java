package me.xemor.superheroes.data;

import me.xemor.superheroes.Superhero;
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
        if (other instanceof SuperheroPlayer) {
            SuperheroPlayer superheroPlayer = (SuperheroPlayer) other;
            return uuid == superheroPlayer.uuid && superhero.equals(superheroPlayer.getSuperhero()) && heroCommandTimestamp == superheroPlayer.getHeroCommandTimestamp();
        }
        return false;
    }
}
