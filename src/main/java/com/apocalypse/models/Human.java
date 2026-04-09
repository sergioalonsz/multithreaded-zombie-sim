package com.apocalypse.models;

import com.apocalypse.core.Apocalypse;
import java.util.Random;

/**
 * Represents a Human survivor navigating between the Shelter and Risk Zones.
 */
public class Human extends Thread {
    private final String id;
    private int foodCollected;
    private boolean marked;
    private final Apocalypse apocalypse;
    private final Random random;
    private RiskZone currentRiskZone;
    private boolean attacked;
    private boolean dead;
    private boolean attackInProgress = false;
    private boolean attackFinished = false;

    public Human(String id, Apocalypse apocalypse) {
        this.id = id;
        this.foodCollected = 0;
        this.marked = false;
        this.apocalypse = apocalypse;
        this.random = new Random();
        this.currentRiskZone = null;
        this.attacked = false;
        this.dead = false;
        
        apocalypse.logEvent("Human " + id + " spawned");
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted() && !dead) {
                apocalypse.getShelter().enterCommonZone(this);
                apocalypse.logEvent("Human " + id + " entered the Common Zone");        
                apocalypse.checkPause();
                prepare();
                if (dead || isInterrupted()) break;
                
                apocalypse.getShelter().leaveCommonZone(this);
                apocalypse.checkPause();
                apocalypse.logEvent("Human " + id + " left the Common Zone");
                if (dead || isInterrupted()) break;

                Tunnel selectedTunnel = selectTunnel();
                apocalypse.logEvent("Human " + id + " waiting to exit via tunnel " + selectedTunnel.getEntityId());
                apocalypse.checkPause();
                formGroupAndCrossTunnel(selectedTunnel);
                apocalypse.checkPause();
                apocalypse.logTunnelState(selectedTunnel);
                apocalypse.logEvent("Human " + id + " crossed out via tunnel " + selectedTunnel.getEntityId());
                if (dead || isInterrupted()) break;

                currentRiskZone = selectedTunnel.getConnectedRiskZone();
                currentRiskZone.addHuman(this);
                currentRiskZone.addUnattackedHuman(this);
                apocalypse.logEvent("Human " + id + " enters risk zone " + currentRiskZone.getEntityId());
                apocalypse.checkPause();

                collectFood();
                apocalypse.logEvent("Human " + id + " collected food");
                apocalypse.checkPause();
                if (dead || isInterrupted()) {
                    if (currentRiskZone != null) {
                        currentRiskZone.removeHuman(this);
                        currentRiskZone.removeUnattackedHuman(this);
                        apocalypse.updateUI();
                    }
                    break;
                }
                
                crossTunnelBack(selectedTunnel);
                apocalypse.logEvent("Human " + id + " crossed back via tunnel " + selectedTunnel.getEntityId());
                apocalypse.checkPause();
                if (dead || isInterrupted()) break;

                currentRiskZone = null;
                depositFood();
                apocalypse.logEvent("Human " + id + " deposited food");
                apocalypse.checkPause();
                if (dead || isInterrupted()) break;

                apocalypse.getShelter().enterRestZone(this);
                apocalypse.logEvent("Human " + id + " entered Rest Zone");
                apocalypse.checkPause();
                rest();
                apocalypse.logEvent("Human " + id + " rested");
                apocalypse.checkPause();
                apocalypse.getShelter().leaveRestZone(this);
                apocalypse.logEvent("Human " + id + " left Rest Zone");
                apocalypse.checkPause();
                if (dead || isInterrupted()) break;

                apocalypse.getShelter().enterDiningRoom(this);
                apocalypse.logEvent("Human " + id + " entered Dining Room");
                apocalypse.checkPause();
                eat();
                apocalypse.logEvent("Human " + id + " finished eating");
                apocalypse.checkPause();
                apocalypse.getShelter().leaveDiningRoom(this);
                apocalypse.logEvent("Human " + id + " left Dining Room");
                apocalypse.checkPause();
                if (dead || isInterrupted()) break;

                if (marked) {
                    apocalypse.getShelter().enterRestZone(this);
                    apocalypse.logEvent("Human " + id + " entered Rest Zone to recover");
                    apocalypse.checkPause();
                    recover();
                    apocalypse.logEvent("Human " + id + " recovered from being marked");
                    apocalypse.checkPause();
                    apocalypse.getShelter().leaveRestZone(this);
                    apocalypse.logEvent("Human " + id + " left Rest Zone post-recovery");
                    apocalypse.checkPause();
                    marked = false;
                    attacked = false;
                    if (dead || isInterrupted()) break;
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void prepare() {
        try {
            Thread.sleep(random.nextInt(1000) + 1000); 
            apocalypse.logEvent("Human " + id + " preparing in Common Zone");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            apocalypse.logEvent("Human " + id + " interrupted during preparation");
        }
    }

    private void eat() {
        try {
            apocalypse.getShelter().consumeFood();
            Thread.sleep(random.nextInt(2000) + 3000); 
            apocalypse.logEvent("Human " + id + " eating in Dining Room");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            apocalypse.logEvent("Human " + id + " interrupted while eating");
        }
    }

    private void recover() {
        try {
            Thread.sleep(random.nextInt(2000) + 3000);
            apocalypse.logEvent("Human " + id + " recovering in Rest Zone");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            apocalypse.logEvent("Human " + id + " interrupted while recovering");
        }
    }

    private Tunnel selectTunnel() {
        return apocalypse.getTunnels().get(random.nextInt(4));
    }

    private void formGroupAndCrossTunnel(Tunnel tunnel) {
        try {
            apocalypse.logEvent("Human " + id + " waiting to form group at tunnel " + tunnel.getEntityId());
            tunnel.formGroup(this); 
            apocalypse.logEvent("Human " + id + " formed group, crossing tunnel " + tunnel.getEntityId());
            tunnel.exitShelter(this); 
        } catch (InterruptedException e) {
            apocalypse.logEvent("Error: Human " + id + " interrupted forming group / crossing tunnel " + tunnel.getEntityId());
            Thread.currentThread().interrupt();
        } finally {
            apocalypse.logEvent("Human " + id + " finished crossing attempt for tunnel " + tunnel.getEntityId());
        }
    }

    private void collectFood() {
        try {
            long startTime = System.currentTimeMillis();
            long gatherTime = random.nextInt(2000) + 3000;

            while (System.currentTimeMillis() - startTime < gatherTime) {
                if (attacked && attackInProgress) {
                    synchronized (this) {
                        while (attackInProgress && !attackFinished) {
                            wait();
                        }
                    }
                    if (dead) return;
                    break;
                }
                Thread.sleep(100); 
            }

            if (!attacked && !dead) {
                foodCollected += 2;
                apocalypse.logEvent("Human " + id + " collected 2 food in area " + currentRiskZone.getEntityId());
            } else if (!dead) {
                apocalypse.logEvent("Human " + id + " was attacked and stopped collecting food");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            apocalypse.logEvent("Human " + id + " interrupted while gathering food");
        }
    }

    private void crossTunnelBack(Tunnel tunnel) {
        try {
            if (!dead) {
                currentRiskZone.removeHuman(this);
                apocalypse.logEvent("Human:" + getId() + " leaving Risk Zone:" + currentRiskZone.getEntityId());
                tunnel.enterShelter(this);
                apocalypse.logEvent("Human " + id + " returned to shelter via tunnel " + tunnel.getEntityId());
            }
        } catch (InterruptedException e) {
            dead = true;
            apocalypse.logEvent("Human:" + getId() + " dead");
        }
    }

    private void depositFood() {
        apocalypse.getShelter().addFood(foodCollected);
        apocalypse.logEvent("Human " + id + " deposits " + foodCollected + " food units");
        foodCollected = 0;
    }

    private void rest() {
        try {
            Thread.sleep((long) (2000 + Math.random() * 2000));
            apocalypse.logEvent("Human " + id + " resting in Rest Zone");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            apocalypse.logEvent("Human " + id + " interrupted checking rest status");
        }
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public String getEntityId() {
        return id;
    }

    private void die() {
        apocalypse.logEvent("Human " + id + " has died and turned into a Zombie");
        if (currentRiskZone != null) {
            Zombie newZombie = new Zombie("Z" + id.substring(1), currentRiskZone, apocalypse);
            newZombie.start();
            currentRiskZone.removeHuman(this);
            apocalypse.logEvent("Human " + id + " resurrects as zombie Z" + id.substring(1));
        }

        synchronized (apocalypse.getHumans()) {
            apocalypse.getHumans().remove(this);
        }
        
        apocalypse.getShelter().leaveCommonZone(this);
        apocalypse.getShelter().leaveRestZone(this);
        apocalypse.getShelter().leaveDiningRoom(this);

        for (Tunnel tunnel : apocalypse.getTunnels()) {
            tunnel.getHumansToExit().remove(this);
            tunnel.getHumansToEnter().remove(this);
        }

        this.dead = true;
        apocalypse.updateUI();
        this.interrupt();
    }

    /**
     * Flags human as under attack.
     */
    public void beAttacked() {
        this.attacked = true;
        this.attackInProgress = true;
    }

    /**
     * Completes an attack attempt on this Human by a Zombie.
     * @param success Whether the Zombie successfully infected the Human.
     * @throws InterruptedException if thread is interrupted.
     */
    public synchronized void finishAttack(boolean success) throws InterruptedException {
        this.attackInProgress = false;
        this.attackFinished = true;

        if (success) {
            die();
        } else {
            marked = true;
            apocalypse.logEvent("Human " + id + " survived the attack but is marked");
        }
        notify(); 
        apocalypse.checkPause();
    }
}
