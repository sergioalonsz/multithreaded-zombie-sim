package com.apocalypse.core;

import com.apocalypse.models.*;
import com.apocalypse.network.Server;
import com.apocalypse.ui.GraphicalInterface;
import com.apocalypse.ui.RemoteUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;

/**
 * Core orchestrator for the Zombie Apocalypse Simulation.
 * Initializes the environment, spawns initial humans/zombies, and operates the main event loop.
 */
public class Apocalypse {
    private final List<Human> humans;
    private final List<Zombie> zombies;
    private final List<Tunnel> tunnels;
    private final List<RiskZone> riskZones;
    private final Shelter shelter;
    private final LoggerManager logger;
    private final GraphicalInterface gui;
    private final RemoteUI remoteUI;
    
    private boolean isPaused;
    private final Lock pauseSystemLock = new ReentrantLock();
    private final Condition pauseCondition = pauseSystemLock.newCondition();
    private final Object pauseMonitor;
    private final Random random;
    private final Server server;

    public Apocalypse() {
        this.humans = new ArrayList<>();
        this.zombies = new ArrayList<>();
        this.tunnels = new ArrayList<>();
        this.riskZones = new ArrayList<>();
        this.shelter = new Shelter();
        this.logger = new LoggerManager("simulation_logs.txt");
        this.gui = new GraphicalInterface(this);
        this.remoteUI = new RemoteUI(this);

        this.isPaused = false;
        this.pauseMonitor = new Object();
        this.random = new Random();
        this.server = new Server(this);

        initializeSystem();
        startSimulation();
        server.start();
    }

    private void initializeSystem() {
        gui.setVisible(true);
        remoteUI.setVisible(true);

        for (int i = 0; i < 4; i++) {
            riskZones.add(new RiskZone("RZ" + i));
        }

        for (int i = 0; i < 4; i++) {
            tunnels.add(new Tunnel("T" + i, riskZones.get(i), this));
        }

        RiskZone initialZone = riskZones.get(random.nextInt(4));
        Zombie patientZero = new Zombie("Z0000", initialZone, this);
        patientZero.start();
    }

    public void addZombie(Zombie z) {
        synchronized (zombies) {
            zombies.add(z);
        }
    }

    private void startSimulation() {
        new Thread(() -> {
            for (int i = 1; i <= 10000; i++) {
                try {
                    checkPause();
                } catch (InterruptedException ignored) {}

                Human human = new Human("H" + String.format("%04d", i), this);
                synchronized (humans) {
                    humans.add(human);
                }
                human.start();

                try {
                    Thread.sleep(random.nextInt(1500) + 500);
                } catch (InterruptedException e) {
                    logEvent("Error pausing human creation: " + e.getMessage());
                }
            }
            logEvent("All humans have been created.");
        }).start();
    }

    public void togglePause() {
        synchronized (pauseMonitor) {
            isPaused = !isPaused;
            if (!isPaused) {
                pauseMonitor.notifyAll();
            }
            logEvent("Simulation " + (isPaused ? "paused" : "resumed"));
        }
    }

    public void checkPause() throws InterruptedException {
        pauseSystemLock.lock();
        try {
            if (isPaused) {
                pauseCondition.await();
            }
        } finally {
            pauseSystemLock.unlock();
        }
    }

    public void pauseSimulation() {
        pauseSystemLock.lock();
        try {
            isPaused = true;
            gui.getButtonPlayStop().setText("RESUME");
            remoteUI.getButtonPlayStop().setText("RESUME");
            logEvent("Simulation paused.");
        } finally {
            pauseSystemLock.unlock();
        }
    }

    public void resumeSimulation() {
        pauseSystemLock.lock();
        try {
            isPaused = false;
            pauseCondition.signalAll();
            gui.getButtonPlayStop().setText("PAUSE");
            remoteUI.getButtonPlayStop().setText("PAUSE");
            logEvent("Simulation resumed.");
        } finally {
            pauseSystemLock.unlock();
        }
    }

    public void logEvent(String message) {
        logger.logEvent(message);
        updateUI();
    }

    public List<Tunnel> getTunnels() {
        return tunnels;
    }

    public List<RiskZone> getRiskZones() {
        return riskZones;
    }

    public Shelter getShelter() {
        return shelter;
    }

