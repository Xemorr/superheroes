package me.xemor.superheroes.skills.skilldata.spell;

import com.google.common.collect.HashBiMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Spells {

    private static final HashBiMap<String, Integer> nameToSpell = HashBiMap.create();
    private static final List<BiFunction<Integer, ConfigurationSection, Spell>> spellToData = new ArrayList<>();
    private static int counter = 0;

    static {
        registerSpell("TRANSMUTATION", TransmutationSpell::new);
        registerSpell("FIRE", (spell, section) -> new PlaceBlockSpell(Material.FIRE, spell, section));
        registerSpell("LAVA", (spell, section) -> new PlaceBlockSpell(Material.LAVA, spell, section));
        registerSpell("WATER", (spell, section) -> new PlaceBlockSpell(Material.WATER, spell, section));
        registerSpell("SNOWBALL", (spell, section) -> new LaunchProjectileSpell(Snowball.class, spell, section));
        registerSpell("EGG", (spell, section) -> new LaunchProjectileSpell(Egg.class, spell, section));
        registerSpell("ARROW", (spell, section) -> new LaunchProjectileSpell(SpectralArrow.class, spell, section));
        registerSpell("FIREBALL", (spell, section) -> new LaunchProjectileSpell(Fireball.class, spell, section));
        registerSpell("TRIDENT", (spell, section) -> new LaunchProjectileSpell(Trident.class, spell, section));
        registerSpell("LIGHTNING", LightningSpell::new);
        registerSpell("EXPLOSION", ExplosionSpell::new);
        registerSpell("FANGS", FangsSpell::new);
    }

    public static void registerSpell(String name, BiFunction<Integer, ConfigurationSection, Spell> constructor) {
        nameToSpell.put(name, counter);
        spellToData.add(constructor);
        counter++;
    }

    public static Spell createSpell(int spell, ConfigurationSection section) {
        return spellToData.get(spell).apply(spell, section);
    }

    public static int getSpell(String name) {
        return nameToSpell.getOrDefault(name, -1);
    }

    public static String getName(int effect) {
        return nameToSpell.inverse().get(effect);
    }

}
