package com.apocalypse.network;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 * Periodically connects to the server to update the Remote UI text fields.
 */
public class Connection extends Thread {
    private Client client;
    private final JTextField tf1, tf2, tf3, tf4, tf5, tf6, tf7, tf8, tf9, tf10, tf11, tf12, tf13, tf14;

    public Connection(JTextField tf1, JTextField tf2, JTextField tf3, JTextField tf4, JTextField tf5, JTextField tf6,
                      JTextField tf7, JTextField tf8, JTextField tf9, JTextField tf10, JTextField tf11, JTextField tf12,
                      JTextField tf13, JTextField tf14) {
        this.tf1 = tf1;
        this.tf2 = tf2;
        this.tf3 = tf3;
        this.tf4 = tf4;
        this.tf5 = tf5;
        this.tf6 = tf6;
        this.tf7 = tf7;
        this.tf8 = tf8;
        this.tf9 = tf9;
        this.tf10 = tf10;
        this.tf11 = tf11;
        this.tf12 = tf12;
        this.tf13 = tf13;
        this.tf14 = tf14;
    }

    @Override
    public void run() {
        client = new Client(6666);
        while (true) {
            // Update main counters
            if (client.connect()) {
                String[] counterValues = client.getCounters();
                updateCounters(counterValues);
                client.disconnect();
            } else {
                updateCounters(null); // Reset bounds if offline
            }
    
            // Update top 3 zombies in a separate connection attempt
            if (client.connect()) {
                String[] top3Zombies = client.getTop3Zombies();
                updateTop3Zombies(top3Zombies);
                client.disconnect();
            } else {
                updateTop3Zombies(new String[]{"0", "0", "0"});
            }
    
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void updateCounters(String[] counterValues) {
        if (counterValues == null || counterValues.length < 13) {
            tf1.setText("0"); tf2.setText("0"); tf3.setText("0"); tf4.setText("0");
            tf5.setText("0"); tf6.setText("0"); tf7.setText("0"); tf8.setText("0");
            tf9.setText("0"); tf10.setText("0"); tf11.setText("0"); tf12.setText("0");
            tf13.setText("0");
            return;
        }
        tf1.setText(counterValues[0]);
        tf2.setText(counterValues[1]);
        tf3.setText(counterValues[2]);
        tf4.setText(counterValues[3]);
        tf5.setText(counterValues[4]);
        tf6.setText(counterValues[5]);
        tf7.setText(counterValues[6]);
        tf8.setText(counterValues[7]);
        tf9.setText(counterValues[8]);
        tf10.setText(counterValues[9]);
        tf11.setText(counterValues[10]);
        tf12.setText(counterValues[11]);
        tf13.setText(counterValues[12]);
    }

    private void updateTop3Zombies(String[] top3Zombies) {
        if (top3Zombies == null || top3Zombies.length < 3) {
            tf14.setText("0,0,0");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String zombie : top3Zombies) {
            sb.append(zombie).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        tf14.setText(sb.toString());
    }

    /**
     * Relays a pause toggle down to the server API.
     * @param isPaused The boolean flag representing if the simulation should be paused
     */
    public void sendPauseSignal(boolean isPaused) {
        if (client != null && client.connect()) {
            try {
                client.sendPauseSignal(isPaused);
            } catch (Exception e) {
                System.out.println("Error sending pause signal: " + e.getMessage());
            } finally {
                client.disconnect();
            }
        }
    }
}
