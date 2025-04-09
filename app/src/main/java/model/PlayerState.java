package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerState {
    private final int MAX_HEALTH;
    private int playerCurrentHealth;
    private final double DAMAGE_REDUCTION_PERCENTAGE = 0.2;
    private final String name;
    private int tokens;
    private int phase;
    private final List<int[]> items = Collections.synchronizedList(new ArrayList<>()); // Synchronized list for items [id, amount]
    private final List<int[]> skills = Collections.synchronizedList(new ArrayList<>()); // Synchronized list for skills [id, level, experience]



    // Default Constructor
    public PlayerState() {
        this.name = "Unknown";
        this.MAX_HEALTH = 100;
        this.playerCurrentHealth = 100; // Start with full health
        this.tokens = 20;
        this.phase = 1;
    }

    // Constructor with parameters
    /**
     * Initializes a new PlayerState with a given name and max health.
     * The player starts with full health.
     *
     * @param name      The name of the player.
     * @param maxHealth The maximum health of the player.
     */
    public PlayerState(String name, int maxHealth, int tokens) {
        this.name = name;
        this.MAX_HEALTH = maxHealth;
        this.playerCurrentHealth = maxHealth; // Start with full health
        this.tokens = tokens;
        this.phase = 1;
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
        if (playerCurrentHealth > MAX_HEALTH) {
            playerCurrentHealth = MAX_HEALTH;
        } else if (playerCurrentHealth < 0) {
            playerCurrentHealth = 0;
        }
    }
    public void modifyHealth(int delta) {
        if (delta < 0){
            delta = (int) (delta * (1 - DAMAGE_REDUCTION_PERCENTAGE));
        }
        updateHealth(delta); // Calls the private method internally
    }

    // Add a skill to the skill list
    public void addSkill(int id, int level, int experience) {
        synchronized(skills) {
            // if player already has skill then return
            for (int[] skill : skills) {
                if (skill[0] == id) {
                    return;
                }
            }

            int[] skill = {id, level, experience};
            skills.add(skill);
        }
    }

    public void setSkillLevelAndExperience(int id, int level, int experience) {
        if (level <= 0) {
            return;
        }

        synchronized(skills) {
            for (int[] skill : skills) {
                if (skill[0] == id) {
                    skill[1] = level;
                    skill[2] = experience;
                    return;
                }
            }
        }
    }

    // Add an item to the inventory
    public void addItem(int id, int amount) {
        if (amount <= 0) {
            return;
        }

        synchronized(items) {
            //add check for if item is in items already, if so iterate item[1] by 1
            for (int[] item : items) {
                if (item[0] == id) {
                    item[1] += amount;
                    return;
                }
            }

            // means item not in inventory so add it as a new item
            int[] item = {id, amount};
            items.add(item);
        }
    }

    // Remove an item from the inventory using item id
    public void removeItem(int id) {
        synchronized(items) {
            for (int i = 0; i < items.size(); i++) {
                int[] item = items.get(i);
                if (hasFoundItemToRemoveById(items, id, item, i)) {
                    return;
                }
            }
        }
    }

    private boolean hasFoundItemToRemoveById(List<int[]> itemList, int id, int[] item, int i) {
        if (item[0] == id) {
            if (item[1] == 1) {
                itemList.remove(i);
            } else {
                item[1] -= 1;
            }
            return true;
        }
        return false;
    }

    public void removeSkill(int id) {
        synchronized(skills) {
            for (int i = 0; i < skills.size(); i++) {
                int[] skill = skills.get(i);
                if (skill[0] == id) {
                    skills.remove(i);
                    return;
                }
            }
        }
    }

    public boolean canUpdateTokens(int delta) {
        int newTokenAmount = tokens + delta;
        int MAX_TOKEN_AMOUNT = 9999;
        return newTokenAmount >= 0 && newTokenAmount < MAX_TOKEN_AMOUNT;
    }

    public void updateTokens(int delta) {
        int newTokenAmount = tokens + delta;
        if (newTokenAmount < 0) {
            tokens = 0;
            return;
        }
        tokens += delta;
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

    public int getTotalSkillLevel() {
        int totalSkillLevel = 0;
        synchronized(skills) {
            for (int[] skill : skills) {
                totalSkillLevel += skill[1];
            }
        }
        return totalSkillLevel;
    }

    // tokens you lose on death (maxHealth/25) * totalSkillLevel
    public int getTokensLostOnDeath() {
        return (MAX_HEALTH/25) * getTotalSkillLevel();
    }

    public int getLevelOfSkill(int id) {
        synchronized(skills) {
            for (int[] skill : skills) {
                if (skill[0] == id) {
                    return skill[1];
                }
            }
        }
        return -1;
    }

    public int getExperienceOfSkill(int id) {
        synchronized(skills) {
            for (int[] skill : skills) {
                if (skill[0] == id) {
                    return skill[2];
                }
            }
        }
        return -1;
    }

    public int getHealth() {
        return playerCurrentHealth;
    }

    public int getPlayerMaxHealth() {
        return MAX_HEALTH;
    }

    public int[] getSkillList() {
        synchronized(skills) {
            return getArrayFromIndexInDoubleArray(skills, 0);
        }
    }

    public int[] getSkillLevels() {
        synchronized(skills) {
            return getArrayFromIndexInDoubleArray(skills, 1);
        }
    }

    public int[] getItemList() {
        synchronized(items) {
            return getArrayFromIndexInDoubleArray(items, 0);
        }
    }

    public int[] getItemAmountsList() {
        synchronized(items) {
            return getArrayFromIndexInDoubleArray(items, 1);
        }
    }

    public int[] getArrayFromIndexInDoubleArray(List<int[]> doubleArray, int index) {
        int[] newArray = new int[0];
        for (int[] array : doubleArray) {
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

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public int getPhase() {
        return phase;
    }

    public List<int[]> getItems() {
        return items;
    }

    public List<int[]> getSkills() {
        return skills;
    }
}