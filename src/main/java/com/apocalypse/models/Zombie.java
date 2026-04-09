package com.apocalypse.models;

import com.apocalypse.core.Apocalypse;
import java.util.List;
import java.util.Random;

/**
 * Represents a Zombie entity roaming Risk Zones, attempting to infect Humans.
 */
public class Zombie extends Thread {
    private String id;
    private RiskZone currentArea;
    private int killCount;
    private Apocalypse apocalypse;
    private Random random;

    /**
     * Initializes a new Zombie.
     * @param id The Zombie's unique identifier.
     * @param initialArea The initial RiskZone.
     * @param simulator The core Apocalypse orchestrator.
     */
    public Zombie(String id, RiskZone initialArea, Apocalypse simulator) {
        this.id = id;
        this.currentArea = initialArea;
        this.killCount = 0;
        this.apocalypse = simulator;
        this.random = new Random();
        apocalypse.addZombie(this);
        apocalypse.logEvent("Zombie " + this.id + " spawned");
    }

    @Override
    public void run() {
        try {
            wander();
            apocalypse.checkPause();
            while (true) {
                try {
                    Thread.sleep(random.nextInt(1000) + 2000); // 2 to 3 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                apocalypse.checkPause();
                attackHuman();
                wander();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void wander() {
        RiskZone newArea = apocalypse.getRiskZones().get(random.nextInt(4));

        currentArea.removeZombie(this);
        currentArea = newArea;
        currentArea.addZombie(this);

        apocalypse.logEvent("Zombie " + id + " moved to area " + currentArea.getEntityId());
    }

    private void attackHuman() throws InterruptedException {
        synchronized (currentArea) {
            List<Human> humans = currentArea.getHumans();
            List<Human> unattackedHumans = currentArea.getUnattackedHumans();

            if (humans.isEmpty()) {
                apocalypse.logEvent("Zombie " + id + " found no humans in area " + currentArea.getEntityId());
                apocalypse.checkPause();
                return;
            }

            Human target = null;
            for (Human human : humans) {
                if (unattackedHumans.contains(human)) {
                    target = human;
                    break;
                }
            }

            if (target == null) {
                apocalypse.logEvent("Zombie " + id + " found no unattacked humans in area " + currentArea.getEntityId());
                apocalypse.checkPause();
                return;
            }

            currentArea.removeUnattackedHuman(target);
            target.beAttacked();

            try {
                apocalypse.logEvent("Zombie " + id + " is attacking human " + target.getEntityId());
                apocalypse.checkPause();
                
                int attackDuration = random.nextInt(1000) + 500;
                Thread.sleep(attackDuration);

                // 33% chance to successfully infect
                boolean success = random.nextInt(3) == 0;
                target.finishAttack(success);
                apocalypse.checkPause();
                
                if (success) {
                    killCount++;
                    apocalypse.logEvent("Zombie " + id + " infected human " + target.getEntityId() + 
                        " (Kills: " + killCount + ")");
                    apocalypse.checkPause();
                } else {
                    apocalypse.logEvent("Zombie " + id + " failed and human " + target.getEntityId() + 
                        " defended themselves");
                    apocalypse.checkPause();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getKillCount() {
        return killCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RiskZone getCurrentArea() {
        return currentArea;
    }

    public void setCurrentArea(RiskZone currentArea) {
        this.currentArea = currentArea;
    }

    public Apocalypse getApocalypse() {
        return apocalypse;
    }

    public void setApocalypse(Apocalypse apocalypse) {
        this.apocalypse = apocalypse;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public String getEntityId() {
        return id;
    }
}
