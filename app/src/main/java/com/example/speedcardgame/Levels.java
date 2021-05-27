package com.example.speedcardgame;

public class Levels {
    private int totalXP; // total xp
    private int level; // level
    private int xpInLevel; //player xp
    private int levelXP; //total xp in current level

    public Levels(int totalXP){
        level = 1;
        levelXP = 25;
        this.totalXP = totalXP;
        int num = this.totalXP;
        while (num >= levelXP) {
            this.level++;
            num = num - levelXP;
            levelXP = levelXP + levelXP;
        }
        this.xpInLevel = num;
    }

    public int getLevel() {
        return level;
    }

    public int getXpInLevel() {
        return xpInLevel;
    }

    public int getLevelXP() {
        return levelXP;
    }
}
