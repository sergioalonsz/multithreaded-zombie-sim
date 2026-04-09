package com.apocalypse.models;

import com.apocalypse.core.Apocalypse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a connection between the Shelter and a RiskZone.
 * Controls the flow of humans entering and leaving the shelter in groups.
 */
public class Tunnel {
    private final String id;
    private final RiskZone connectedRiskZone;
    private final ReentrantLock lock;
    private final Condition conditionEnter;
    private final Condition conditionExit;
    
    private boolean occupied;
    private int waitingToEnter;
    private int waitingToExit;
    private final CyclicBarrier barrier; // Forms groups of 3
    
    private final List<Human> humansToEnter;
    private final List<Human> humansToExit;
    private Human humanInside;
    private final Apocalypse apocalypse;

    public Tunnel(String id, RiskZone connectedRiskZone, Apocalypse apocalypse) {
        this.id = id;
        this.connectedRiskZone = connectedRiskZone;
        this.lock = new ReentrantLock();
        this.conditionEnter = lock.newCondition();
        this.conditionExit = lock.newCondition();
        this.occupied = false;
        this.waitingToEnter = 0;
        this.waitingToExit = 0;
        this.barrier = new CyclicBarrier(3); // 3 humans needed to form a group exiting
        this.humansToEnter = new ArrayList<>();
        this.humansToExit = new ArrayList<>();
        this.apocalypse = apocalypse;
    }

    /**
     * Groups humans waiting to exit into teams of 3 using a CyclicBarrier.
     * @param human The Human wanting to form a group to exit.
     * @throws InterruptedException if thread is interrupted.
     */
    public void formGroup(Human human) throws InterruptedException {
        lock.lock();
        try {
            if (!humansToExit.contains(human)) {
                humansToExit.add(human);
                apocalypse.logEvent("Human " + human.getEntityId() + " joins exit group. " +
                        humansToExit.size() + " human(s) waiting to exit shelter via tunnel " + id);
            }
        } finally {
            lock.unlock();
        }

        try {
            barrier.await();
            lock.lock();
            try {
                apocalypse.logEvent("A full group of 3 humans formed at tunnel " + id);
                conditionExit.signalAll();
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            lock.lock();
            try {
                humansToExit.remove(human);
                apocalypse.logEvent("Human " + human.getEntityId() + " interrupted forming group at tunnel " + id);
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
            throw e;
        } catch (BrokenBarrierException e) {
            lock.lock();
            try {
                humansToExit.remove(human);
                apocalypse.logEvent("Group formation barrier broken for Human " + human.getEntityId());
            } finally {
                lock.unlock();
            }
            throw new InterruptedException("Broken barrier for human " + human.getEntityId());
        }
    }

    /**
     * Permits a human to exit the shelter via the tunnel.
     * @param human The Human exiting.
     * @throws InterruptedException if thread is interrupted.
     */
    public void exitShelter(Human human) throws InterruptedException {
        lock.lock();
        try {
            waitingToExit++;
            while (occupied || (waitingToEnter > 0 && humansToExit.size() < 3)) {
                apocalypse.logEvent("Human " + human.getEntityId() + " waiting to exit the shelter");
                conditionExit.await();
            }
            waitingToExit--;
            occupied = true;
            humanInside = human;
            
            apocalypse.logEvent("Human " + human.getEntityId() + " is crossing out through tunnel " + id);
        } catch (InterruptedException e) {
            waitingToExit--;
            humansToExit.remove(human);
            apocalypse.logEvent("Human " + human.getEntityId() + " interrupted waiting to exit");
            throw e;
        } finally {
            lock.unlock();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            lock.lock();
            try {
                occupied = false;
                humanInside = null;
                humansToExit.remove(human);
                apocalypse.logEvent("Human " + human.getEntityId() + " interrupted while mid-tunnel " + id);
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
            throw e;
        }

        lock.lock();
        try {
            occupied = false;
            humanInside = null;
            humansToExit.remove(human);
            apocalypse.logEvent("Human " + human.getEntityId() + " crossed out through tunnel " + id + " and left shelter");

            if (humansToExit.isEmpty() && barrier.getNumberWaiting() == 0) {
                apocalypse.logEvent("Group of 3 humans successfully left shelter via tunnel " + id);
                barrier.reset();
            }

            if (waitingToEnter > 0) {
                conditionEnter.signal();
            } else if (waitingToExit > 0) {
                conditionExit.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Permits a human to return to the shelter safely via the tunnel.
     * @param human The Human coming back in.
     * @throws InterruptedException if interrupted.
     */
    public void enterShelter(Human human) throws InterruptedException {
        lock.lock();
        try {
            waitingToEnter++;
            humansToEnter.add(human);
            apocalypse.logEvent("Human " + human.getEntityId() + " waiting to enter shelter via tunnel " + id);

            while (occupied) {
                conditionEnter.await();
            }

            waitingToEnter--;
            occupied = true;
            humanInside = human;
            humansToEnter.remove(human);
            apocalypse.logEvent("Human " + human.getEntityId() + " is crossing in through tunnel " + id);
        } catch (InterruptedException e) {
            waitingToEnter--;
            humansToEnter.remove(human);
            apocalypse.logEvent("Human " + human.getEntityId() + " interrupted while waiting to enter");
            throw e;
        } finally {
            lock.unlock();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            lock.lock();
            try {
                occupied = false;
                humanInside = null;
                humansToEnter.remove(human);
                apocalypse.logEvent("Human " + human.getEntityId() + " interrupted crossing in");
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
            throw e;
        }

        lock.lock();
        try {
            occupied = false;
            humanInside = null;
            apocalypse.logEvent("Human " + human.getEntityId() + " has entered shelter via tunnel " + id);

            if (waitingToEnter > 0) {
                conditionEnter.signal();
            } else if (waitingToExit > 0) {
                conditionExit.signal();
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Gets the connected RiskZone.
     * @return RiskZone corresponding to this Tunnel.
     */
    public RiskZone getConnectedRiskZone() {
        return connectedRiskZone;
    }

    public String getEntityId() {
        return id;
    }

    public List<Human> getHumansToEnter() {
        return humansToEnter;
    }

    public List<Human> getHumansToExit() {
        return humansToExit;
    }

    public Human getHumanInside() {
        return humanInside;
    }
}
