package model;

public class PlayerState {
    private int playerMaxHealth, playerCurrentHealth, currentPotionId, currentWeapon;
    private String name;
    private int[] items = new int[0]; // Empty integer array
    private int[] Skills = new int[0]; // Empty integer array
    private int[] currentWeaponList = new int[0]; // Empty integer array

    public int getHealth() {
        return playerCurrentHealth;
    }
    public void updateHealth(int delta) {
        playerCurrentHealth += delta;
        if (playerCurrentHealth > playerMaxHealth) {
            playerCurrentHealth = playerMaxHealth;
        } else if (playerCurrentHealth < 0) {
            playerCurrentHealth = 0;
        }
    }
}
