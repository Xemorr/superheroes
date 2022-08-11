package me.xemor.superheroes.data.storage;

import me.xemor.superheroes.data.SuperheroPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    void saveSuperheroPlayer(@NotNull SuperheroPlayer superheroPlayer);
    @Nullable
    SuperheroPlayer loadSuperheroPlayer(@NotNull UUID uuid);
    CompletableFuture<Void> importSuperheroPlayers(List<SuperheroPlayer> superheroPlayers);
    List<SuperheroPlayer> exportSuperheroPlayers();

}
