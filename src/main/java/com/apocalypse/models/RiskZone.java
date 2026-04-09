package com.apocalypse.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a dangerous area where Humans can scavenge for food
 * and face potential Zombie attacks.
 */
public class RiskZone {
    private final String id;
    private final List<Human> presentHumans;
    private final List<Human> unattackedHumans;
    private final List<Zombie> presentZombies;
    
    private final ReentrantLock lockHumans;
    private final ReentrantLock lockZombies;

    /**
     * Initializes a new Risk Zone with a specified identifier.
     * @param id The unique identifier for this zone.
     */
    public RiskZone(String id) {
        this.id = id;
        this.presentHumans = new ArrayList<>();
        this.unattackedHumans = new ArrayList<>();
        this.presentZombies = new ArrayList<>();
        this.lockHumans = new ReentrantLock();
        this.lockZombies = new ReentrantLock();
    }

    /**
     * Adds a Human to the risk zone.
     * @param human The Human entering the zone.
     */
    public void addHuman(Human human) {
        lockHumans.lock();
        try {
            presentHumans.add(human);
        } finally {
            lockHumans.unlock();
        }
    }

    /**
     * Marks a Human as currently not attacked.
     * @param human The Human to add to the unattacked list.
     */
    public void addUnattackedHuman(Human human) {
        lockHumans.lock();
        try {
            unattackedHumans.add(human);
        } finally {
            lockHumans.unlock();
        }
    }

    /**
     * Removes a Human from the unattacked list.
     * @param human The Human that was attacked or left.
     */
    public void removeUnattackedHuman(Human human) {
        lockHumans.lock();
        try {
            unattackedHumans.remove(human);
        } finally {
            lockHumans.unlock();
        }
    }

    /**
     * Gets a safe copy of the unattacked humans present.
     * @return A list of Humans currently unattacked.
     */
    public List<Human> getUnattackedHumans() {
        lockHumans.lock();
        try {
            return new ArrayList<>(unattackedHumans);
        } finally {
            lockHumans.unlock();
        }
    }

    /**
     * Removes a Human from the risk zone completely.
     * @param human The Human leaving the risk zone or dying.
     */
    public void removeHuman(Human human) {
        lockHumans.lock();
        try {
            presentHumans.remove(human);
        } finally {
            lockHumans.unlock();
        }
    }

    /**
     * Adds a Zombie to the risk zone.
     * @param zombie The Zombie entering the zone.
     */
    public void addZombie(Zombie zombie) {
        lockZombies.lock();
        try {
            presentZombies.add(zombie);
        } finally {
            lockZombies.unlock();
        }
    }

    /**
     * Removes a Zombie from the risk zone.
     * @param zombie The Zombie leaving the zone.
     */
    public void removeZombie(Zombie zombie) {
        lockZombies.lock();
        try {
            presentZombies.remove(zombie);
        } finally {
            lockZombies.unlock();
        }
    }

    /**
     * Gets a safe copy of all humans present in the zone.
     * @return A list of all Humans in the risk zone.
     */
    public List<Human> getHumans() {
        lockHumans.lock();
        try {
            return new ArrayList<>(presentHumans);
        } finally {
            lockHumans.unlock();
        }
    }

    /**
     * Gets a safe copy of all zombies present in the zone.
     * @return A list of all Zombies in the risk zone.
     */
    public List<Zombie> getZombies() {
        lockZombies.lock();
        try {
            return new ArrayList<>(presentZombies);
        } finally {
            lockZombies.unlock();
        }
    }

    /**
     * Retrieves the identifier of the risk zone.
     * @return The ID string.
     */
    public String getEntityId() {
        return id;
    }
}
