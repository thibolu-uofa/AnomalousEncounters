package model;

public class EnemyState {
    private int enemyMaxHealth, enemyCurrentHealth, currentAttackId;
    private String name;
    private int[] skills = new int[0]; // Empty integer array
    private int[] items = new int[0]; // Empty integer array
    private int[] itemDrops = new int[0]; // Items dropped upon defeat

    // Default Constructor
    public EnemyState() {
        this.name = "Unknown";
        this.enemyMaxHealth = 100;
        this.enemyCurrentHealth = 100; // Start with full health
    }

    // Constructor with parameters
    public EnemyState(String name, int maxHealth) {
        this.name = name;
        this.enemyMaxHealth = maxHealth;
        this.enemyCurrentHealth = maxHealth; // Start with full health
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

    // Add an ability to the ability list
    private void addAbility(int id) {
        skills = expandArray(skills, id);
    }

    // Add an item to the enemy's inventory
    private void addItemDrops(int id) {
        items = expandArray(items, id);
    }

    // Remove an item from the enemy's inventory
    private void removeItem(int id) {
        items = removeFromArray(items, id);
    }

    // Add an item to the enemy's drop list
    public void addItemDrop(int id) {
        itemDrops = expandArray(itemDrops, id);
    }

    // Utility method to expand an array by adding a new element
    private int[] expandArray(int[] array, int newElement) {
        int[] newArray = new int[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = newElement;
        return newArray;
    }

    // Utility method to remove an element from an array
    private int[] removeFromArray(int[] array, int element) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                index = i;
                break;
            }
        }
        if (index == -1) return array; // Element not found

        int[] newArray = new int[array.length - 1];
        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != index) {
                newArray[j++] = array[i];
            }
        }
        return newArray;
    }

    // Getters (moved to the bottom)

    public int getHealth() {

        return enemyCurrentHealth;
    }

    public int[] getAbilityList() {

        return skills;
    }


    // Get the list of items the enemy drops when defeated
    public int[] getItemDrops() {

        return itemDrops;
    }
}//