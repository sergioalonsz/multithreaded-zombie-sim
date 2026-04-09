package com.apocalypse.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handles socket communication with the Server to fetch simulation data.
 */
public class Client {
    private final int port;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;


    public Client(int port) {
        this.port = port;
    }

    /**
     * Attempts to connect to the designated server.
     * @return true if successful connection, false otherwise.
     */
    public boolean connect() {
        try {
            socket = new Socket(InetAddress.getLocalHost(), this.port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gracefully disconnects the socket and streams.
     */
    public void disconnect() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("Error disconnecting from server: " + e.getMessage());
        } finally {
            input = null;
            output = null;
            socket = null;
        }
    }

    /**
     * Retrieves the environment counters from the server.
     * @return Array containing counter values.
     */
    public String[] getCounters() {
        String[] defaultArray = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        if (output == null || input == null) {
            System.out.println("No connection established with the server");
            return defaultArray;
        }
        try {
            output.writeUTF("getCounters");
            String response = input.readUTF();
            String[] elements = response.split(",");
            
            if (elements.length < 13) {
                String[] result = new String[13];
                for (int i = 0; i < 13; i++) {
                    result[i] = i < elements.length ? elements[i] : "0";
                }
                return result;
            }
            return elements;
        } catch (IOException e) {
            System.out.println("Error fetching counters: " + e.getMessage());
            return defaultArray;
        }
    }

    /**
     * Retrieves the top 3 zombies from the server.
     * @return Array containing top zombie details.
     */
    public String[] getTop3Zombies() {
        String[] defaultArray = {"0", "0", "0"};
        if (output == null || input == null) {
            System.out.println("No connection established with the server");
            return defaultArray;
        }
        try {
            output.writeUTF("getTop3Zombies");
            String response = input.readUTF();
            String[] elements = response.split(",");
            
            if (elements.length < 3) {
                String[] result = new String[3];
                for (int i = 0; i < 3; i++) {
                    result[i] = i < elements.length ? elements[i] : "0";
                }
                return result;
            }
            return elements;
        } catch (IOException e) {
            System.out.println("Error fetching top 3 zombies: " + e.getMessage());
            return defaultArray;
        }
    }

    /**
     * Sends a pause or resume signal to the server.
     * @param isPaused true to pause, false to resume.
     */
    public void sendPauseSignal(boolean isPaused) {
        if (output == null) {
            System.out.println("No connection established with the server");
            return;
        }
        try {
            output.writeUTF("pause," + (isPaused ? "1" : "0"));
        } catch (IOException e) {
            System.out.println("Error sending pause signal: " + e.getMessage());
        }
    }
}
