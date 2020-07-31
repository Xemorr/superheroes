package me.xemor.superheroes.Superpowers;

import org.bukkit.ChatColor;

public enum Power {

    Superhuman("&c&lSuperhuman", "You are a more powerful human"),
    Mole("&6&lThe Mole", "You can instant break certain blocks"),
    Aerosurfer("&7&lAerosurfer", "You can create platforms under you by sneak + jumping"),
    GravityGuy("&b&lGravity Guy", "When you sneak, gravity is inverted."),
    Floral("&a&lFloral", "Wherever you look, you create flowers. Right-clicking with flowers consumes them."),
    Phase("&3&lPhase", "You can phase through the ground whenever you sneak."),
    Trap("&7&lTrap", "You can trick players with your waifu disguise."),
    Strongman("&c&lStrongman", "You can pick up mobs and throw them."),
    Speedster("&b&lSpeedster", "You have Speed IV, 'gotta go fast'"),
    Gun("&8&lGun", "You have a gun."),
    Pyromaniac("&c&lPyromaniac", "Wherever you walk and look turns to fire, you are immune to fire."),
    Repulsion("&7&lRepulsion", "When you go near other entities, it is repelled away from you"),
    LavaWalker("&c&lLava Walker", "You have full fire immunity - and can walk on lava."),
    Chicken("&e&lChicken", "You are, the chicken."),
    ExtraHeartMan("&d&lExtra Heart Man", "has extra hearts. can give absorption hearts to other players by right-clicking."),
    Enderman("&5&lEnderman", "Allows you to teleport to wherever you're looking by punching. Get more Enderpearls."),
    Snowman("&f&lSnowman", "You never run out of snowballs and you place snow under yourself"),
    KingMidas("&6&lKingMidas", "Apples are turned golden. Clicking on ingot blocks, turns them into gold."),
    Frozone("&b&lFrozone", "You can walk on water, and every other entity around you is slowed."),
    Eraserhead("&8&lEraserhead", "When you look and people and sneak, they lose their power."),
    Creeper("&2&lCreeper", "If you hold sneak for two seconds, you explode."),
    Pickpocket("&d&lPickpocket", "Right clicking on players, lets you steal from their inventory"),
    Speleologist("&7&lSpeleologist", "Instantly smelt and double ores!"),
    Robot("&7&lRobot", "You're never hungry, but water hurts you."),
    Doomfist("&8&lDoomfist", "You are doomfist."),
    Slime("&a&lSlime", "You can bounce along the floor!"),
    Zeus("&e&lZeus", "Punching causes you to strike lightning wherever you look."),
    Scavenger("&e&lScavenger", "You unlock cheaper crafting recipes!"),
    Sorcerer("&5&lSorcerer", "You can create magical spell books and wield them using redstone.");

    String nameColourCode;
    String description;

    public String getNameColourCode() {
        return nameColourCode;
    }

    public String getDescription() {
        return description;
    }

    Power(String nameColourCode, String description) {
        this.nameColourCode = ChatColor.translateAlternateColorCodes('&', nameColourCode);
        this.description = ChatColor.translateAlternateColorCodes('&', description);
    }
}
