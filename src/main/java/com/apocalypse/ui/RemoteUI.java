package com.apocalypse.ui;

import com.apocalypse.core.Apocalypse;
import com.apocalypse.network.Connection;
import javax.swing.JToggleButton;

/**
 * Remote User Interface for viewing and manipulating the simulation state.
 */
public class RemoteUI extends javax.swing.JFrame {
    private boolean isRunning = true;
    private final Connection connection;
    private static Apocalypse simulator;

    public RemoteUI(Apocalypse simulator) {
        initComponents();
        buttonPlayStop.setText("PAUSE");
        RemoteUI.simulator = simulator;

        connection = new Connection(textFieldShelter, textFieldT1, textFieldT2, textFieldT3, textFieldT4,
                textFieldHZR1, textFieldHZR2, textFieldHZR3, textFieldHZR4, textFieldZZR1, textFieldZZR2,
                textFieldZZR3, textFieldZZR4, textFieldTopZ);
        connection.start();
    }

    private void initComponents() {

        textFieldTopZ = new javax.swing.JTextField();
        buttonPlayStop = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        textFieldT2 = new javax.swing.JTextField();
        textFieldShelter = new javax.swing.JTextField();
        textFieldT1 = new javax.swing.JTextField();
        textFieldT3 = new javax.swing.JTextField();
        textFieldT4 = new javax.swing.JTextField();
        textFieldZZR1 = new javax.swing.JTextField();
        textFieldZZR3 = new javax.swing.JTextField();
        textFieldZZR4 = new javax.swing.JTextField();
        textFieldZZR2 = new javax.swing.JTextField();
        textFieldHZR1 = new javax.swing.JTextField();
        textFieldHZR3 = new javax.swing.JTextField();
        textFieldHZR4 = new javax.swing.JTextField();
        textFieldHZR2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Zombie Apocalypse - Remote");

        textFieldTopZ.setEditable(false);

        buttonPlayStop.setText("PAUSE/RESUME");
        buttonPlayStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPlayStopActionPerformed(evt);
            }
        });

        jLabel1.setText("Most lethal Zombies");
        jLabel2.setText("Number of humans in shelter");
        jLabel3.setText("Number of humans in tunnels");
        jLabel4.setText("Number of humans in risk zones");
        jLabel5.setText("Number of zombies in risk zones");

        textFieldT2.setEditable(false);
        textFieldShelter.setEditable(false);
        textFieldT1.setEditable(false);
        textFieldT3.setEditable(false);
        textFieldT4.setEditable(false);
        textFieldZZR1.setEditable(false);
        textFieldZZR3.setEditable(false);
        textFieldZZR4.setEditable(false);
        textFieldZZR2.setEditable(false);
        textFieldHZR1.setEditable(false);
        textFieldHZR3.setEditable(false);
        textFieldHZR4.setEditable(false);
        textFieldHZR2.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(textFieldTopZ)
                        .addGap(3, 3, 3)
                        .addComponent(buttonPlayStop))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabel2)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(textFieldShelter, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(textFieldHZR1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                                .addComponent(textFieldHZR2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(textFieldHZR3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(textFieldHZR4, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(textFieldZZR1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldZZR2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(textFieldZZR3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(textFieldZZR4, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(textFieldT1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(textFieldT2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(textFieldT3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(textFieldT4, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(textFieldShelter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(textFieldT2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldT1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldT3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldT4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(textFieldHZR2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldHZR1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldHZR3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldHZR4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(textFieldZZR2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldZZR1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldZZR3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(textFieldZZR4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5))
                .addGap(58, 58, 58)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldTopZ, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonPlayStop))
                .addContainerGap(85, Short.MAX_VALUE))
        );

        pack();
    }

    private void buttonPlayStopActionPerformed(java.awt.event.ActionEvent evt) {
        if (isRunning) {
            isRunning = false;
            connection.sendPauseSignal(true);
        } else {
            isRunning = true;
            connection.sendPauseSignal(false);
        }
    }

    public JToggleButton getButtonPlayStop() {
        return buttonPlayStop;
    }

    public void setButtonPlayStop(JToggleButton buttonPlayStop) {
        this.buttonPlayStop = buttonPlayStop;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RemoteUI(simulator).setVisible(true);
            }
        });
    }

    // Variables declaration
    private javax.swing.JToggleButton buttonPlayStop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField textFieldHZR1;
    private javax.swing.JTextField textFieldHZR2;
    private javax.swing.JTextField textFieldHZR3;
    private javax.swing.JTextField textFieldHZR4;
    private javax.swing.JTextField textFieldShelter;
    private javax.swing.JTextField textFieldT1;
    private javax.swing.JTextField textFieldT2;
    private javax.swing.JTextField textFieldT3;
    private javax.swing.JTextField textFieldT4;
    private javax.swing.JTextField textFieldTopZ;
    private javax.swing.JTextField textFieldZZR1;
    private javax.swing.JTextField textFieldZZR2;
    private javax.swing.JTextField textFieldZZR3;
    private javax.swing.JTextField textFieldZZR4;
    // End of variables declaration
}
