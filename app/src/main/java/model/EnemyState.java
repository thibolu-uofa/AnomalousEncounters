package model;

public class EnemyState {
    private int id;
    private int tier;
    private final int enemyMaxHealth;
    private int enemyCurrentHealth;
    private final String name;
    private final int[] skills = new int[0]; // Empty integer array

    // Default Constructor
    public EnemyState() {
        this.name = "Unknown";
        this.enemyMaxHealth = 100;
        this.enemyCurrentHealth = 100; // Start with full health
    }

    // Constructor with parameters
    public EnemyState(String name, int maxHealth, int enemyId, int tier) {
        this.name = name;
        this.enemyMaxHealth = maxHealth;
        this.enemyCurrentHealth = maxHealth; // Start with full health
        this.id = enemyId;
        this.tier = tier;
    }

    // Update health
    private void updateHealth(int delta) {
        enemyCurrentHealth += delta;
        if (enemyCurrentHealth > enemyMaxHealth) {
            enemyCurrentHealth = enemyMaxHealth;
        } else if (enemyCurrentHealth < 0) {
            enemyCurrentHealth = 0;
        }
    }
    public void modifyHealth(int delta) {
        updateHealth(delta); // Calls the private method internally
    }

    // Getters (moved to the bottom)

    public int getHealth() {

        return enemyCurrentHealth;
    }

    public int[] getSkillList() {
        return skills;
    }

    public String getName() {
        return name;
    }

    public int getEnemyMaxHealth() {
        return enemyMaxHealth;
    }

    public int getEnemyCurrentHealth() {
        return enemyCurrentHealth;
    }

    public int getId() {
        return id;
    }

    public int getTier() {
        return tier;
    }
}//
