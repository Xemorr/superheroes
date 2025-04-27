package me.xemor.superheroes.skills.skilldata.spell;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;

public class Spells {

    private static final HashBiMap<String, Integer> nameToSpell = HashBiMap.create();
    private static final List<Class<? extends SpellData>> spellToData = new ArrayList<>();
    private static int counter = 0;

    static {
        registerSpell("TRANSMUTATION", TransmutationSpell.class);
        registerSpell("BLOCK", PlaceBlockSpell.class);
        registerSpell("PROJECTILE", LaunchProjectileSpell.class);
        registerSpell("LIGHTNING", LightningSpell.class);
        registerSpell("EXPLOSION", ExplosionSpell.class);
        registerSpell("FANGS", FangsSpell.class);
    }

    public static void registerSpell(String name, Class<? extends SpellData> clazz) {
        nameToSpell.put(name, counter);
        spellToData.add(clazz);
        counter++;
    }

    public static int getSpell(String name) {
        return nameToSpell.getOrDefault(name, -1);
    }

    public static String getName(int effect) {
        return nameToSpell.inverse().get(effect);
    }

    public static NamedType[] getNamedTypes() {
        return nameToSpell.entrySet().stream().map((entry) -> new NamedType(spellToData.get(entry.getValue()), entry.getKey() + "_SPELL")).toArray(NamedType[]::new);
    }

}
