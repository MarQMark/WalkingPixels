package com.game.walkingpixels.model;

public class Spell {

    public static final int maxID = 16;

    private final int id;
    private final String path;
    private final String name;
    private final String description;
    private final double castTime;
    private final double maxDamage;
    private int usages;

    public Spell(int id, int usages){
        this.id = id;
        this.usages = usages;

        switch (id){
            case 0:
                path = "shapes/fire_1.png";
                name = "Spark";
                description = "This is the most basic fire spell.";
                castTime = 2.0;
                maxDamage = 25;
                break;

            case 1:
                path = "shapes/fire_2.png";
                name = "Flame";
                description = "A more advanced version of the spark spell. Even though this spell is still quite basic, it is not to be underestimated.";
                castTime = 4.0;
                maxDamage = 40;
                break;

            case 2:
                path = "shapes/fire_3.png";
                name = "Fire";
                description = "An intermediate fire spell. Only skilled scholars have mastered it.";
                castTime = 6.0;
                maxDamage = 70;
                break;

            case 3:
                path = "shapes/fire_4.png";
                name = "Inferno";
                description = "The culmination of fire magic mastery. Only the grandest of wizards will hope to get to this level. Let there be hell!";
                castTime = 8.0;
                maxDamage = 120;
                break;


            case 4:
                path = "shapes/water_1.png";
                name = "Drop";
                description = "At most this will annoy your enemy.";
                castTime = 1.0;
                maxDamage = 20;
                break;

            case 5:
                path = "shapes/water_2.png";
                name = "Splash";
                description = "Maybe if your enemy is wet, they will leave, but do not expect more out of this spell.";
                castTime = 3.0;
                maxDamage = 35;
                break;

            case 6:
                path = "shapes/water_3.png";
                name = "Stream";
                description = "A fast stream of water, which is quite dangerous.";
                castTime = 6.0;
                maxDamage = 60;
                break;

            case 7:
                path = "shapes/water_4.png";
                name = "Tsunami";
                description = "The peak of water magic. This will undoubted defeat you enemy.";
                castTime = 9.0;
                maxDamage = 110;
                break;


            case 8:
                path = "shapes/earth_1.png";
                name = "Pebble";
                description = "Maybe if you throw enough pebbles at your enemy they will perish. Like the saying goes: many a mickle makes a muckle!";
                castTime = 5.0;
                maxDamage = 10;
                break;

            case 9:
                path = "shapes/earth_2.png";
                name = "Stone";
                description = "With this, you can throw a stone at someone, you do not like. Maybe they end up with a scar and become the chosen one.";
                castTime = 7.0;
                maxDamage = 25;
                break;

            case 10:
                path = "shapes/earth_3.png";
                name = "Rock";
                description = "Intermediate earth spell. A solid choice.";
                castTime = 9.0;
                maxDamage = 45;
                break;

            case 11:
                path = "shapes/earth_4.png";
                name = "Earthquake";
                description = "Let the earth rumble with the might of your power.";
                castTime = 11.0;
                maxDamage = 85;
                break;


            case 12:
                path = "shapes/air_1.png";
                name = "Breeze";
                description = "If it is hot, this will be quite pleasant";
                castTime = 1.5;
                maxDamage = 30;
                break;

            case 13:
                path = "shapes/air_2.png";
                name = "Wind";
                description = "An adept air spell. With this you might blow your enemies away.";
                castTime = 2.5;
                maxDamage = 50;
                break;

            case 14:
                path = "shapes/air_3.png";
                name = "Tempest";
                description = "Named after one of the four true dragons, Veldora the Storm Dragon.";
                castTime = 5.0;
                maxDamage = 80;
                break;

            case 15:
                path = "shapes/air_4.png";
                name = "Thunderstorm";
                description = "Rain down on earth with the power of wind and lightning.";
                castTime = 7.5;
                maxDamage = 150;
                break;


            case 16:
                path = "shapes/meggido.png";
                name = "Meggido";
                description = "Unleash the Wrath of the Gods!";
                castTime = 10.0;
                maxDamage = 250;
                break;

            default:
                path = "shapes/fire_1.png";
                name = "NULL";
                description = "This is no spell.";
                castTime = 0.0;
                maxDamage = 0;
                break;
        }
    }

    public String getPath(){
        return path;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public double getCastTime() {return castTime;}
    public double getMaxDamage() {return maxDamage;}

    public int getId(){
        return id;
    }
    public void addUsages(){
        if(usages != -1)
            usages++;
    }
    public int getUsages() {
        return usages;
    }
}

