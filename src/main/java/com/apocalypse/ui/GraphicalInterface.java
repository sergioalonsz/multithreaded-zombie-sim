package com.apocalypse.ui;

import com.apocalypse.core.Apocalypse;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI representing the locally hosted Apocalypse Simulation.
 * Displays state of the Shelter, Risk Zones, and Tunnels.
 */
public class GraphicalInterface extends JFrame {
    private boolean isRunning = true;
    private final Apocalypse simulator;

    private JToggleButton buttonPlayStop;
    private JTextField textFieldRestZone, textFieldDiningRoom, textFieldFood, textFieldCommonZone;
    private JTextField textFieldT11, textFieldT12, textFieldT13;
    private JTextField textFieldT21, textFieldT22, textFieldT23;
    private JTextField textFieldT31, textFieldT32, textFieldT33;
    private JTextField textFieldT41, textFieldT42, textFieldT43;
    private JTextField textFieldZR11, textFieldZR12;
    private JTextField textFieldZR21, textFieldZR22;
    private JTextField textFieldZR31, textFieldZR32;
    private JTextField textFieldZR41, textFieldZR42;

    public GraphicalInterface(Apocalypse simulator) {
        this.simulator = simulator;
        initComponents();
        setTitle("Zombie Apocalypse Simulation");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        JLabel titleLabel = new JLabel("Zombie Apocalypse Simulator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Grid
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // Column 1: Shelter
        JPanel shelterPanel = createSectionPanel("Shelter");
        textFieldRestZone = createFieldRow(shelterPanel, "Rest Zone:");
        textFieldDiningRoom = createFieldRow(shelterPanel, "Dining Room:");
        textFieldFood = createFieldRow(shelterPanel, "Food Available:");
        textFieldCommonZone = createFieldRow(shelterPanel, "Common Zone:");
        mainPanel.add(shelterPanel);

        // Column 2: Tunnels
        JPanel tunnelsPanel = createSectionPanel("Tunnels (Exit Q | Inside | Enter Q)");
        textFieldT11 = new JTextField(8); textFieldT12 = new JTextField(4); textFieldT13 = new JTextField(8);
        addTunnelRow(tunnelsPanel, "Tunnel 1", textFieldT11, textFieldT12, textFieldT13);
        
        textFieldT21 = new JTextField(8); textFieldT22 = new JTextField(4); textFieldT23 = new JTextField(8);
        addTunnelRow(tunnelsPanel, "Tunnel 2", textFieldT21, textFieldT22, textFieldT23);

        textFieldT31 = new JTextField(8); textFieldT32 = new JTextField(4); textFieldT33 = new JTextField(8);
        addTunnelRow(tunnelsPanel, "Tunnel 3", textFieldT31, textFieldT32, textFieldT33);

        textFieldT41 = new JTextField(8); textFieldT42 = new JTextField(4); textFieldT43 = new JTextField(8);
        addTunnelRow(tunnelsPanel, "Tunnel 4", textFieldT41, textFieldT42, textFieldT43);
        mainPanel.add(tunnelsPanel);

        // Column 3: Risk Zones
        JPanel riskZonesPanel = createSectionPanel("Risk Zones (Humans | Zombies)");
        textFieldZR11 = new JTextField(10); textFieldZR12 = new JTextField(10);
        addRiskZoneRow(riskZonesPanel, "Zone 1", textFieldZR11, textFieldZR12);

        textFieldZR21 = new JTextField(10); textFieldZR22 = new JTextField(10);
        addRiskZoneRow(riskZonesPanel, "Zone 2", textFieldZR21, textFieldZR22);

        textFieldZR31 = new JTextField(10); textFieldZR32 = new JTextField(10);
        addRiskZoneRow(riskZonesPanel, "Zone 3", textFieldZR31, textFieldZR32);

        textFieldZR41 = new JTextField(10); textFieldZR42 = new JTextField(10);
        addRiskZoneRow(riskZonesPanel, "Zone 4", textFieldZR41, textFieldZR42);
        mainPanel.add(riskZonesPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        buttonPlayStop = new JToggleButton("PAUSE/RESUME");
        buttonPlayStop.setFont(new Font("Arial", Font.BOLD, 18));
        buttonPlayStop.addActionListener(e -> {
            if (isRunning) {
                isRunning = false;
                simulator.pauseSimulation();
            } else {
                isRunning = true;
                simulator.resumeSimulation();
            }
        });
        footerPanel.add(buttonPlayStop);
        add(footerPanel, BorderLayout.SOUTH);
        
        setAllNonEditable();
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private JTextField createFieldRow(JPanel parent, String labelText) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(labelText));
        JTextField field = new JTextField(15);
        row.add(field);
        parent.add(row);
        return field;
    }

    private void addTunnelRow(JPanel parent, String title, JTextField f1, JTextField f2, JTextField f3) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(title));
        row.add(f1); row.add(f2); row.add(f3);
        parent.add(row);
    }

    private void addRiskZoneRow(JPanel parent, String title, JTextField f1, JTextField f2) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(new JLabel(title));
        row.add(f1); row.add(f2);
        parent.add(row);
    }

    private void setAllNonEditable() {
        for (Component c : getContentPane().getComponents()) {
            disableEdits(c);
        }
    }

    private void disableEdits(Component c) {
        if (c instanceof JTextField) {
            ((JTextField) c).setEditable(false);
        } else if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                disableEdits(child);
            }
        }
    }

    public JToggleButton getButtonPlayStop() { return buttonPlayStop; }
    public JTextField getTextFieldRestZone() { return textFieldRestZone; }
    public JTextField getTextFieldDiningRoom() { return textFieldDiningRoom; }
    public JTextField getTextFieldFood() { return textFieldFood; }
    public JTextField getTextFieldCommonZone() { return textFieldCommonZone; }

    public JTextField getTextFieldT11() { return textFieldT11; }
    public JTextField getTextFieldT12() { return textFieldT12; }
    public JTextField getTextFieldT13() { return textFieldT13; }
    public JTextField getTextFieldT21() { return textFieldT21; }
    public JTextField getTextFieldT22() { return textFieldT22; }
    public JTextField getTextFieldT23() { return textFieldT23; }
    public JTextField getTextFieldT31() { return textFieldT31; }
    public JTextField getTextFieldT32() { return textFieldT32; }
    public JTextField getTextFieldT33() { return textFieldT33; }
    public JTextField getTextFieldT41() { return textFieldT41; }
    public JTextField getTextFieldT42() { return textFieldT42; }
    public JTextField getTextFieldT43() { return textFieldT43; }

    public JTextField getTextFieldZR11() { return textFieldZR11; }
    public JTextField getTextFieldZR12() { return textFieldZR12; }
    public JTextField getTextFieldZR21() { return textFieldZR21; }
    public JTextField getTextFieldZR22() { return textFieldZR22; }
    public JTextField getTextFieldZR31() { return textFieldZR31; }
    public JTextField getTextFieldZR32() { return textFieldZR32; }
    public JTextField getTextFieldZR41() { return textFieldZR41; }
    public JTextField getTextFieldZR42() { return textFieldZR42; }
}
