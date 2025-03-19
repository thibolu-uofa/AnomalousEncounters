package model;

import java.util.ArrayList;

public class PlayerState {
    private int playerMaxHealth, playerCurrentHealth, currentPotionId, currentWeapon;
    private String name;
    private int tokens;
    private ArrayList<int[]> items = new ArrayList<>(); // Empty double integer array (first int is the item id, second is the number of items)
    private ArrayList<int[]>  skills = new ArrayList<>(); // Empty double integer array (first int is the skill id, second is the skill level)
    private int[] currentWeaponList = new int[0]; // Empty integer array

    // Default Constructor
    /**
     * Default constructor initializes the player with no name and a default max health of 100.
     */
    public PlayerState() {
        this.name = "Unknown";
        this.playerMaxHealth = 100;
        this.playerCurrentHealth = 100; // Start with full health
        this.tokens = 0;
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
        this.tokens = 0;
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
    public void modifyHealth(int delta) {
        updateHealth(delta); // Calls the private method internally
    }
    // Add a skill to the skill list
    /**
     * Adds a skill to the player's skill list.
     *
     * @param id The skill ID to be added.
     * @param level The level of the skill to be added.
     */
    public void addSkill(int id, int level) {
        // if player already has skill then return
        for (int[] skill: skills) {
            if (skill[0] == id) {
                return;
            }
        }

        int [] skill = {id, level};
        skills.add(skill);
    }

    // Add an item to the inventory
    /**
     * Adds an item to the player's inventory.
     *
     * @param id The item ID to be added.
     * @param amount The amount of the item to add.
     */
    public void addItem(int id, int amount) {
        //add check for if item is in items already, if so iterate item[1] by 1
        for (int[] item: items) {
            if (item[0] == id) {
                item[1] += 1;
                return;
            }
        }

        // means item not in inventory so add it as a new item
        int [] item = {id, amount};
        items.add(item);
    }

    // Remove an item from the inventory
    /**
     * Removes an item from the player's inventory.
     * If the item is not found, the inventory remains unchanged.
     *
     * @param id The item ID to be removed.
     */
    public void removeItem(int id) {
        for (int[] item: items) {
            if (item[0] == id && item[1] == 1) {
                items.remove(item);
                return;
            }

            if (item[0] == id) {
                item[1] -= 1;
            }
        }
    }

    public void removeSkill(int id) {
        items.removeIf(skill -> skill[0] == id);
    }

    public boolean updateTokens(int delta) {
        int newTokenAmount = tokens + delta;
        if (newTokenAmount < 0) {
            return false;
        }
        tokens += delta;
        return true;
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

    public int getPlayerMaxHealth() {
        return playerMaxHealth;
    }

    /**
     * Retrieves the player's current list of skill IDs.
     *
     * @return An array of skill IDs.
     */
    public int[] getSkillList() {
        return getArrayFromIndexInDoubleArray(skills, 0);
    }

    public int[] getSkillLevels() {
        return getArrayFromIndexInDoubleArray(skills, 1);
    }

    /**
     * Retrieves the player's current list of item IDs.
     *
     * @return An array of item IDs.
     */
    public int[] getItemList() {
        return getArrayFromIndexInDoubleArray(items, 0);
    }//

    public int[] getItemAmountsList() {
        return getArrayFromIndexInDoubleArray(items, 1);
    }

    public int[] getArrayFromIndexInDoubleArray(ArrayList<int[]> doubleArray, int index) {
        int[] newArray = new int[0];
        for (int[] array: doubleArray) {
            newArray = expandArray(newArray, array[index]);
        }
        return newArray;
    }

    public int getTokens() {
        return tokens;
    }

    public String getName() {
        return name;
    }
}