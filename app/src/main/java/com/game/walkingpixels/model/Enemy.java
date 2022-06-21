package com.game.walkingpixels.model;

import java.io.Serializable;
import java.util.Random;

public class Enemy implements Serializable {

    private static final float NUMBER_OF_ENEMY_TYPES = 6;
    private static final float SOFT_LEVEL_BORDER = 100;
    private static final int LEVEL_SPAN = 3;

    private int health;
    private final int level;
    private final Block type;
    private final int maxAttack;
    private final int minAttack;
    private final int xp;

    private boolean isEnemyTurn = false;
    private static final double maxAttackDelay = 1;
    private double attackDelay = maxAttackDelay;

    public Enemy(int level){
        Random random = new Random();

        //set health
        health = (int) (Constants.enemyBaseHealth * Constants.enemyLevelFunction(level));

        //calculate attack
        maxAttack = (int) (Constants.enemyBaseMaxAttack * Constants.enemyLevelFunction(level));
        minAttack = random.nextInt(maxAttack);

        //xp
        xp = (int) (Constants.enemyBaseXp * Constants.enemyXpFunction(level));

        //get pseudo-random enemy type
        int id = random.nextInt(LEVEL_SPAN) + (int)Math.min(NUMBER_OF_ENEMY_TYPES - LEVEL_SPAN, level * (NUMBER_OF_ENEMY_TYPES / SOFT_LEVEL_BORDER));
        this.type = getType(id);

        //stronger enemies -> lower level at same strength
        this.level = Math.max(1, level - id * (int)(SOFT_LEVEL_BORDER / NUMBER_OF_ENEMY_TYPES));
    }

    private Block getType(int id){
        switch (id){
            case 0:
                switch (new Random().nextInt(3)){
                    case 0: return Block.BLUE_SLIME;
                    case 1: return Block.GREEN_SLIME;
                    case 2: return Block.PURPLE_SLIME;
                }
            case 1: return Block.BAT;
            case 2: return Block.EYE;
            case 3: return Block.DANGER_NOODLE;
            case 4: return Block.PLANT;
            case 5: return Block.GOLEM;
        }
        return Block.BLUE_SLIME;
    }

    public int getHealth() {
        return health;
    }

    public boolean isEnemyTurn(){
        return isEnemyTurn;
    }

    public void setEnemyTurn(boolean isEnemyTurn){
        if(!isEnemyTurn)
            attackDelay = maxAttackDelay;

        this.isEnemyTurn = isEnemyTurn;
    }

    public void damage(int damage){
        health -= damage;
        if(health < 0)
            health = 0;
    }

    public int attack(){
        Random random = new Random();
        return random.nextInt(maxAttack - minAttack) + minAttack;
    }

    public double getAttackDelay() {
        return attackDelay;
    }

    public void update(double dt){
        attackDelay -= dt;
        if(attackDelay < 0.0)
            attackDelay = 0.0;
    }

    public int getXp(){
        return xp;
    }

    public Block getType(){
        return type;
    }

    public int getLevel(){
        return level;
    }
}
