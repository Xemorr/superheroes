package me.xemor.superheroes2.skills.conditions;

import java.util.HashMap;

public class Conditions {

    private static final HashMap<String, Integer> nameToConditions = new HashMap<>();
    private static final HashMap<Integer, Class<? extends Condition>> conditionsToData = new HashMap<>();
    private static int counter = 0;

    static {
        registerConditions("HEALTH", HealthCondition.class);
        registerConditions("GLIDING", GlidingCondition.class);
        registerConditions("ONGROUND", OnGroundCondition.class);
        registerConditions("COOLDOWN", CooldownCondition.class);
        registerConditions("SNEAK", SneakCondition.class);
        registerConditions("TIME", TimeCondition.class);
    }

    public static void registerConditions(String name, Class<? extends Condition> effectDataClass) {
        nameToConditions.put(name, counter);
        conditionsToData.put(counter, effectDataClass);
        counter++;
    }

    public static Class<? extends Condition> getClass(int condition) { return conditionsToData.getOrDefault(condition, Condition.class); }

    public static int getCondition(String name) {
        return nameToConditions.getOrDefault(name, -1);
    }

}
