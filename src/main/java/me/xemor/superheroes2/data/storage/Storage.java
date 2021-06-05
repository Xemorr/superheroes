package me.xemor.superheroes2.data.storage;

import me.xemor.superheroes2.data.SuperheroPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    void saveSuperheroPlayer(@NotNull SuperheroPlayer superheroPlayer);

    @Nullable
    SuperheroPlayer loadSuperheroPlayer(@NotNull UUID uuid);

    CompletableFuture<SuperheroPlayer> loadSuperheroPlayerAsync(@NotNull UUID uuid);

}