    public LoggerManager getLogger() {
        return logger;
    }

    public List<Human> getHumans() {
        synchronized (humans) {
            return new ArrayList<>(humans);
        }
    }

    public List<Zombie> getZombies() {
        synchronized (zombies) {
            return new ArrayList<>(zombies);
        }
    }

    public static void main(String[] args) {
        new Apocalypse();
    }

    public void logTunnelState(Tunnel tunnel) {
        logEvent("Tunnel " + tunnel.getEntityId() + " status | Occupied: " + tunnel.getHumanInside() 
            + " | WaitEnter: " + tunnel.getHumansToEnter().size() + " | WaitExit: " + tunnel.getHumansToExit().size());
    }

    public void updateUI() {
        SwingUtilities.invokeLater(() -> {
            try {
                gui.getTextFieldRestZone().setText(getHumanIds(shelter.getHumansInRestZone()));
                gui.getTextFieldDiningRoom().setText(getHumanIds(shelter.getHumansInDiningRoom()));
                gui.getTextFieldFood().setText(String.valueOf(shelter.getFoodAvailable()));
                gui.getTextFieldCommonZone().setText(getHumanIds(shelter.getHumansInCommonZone()));

                for (int i = 0; i < tunnels.size(); i++) {
                    Tunnel t = tunnels.get(i);
                    if (t != null) {
                        String idInside = t.getHumanInside() != null ? t.getHumanInside().getEntityId() : "";
                        switch (i) {
                            case 0:
                                gui.getTextFieldT11().setText(getHumanIds(t.getHumansToExit()));
                                gui.getTextFieldT12().setText(idInside);
                                gui.getTextFieldT13().setText(getHumanIds(t.getHumansToEnter()));
                                break;
                            case 1:
                                gui.getTextFieldT21().setText(getHumanIds(t.getHumansToExit()));
                                gui.getTextFieldT22().setText(idInside);
                                gui.getTextFieldT23().setText(getHumanIds(t.getHumansToEnter()));
                                break;
                            case 2:
                                gui.getTextFieldT31().setText(getHumanIds(t.getHumansToExit()));
                                gui.getTextFieldT32().setText(idInside);
                                gui.getTextFieldT33().setText(getHumanIds(t.getHumansToEnter()));
                                break;
                            case 3:
                                gui.getTextFieldT41().setText(getHumanIds(t.getHumansToExit()));
                                gui.getTextFieldT42().setText(idInside);
                                gui.getTextFieldT43().setText(getHumanIds(t.getHumansToEnter()));
                                break;
                        }
                    }
                }

                for (int i = 0; i < riskZones.size(); i++) {
                    RiskZone rz = riskZones.get(i);
                    if (rz != null) {
                        switch (i) {
                            case 0:
                                gui.getTextFieldZR11().setText(getHumanIds(rz.getHumans()));
                                gui.getTextFieldZR12().setText(getZombieIds(rz.getZombies()));
                                break;
                            case 1:
                                gui.getTextFieldZR21().setText(getHumanIds(rz.getHumans()));
                                gui.getTextFieldZR22().setText(getZombieIds(rz.getZombies()));
                                break;
                            case 2:
                                gui.getTextFieldZR31().setText(getHumanIds(rz.getHumans()));
                                gui.getTextFieldZR32().setText(getZombieIds(rz.getZombies()));
                                break;
                            case 3:
                                gui.getTextFieldZR41().setText(getHumanIds(rz.getHumans()));
                                gui.getTextFieldZR42().setText(getZombieIds(rz.getZombies()));
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error updating UI: " + e.getMessage());
            }
        });
    }

    public String getHumanIds(List<Human> group) {
        if (group == null || group.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Human h : group) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(h.getEntityId());
        }
        return sb.toString();
    }

    public String getZombieIds(List<Zombie> group) {
        if (group == null || group.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Zombie z : group) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(z.getEntityId());
        }
        return sb.toString();
    }

    public List<Zombie> getTop3Zombies() {
        List<Zombie> copy;
        synchronized (zombies) {
            copy = new ArrayList<>(zombies);
        }
        copy.sort((z1, z2) -> Integer.compare(z2.getKillCount(), z1.getKillCount()));
        return copy.subList(0, Math.min(3, copy.size()));
    }
}
