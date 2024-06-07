package me.xemor.superheroes;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldGuardSupport {

    private static StateFlag allowHeroes;

    public WorldGuardSupport() {
        super();
        allowHeroes = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get("allow-heroes");
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(WorldGuardHandler.FACTORY, null);
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

        public void playerCheckHero(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, StateFlag.State state) {
            Player player = Bukkit.getPlayer(localPlayer.getUniqueId());
            if (player == null) {
                Superheroes.getInstance().getLogger().warning("The player is null in WorldGuardHandler!");
            }
            assert player != null;
            if (state == StateFlag.State.DENY) {
                previousHero = Superheroes.getInstance().getHeroHandler().getSuperhero(player);
                Superheroes.getInstance().getHeroHandler().setHeroInMemory(player, Superheroes.getInstance().getHeroHandler().getNoPower(), false);
            }
            else {
                if (previousHero == null) return;
                Superheroes.getInstance().getHeroHandler().setHeroInMemory(player, previousHero, false);
            }
        }

        @Override
        protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, StateFlag.State state) {
            playerCheckHero(localPlayer, applicableRegionSet, state);
        }

        @Override
        protected boolean onSetValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, StateFlag.State state, StateFlag.State t1, MoveType moveType) {
            playerCheckHero(localPlayer, applicableRegionSet, state);
            return true;
        }

        @Override
        protected boolean onAbsentValue(LocalPlayer localPlayer, Location location, Location location1, ApplicableRegionSet applicableRegionSet, StateFlag.State state, MoveType moveType) {
            playerCheckHero(localPlayer, applicableRegionSet, state);
            return true;
        }
    }
}
