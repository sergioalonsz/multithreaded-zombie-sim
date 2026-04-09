package com.apocalypse.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents the primary Shelter where Humans reside, sleep, and eat.
 * Manages food supplies and tracks human populations across its sub-areas securely.
 */
public class Shelter {
    private int foodAvailable;
    private final ReentrantLock lockFood;
    private final Condition foodAvailableCondition;

    private final List<Human> humansInRestZone;
    private final List<Human> humansInDiningRoom;
    private final List<Human> humansInCommonZone;

    private final ReentrantLock lockRestZone;
    private final ReentrantLock lockDiningRoom;
    private final ReentrantLock lockCommonZone;

    public Shelter() {
        this.foodAvailable = 0;
        this.lockFood = new ReentrantLock();
        this.foodAvailableCondition = lockFood.newCondition();

        this.humansInRestZone = new ArrayList<>();
        this.humansInDiningRoom = new ArrayList<>();
        this.humansInCommonZone = new ArrayList<>();

        this.lockRestZone = new ReentrantLock();
        this.lockDiningRoom = new ReentrantLock();
        this.lockCommonZone = new ReentrantLock();
    }

    /**
     * Safely increments the food available in the shelter.
     * @param amount The number of food units added by scavengers.
     */
    public void addFood(int amount) {
        lockFood.lock();
        try {
            foodAvailable += amount;
            foodAvailableCondition.signalAll(); // Notify hungry humans
        } finally {
            lockFood.unlock();
        }
    }

    /**
     * Blocks a Human thread until food is available, then safely deducts one unit.
     * @throws InterruptedException if the waiting process is interrupted.
     */
    public void consumeFood() throws InterruptedException {
        lockFood.lock();
        try {
            while (foodAvailable == 0) {
                foodAvailableCondition.await(); 
            }
            foodAvailable--;
        } finally {
            lockFood.unlock();
        }
    }

    /**
     * Gets current total food units securely.
     * @return Current amount of food.
     */
    public int getFoodAvailable() {
        lockFood.lock();
        try {
            return foodAvailable;
        } finally {
            lockFood.unlock();
        }
    }

    /**
     * Registers a Human as entering the resting zone.
     * @param human The Human resting.
     */
    public void enterRestZone(Human human) {
        lockRestZone.lock();
        try {
            humansInRestZone.add(human);
        } finally {
            lockRestZone.unlock();
        }
    }

    /**
     * Registers a Human as leaving the resting zone.
     * @param human The Human taking action.
     */
    public void leaveRestZone(Human human) {
        lockRestZone.lock();
        try {
            humansInRestZone.remove(human);
        } finally {
            lockRestZone.unlock();
        }
    }

    /**
     * Registers a Human as entering the dining room.
     * @param human The Human looking to eat.
     */
    public void enterDiningRoom(Human human) {
        lockDiningRoom.lock();
        try {
            humansInDiningRoom.add(human);
        } finally {
            lockDiningRoom.unlock();
        }
    }

    /**
     * Registers a Human as leaving the dining room.
     * @param human The Human that finished eating.
     */
    public void leaveDiningRoom(Human human) {
        lockDiningRoom.lock();
        try {
            humansInDiningRoom.remove(human);
        } finally {
            lockDiningRoom.unlock();
        }
    }

    /**
     * Registers a Human as entering the common zone (gathering hall).
     * @param human The Human entering.
     */
    public void enterCommonZone(Human human) {
        lockCommonZone.lock();
        try {
            humansInCommonZone.add(human);
        } finally {
            lockCommonZone.unlock();
        }
    }

    /**
     * Registers a Human as leaving the common zone.
     * @param human The Human exiting.
     */
    public void leaveCommonZone(Human human) {
        lockCommonZone.lock();
        try {
            humansInCommonZone.remove(human);
        } finally {
            lockCommonZone.unlock();
        }
    }

    /**
     * Retrieves a safe copy of the Humans in the resting room.
     * @return List of current resting Humans.
     */
    public List<Human> getHumansInRestZone() {
        lockRestZone.lock();
        try {
            return new ArrayList<>(humansInRestZone);
        } finally {
            lockRestZone.unlock();
        }
    }

    /**
     * Retrieves a safe copy of the Humans in the dining room.
     * @return List of current Humans eating or waiting to eat.
     */
    public List<Human> getHumansInDiningRoom() {
        lockDiningRoom.lock();
        try {
            return new ArrayList<>(humansInDiningRoom);
        } finally {
            lockDiningRoom.unlock();
        }
    }

    /**
     * Retrieves a safe copy of the Humans in the common zone.
     * @return List of current Humans in the common zone.
     */
    public List<Human> getHumansInCommonZone() {
        lockCommonZone.lock();
        try {
            return new ArrayList<>(humansInCommonZone);
        } finally {
            lockCommonZone.unlock();
        }
    }
}
