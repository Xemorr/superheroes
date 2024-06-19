package me.xemor.superheroes;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import me.xemor.superheroes.data.HeroHandler;
import me.xemor.superheroes.events.PlayerChangedSuperheroEvent;
import me.xemor.superheroes.events.SuperheroPlayerJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldGuardSupport implements Listener {

    private static StateFlag allowHeroes;

    public WorldGuardSupport() {
        super();
        allowHeroes = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get("allow-heroes");
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(WorldGuardHandler.FACTORY, null);
    }

    public static void setupFlag() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            // create a flag with the name "my-custom-flag", defaulting to true
            StateFlag flag = new StateFlag("allow-heroes", true);
            registry.register(flag);
        } catch (FlagConflictException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onHeroChange(PlayerChangedSuperheroEvent e) {
        if (e.getNewHero() == Superheroes.getInstance().getHeroHandler().getNoPower()) return;
        if (e.getCause() == PlayerChangedSuperheroEvent.Cause.WORLDGUARD) return;
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        Session session = sessionManager.getIfPresent(WorldGuardPlugin.inst().wrapPlayer(e.getPlayer()));
        if (session != null) {
            WorldGuardHandler handler = session.getHandler(WorldGuardHandler.class);
            if (handler != null) {
                if (getFlag(e.getPlayer(), allowHeroes) == StateFlag.State.DENY) {
                    handler.previousHero = e.getNewHero();
                    Superheroes.getScheduling().entitySpecificScheduler(e.getPlayer()).run(() -> {
                        Superheroes.getInstance().getHeroHandler().setHeroInMemory(e.getPlayer(), Superheroes.getInstance().getHeroHandler().getNoPower(), false, PlayerChangedSuperheroEvent.Cause.WORLDGUARD);
                    }, () -> {});
                }
            }
        }
    }

    @EventHandler
    public void onSuperheroLoad(SuperheroPlayerJoinEvent e) {
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(e.getPlayer());
        Session session = sessionManager.getIfPresent(localPlayer);
        if (session != null) {
            WorldGuardHandler handler = session.getHandler(WorldGuardHandler.class);
            if (handler != null) {
                handler.playerCheckHero(localPlayer, getFlag(e.getPlayer(), allowHeroes));
            }
        }
    }

    public StateFlag.State getFlag(Player player, StateFlag flag) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        Location loc = localPlayer.getLocation();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        return set.queryValue(localPlayer, flag);
    }

    public static class WorldGuardHandler extends FlagValueChangeHandler<StateFlag.State> {
        private Superhero previousHero = null;
        public static final Factory FACTORY = new Factory();
        public static class Factory extends Handler.Factory<WorldGuardHandler> {
            @Override
            public WorldGuardHandler create(Session session) {
                // create an instance of a handler for the particular session
                // if you need to pass certain variables based on, for example, the player
                // whose session this is, do it here
                return new WorldGuardHandler(session, allowHeroes);
            }
        }
        protected WorldGuardHandler(Session session, Flag<StateFlag.State> flag) {
            super(session, flag);
        }

        public void playerCheckHero(LocalPlayer localPlayer, StateFlag.State state) {
            Player player = Bukkit.getPlayer(localPlayer.getUniqueId());
            if (player == null) {
                Superheroes.getInstance().getLogger().warning("The player is null in WorldGuardHandler!");
            }
            assert player != null;
            if (state == StateFlag.State.DENY) {
                previousHero = Superheroes.getInstance().getHeroHandler().getSuperhero(player);
                Superheroes.getInstance().getHeroHandler().setHeroInMemory(player, Superheroes.getInstance().getHeroHandler().getNoPower(), false, PlayerChangedSuperheroEvent.Cause.WORLDGUARD);
            }
            else {
                if (previousHero == null) return;
                Superheroes.getInstance().getHeroHandler().setHeroInMemory(player, previousHero, false, PlayerChangedSuperheroEvent.Cause.WORLDGUARD);
            }
        }

        @Override
        protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, StateFlag.State state) {
            // Initialized by the onSuperheroLoad function
        }

        @Override
        protected boolean onSetValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, StateFlag.State state, StateFlag.State t1, MoveType moveType) {
            playerCheckHero(localPlayer, state);
            return true;
        }

        @Override
        protected boolean onAbsentValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, StateFlag.State state, MoveType moveType) {
            playerCheckHero(localPlayer, state);
            return true;
        }
    }
}
