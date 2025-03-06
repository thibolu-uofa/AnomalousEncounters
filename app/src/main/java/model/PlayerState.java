package model;
public class PlayerState {
    private int playerMaxHealth, playerCurrentHealth, currentPotionId, currentWeapon;
    private String name;
    private int[] items = new int[0]; // Empty integer array
    private int[] skills = new int[0]; // Empty integer array
    private int[] currentWeaponList = new int[0]; // Empty integer array

    // Default Constructor
    /**
     * Default constructor initializes the player with no name and a default max health of 100.
     */
    public PlayerState() {
        this.name = "Unknown";
        this.playerMaxHealth = 100;
        this.playerCurrentHealth = 100; // Start with full health
    }

    // Constructor with parameters
    /**
     * Initializes a new PlayerState with a given name and max health.
     * The player starts with full health.
     *
     * @param name      The name of the player.
     * @param maxHealth The maximum health of the player.
     */
    public PlayerState(String name, int maxHealth) {
        this.name = name;
        this.playerMaxHealth = maxHealth;
        this.playerCurrentHealth = maxHealth; // Start with full health
    }

    // Update health
    /**
     * Updates the player's current health by a specified amount.
     * Ensures health does not exceed max health or drop below 0.
     *
     * @param delta The amount to change the health by (can be positive or negative).
     */
    private void updateHealth(int delta) {
        playerCurrentHealth += delta;
        if (playerCurrentHealth > playerMaxHealth) {
            playerCurrentHealth = playerMaxHealth;
        } else if (playerCurrentHealth < 0) {
            playerCurrentHealth = 0;
        }
    }

    // Add a skill to the skill list
    /**
     * Adds a skill to the player's skill list.
     *
     * @param id The skill ID to be added.
     */
    private void addSkill(int id) {
        skills = expandArray(skills, id);
    }

    // Add an item to the inventory
    /**
     * Adds an item to the player's inventory.
     *
     * @param id The item ID to be added.
     */
    private void addItem(int id) {
        items = expandArray(items, id);
    }

    // Remove an item from the inventory
    /**
     * Removes an item from the player's inventory.
     * If the item is not found, the inventory remains unchanged.
     *
     * @param id The item ID to be removed.
     */
    private void removeItem(int id) {
        items = removeFromArray(items, id);
    }

    // Utility method to expand an array by adding a new element
    /**
     * Expands an integer array by adding a new element.
     *
     * @param array      The original array.
     * @param newElement The new element to be added.
     * @return A new array containing the old elements and the new element.
     */
    private int[] expandArray(int[] array, int newElement) {
        int[] newArray = new int[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = newElement;
        return newArray;
    }

    // Utility method to remove an element from an array
    /**
     * Removes an element from an integer array.
     *
     * @param array   The original array.
     * @param element The element to remove.
     * @return A new array without the specified element. Returns the original array if the element is not found.
     */
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


    /**
     * Retrieves the player's current health.
     *
     * @return The player's current health.
     */
    public int getHealth() {
        return playerCurrentHealth;
    }

    /**
     * Retrieves the player's current list of skill IDs.
     *
     * @return An array of skill IDs.
     */
    public int[] getSkillList() {
        return skills;
    }

    /**
     * Retrieves the player's current list of item IDs.
     *
     * @return An array of item IDs.
     */
    public int[] getItemList() {
        return items;
    }
}