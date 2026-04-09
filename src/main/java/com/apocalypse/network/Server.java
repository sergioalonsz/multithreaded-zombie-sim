package com.apocalypse.network;

import com.apocalypse.core.Apocalypse;
import com.apocalypse.models.Zombie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Listens for incoming queries from remote GUIs and dispatches status counters.
 */
public class Server extends Thread {
    private final Apocalypse apocalypse;
    private ServerSocket serverSocket;
    private boolean closeRequested = false;

    public Server(Apocalypse app) {
        this.apocalypse = app;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(6666);
        } catch (IOException ex) {
            System.out.println("Error initializing the server: " + ex.getMessage());
            return;
        }

        System.out.println("The server has successfully started up.");

        while (!closeRequested) {
            try {
                try (Socket connection = serverSocket.accept()) {
                    DataInputStream input = new DataInputStream(connection.getInputStream());
                    DataOutputStream output = new DataOutputStream(connection.getOutputStream());
                    String message = input.readUTF();
                    String action = message.split(",")[0];

                    switch (action) {
                        case "getCounters":
                            List<String> counters = new ArrayList<>();

                            // Shelter total humans
                            counters.add(String.valueOf(
                                apocalypse.getShelter().getHumansInRestZone().size() +
                                apocalypse.getShelter().getHumansInCommonZone().size() +
                                apocalypse.getShelter().getHumansInDiningRoom().size()
                            ));

                            // Tunnels
                            for (int i = 0; i < 4; i++) {
                                if (i < apocalypse.getTunnels().size()) {
                                    counters.add(String.valueOf(
                                        apocalypse.getTunnels().get(i).getHumansToEnter().size() +
                                        apocalypse.getTunnels().get(i).getHumansToExit().size() +
                                        (apocalypse.getTunnels().get(i).getHumanInside() != null ? 1 : 0)
                                    ));
                                } else {
                                    counters.add("0");
                                }
                            }

                            // Risk zones - humans
                            for (int i = 0; i < 4; i++) {
                                if (i < apocalypse.getRiskZones().size()) {
                                    counters.add(String.valueOf(apocalypse.getRiskZones().get(i).getHumans().size()));
                                } else {
                                    counters.add("0");
                                }
                            }

                            // Risk zones - zombies
                            for (int i = 0; i < 4; i++) {
                                if (i < apocalypse.getRiskZones().size()) {
                                    counters.add(String.valueOf(apocalypse.getRiskZones().get(i).getZombies().size()));
                                } else {
                                    counters.add("0");
                                }
                            }

                            String countersStr = String.join(",", counters);
                            output.writeUTF(countersStr);
                            break;

                        case "pause":
                            String[] parts = message.split(",");
                            if (parts[1].equals("1")) {
                                apocalypse.pauseSimulation();
                            } else if (parts[1].equals("0")) {
                                apocalypse.resumeSimulation();
                            }
                            output.writeUTF("Pause: " + parts[1]);
                            break;
                        
                        case "getTop3Zombies":
                            List<Zombie> topZombies = apocalypse.getTop3Zombies();
                            String[] top3ZombiesArray = new String[3];
                            for (int i = 0; i < 3; i++) {
                                if (i < topZombies.size()) {
                                    Zombie z = topZombies.get(i);
                                    top3ZombiesArray[i] = z.getEntityId() + " - " + z.getKillCount();
                                } else {
                                    top3ZombiesArray[i] = "N/A - 0";
                                }
                            }
                            String top3ZombiesStr = String.join(",", top3ZombiesArray);
                            output.writeUTF(top3ZombiesStr);
                            break;

                        default:
                            output.writeUTF("Unrecognized command");
                            break;
                    }
                    
                }
            } catch (IOException ex) {
                // Ignore connection exceptions generated by dropped sockets
            }
        }

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            System.out.println("Error closing the server: " + ex.getMessage());
        }
    }

    public void terminate() {
        closeRequested = true;
    }
}
