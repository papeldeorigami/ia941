/** ***************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *    Klaus Raizer, Andre Paraense, Ricardo Ribeiro Gudwin
 **************************************************************************** */
package support;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import memory.CreatureInnerSense;
import org.xguzm.pathfinding.grid.GridCell;
import support.PathPlan.Finder;
import ws3dproxy.CommandExecException;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Thing;
import ws3dproxy.model.World;
import ws3dproxy.model.WorldPoint;
import ws3dproxy.util.Constants;

class MVTimerTask extends TimerTask {

    MindView mv;
    boolean enabled = true;

    public MVTimerTask(MindView mvi) {
        mv = mvi;
    }

    public void run() {
        if (enabled) {
            mv.tick();
        }
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }
}

/**
 *
 * @author rgudwin
 */
public class MindView extends javax.swing.JFrame {

    Timer t;
    List<Memory> mol = new ArrayList<>();
    PathPlan pathPlan;
    WorldPoint position;
    double pitch = 0.0;

//    int j=0;
    Random r = new Random();
    private long lastPathPlanUpdate;
    private WS3DProxy proxy;

    /**
     * Creates new form NewJFrame
     */
    public MindView(String name) {
        initComponents();
        setTitle(name);
        targetXLabel.setText(String.valueOf(PathPlan.INITIAL_DESTINATION_X));
        targetYLabel.setText(String.valueOf(PathPlan.INITIAL_DESTINATION_Y));
    }

    public void setProxy(WS3DProxy proxy) {
        this.proxy = proxy;
    }
    
    public void addMO(Memory moi) {
        mol.add(moi);
    }

    public void StartTimer() {
        t = new Timer();
        MVTimerTask tt = new MVTimerTask(this);
        t.scheduleAtFixedRate(tt, 0, 500);
    }

    public void tick() {
        String alltext = "";
        if (mol.size() != 0) {
            for (Memory mo : mol) {
                if (mo.getI() != null) {
                    //Class cl = mo.getT();
                    //Object k = cl.cast(mo.getI());
                    Object k = mo.getI();
                    String moName = mo.getName();

                    if (moName.equals("INNER")) {
                        CreatureInnerSense cis = (CreatureInnerSense) k;
                        position = cis.position;
                        pitch = cis.pitch;
                    }

                    if (moName.equals("PATH_PLAN")) {
                        pathPlan = (PathPlan) k;
                    } else if (moName.equals("KNOWN_APPLES") || moName.equals("VISION")) {
                        //alltext += mo.getName()+": "+k+"<-> ";
                        alltext += mo.getName() + ": [ ";
                        CopyOnWriteArrayList<Thing> l = new CopyOnWriteArrayList<>((List<Thing>) k);
                        for (Thing t : l) {
                            String kindofthing = "t";
                            if (t.getCategory() == Constants.categoryPFOOD) {
                                kindofthing = "a";
                            }
                            alltext += kindofthing + "(" + (int) (t.getX1() + t.getX2()) / 2 + "," + (int) (t.getY1() + t.getY2()) / 2 + ") ";
                        }
                        alltext += "]\n";
                    } else if (moName.equals("CLOSEST_APPLE")) {
                        Thing t = (Thing) k;
                        String kindofthing = "t";
                        if (t.getCategory() == 21) {
                            kindofthing = "a";
                        }
                        alltext += moName + ": " + kindofthing + "(" + (int) (t.getX1() + t.getX2()) / 2 + "," + (int) (t.getY1() + t.getY2()) / 2 + ")\n";
                    } else {
                        alltext += mo.getName() + ": " + k + "\n";
                    }
                } else //alltext += mo.getName()+": "+mo.getI()+"\n";
                {
                    alltext += mo.getName() + ":\n";
                }
            }
        }
        text.setText(alltext);
        updatePositionLabel();
        refreshPlan();
//        j++;
//        if (j == 7) {
//            try {
//              World.createFood(0,r.nextInt(800) , r.nextInt(600));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            j = 0;
//        }
        //System.out.println("i");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        text = new javax.swing.JTextArea();
        actionTab = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        targetXLabel = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        targetYLabel = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        aStarButton = new javax.swing.JRadioButton();
        thetaStarButton = new javax.swing.JRadioButton();
        allowDiagonal = new javax.swing.JCheckBox();
        jumpPointButton = new javax.swing.JRadioButton();
        autoButton = new javax.swing.JRadioButton();
        planTab = new javax.swing.JPanel();
        positionPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        positionLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        planTextArea = new javax.swing.JTextArea();
        mapTab = new javax.swing.JScrollPane();
        gridTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        text.setColumns(20);
        text.setRows(5);
        jScrollPane1.setViewportView(text);

        jTabbedPane1.addTab("MindView", jScrollPane1);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setText("Target X:");

        jLabel3.setText("Target Y:");

        targetYLabel.setName(""); // NOI18N

        jButton1.setText("Update target destination");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(aStarButton);
        aStarButton.setSelected(true);
        aStarButton.setText("A-Star");

        buttonGroup1.add(thetaStarButton);
        thetaStarButton.setText("Theta-Star");

        allowDiagonal.setText("Allow diagonal move");

        buttonGroup1.add(jumpPointButton);
        jumpPointButton.setText("Jump-point");

        buttonGroup1.add(autoButton);
        autoButton.setLabel("Auto");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(targetXLabel)
                            .addComponent(targetYLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(autoButton)
                        .addGap(7, 7, 7)
                        .addComponent(jumpPointButton)
                        .addGap(18, 18, 18)
                        .addComponent(aStarButton)
                        .addGap(18, 18, 18)
                        .addComponent(thetaStarButton))
                    .addComponent(allowDiagonal)
                    .addComponent(jButton1))
                .addContainerGap(208, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetXLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetYLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jumpPointButton)
                    .addComponent(aStarButton)
                    .addComponent(autoButton)
                    .addComponent(thetaStarButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(allowDiagonal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout actionTabLayout = new javax.swing.GroupLayout(actionTab);
        actionTab.setLayout(actionTabLayout);
        actionTabLayout.setHorizontalGroup(
            actionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actionTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        actionTabLayout.setVerticalGroup(
            actionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(94, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Destination", actionTab);

        planTab.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        positionPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setText("Position:");

        positionLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        positionLabel.setText("0,0");

        javax.swing.GroupLayout positionPanelLayout = new javax.swing.GroupLayout(positionPanel);
        positionPanel.setLayout(positionPanelLayout);
        positionPanelLayout.setHorizontalGroup(
            positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(positionPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(positionLabel))
                .addContainerGap(503, Short.MAX_VALUE))
        );
        positionPanelLayout.setVerticalGroup(
            positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(positionPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(positionLabel)
                .addGap(64, 64, 64))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Plan:");

        planTextArea.setEditable(false);
        planTextArea.setColumns(20);
        planTextArea.setRows(5);
        jScrollPane2.setViewportView(planTextArea);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout planTabLayout = new javax.swing.GroupLayout(planTab);
        planTab.setLayout(planTabLayout);
        planTabLayout.setHorizontalGroup(
            planTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(planTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(planTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(positionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        planTabLayout.setVerticalGroup(
            planTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, planTabLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(positionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        jTabbedPane1.addTab("Plan", planTab);

        mapTab.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        mapTab.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        mapTab.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        gridTable.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        gridTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        gridTable.setRowHeight(15);
        mapTab.setViewportView(gridTable);

        jTabbedPane1.addTab("Map", mapTab);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (pathPlan == null) {
            return;
        }
        int x = Integer.valueOf(targetXLabel.getText());
        int y = Integer.valueOf(targetYLabel.getText());
        Finder finder = Finder.JUMP_POINT_FINDER;
        if (aStarButton.isSelected()) {
            finder = Finder.A_STAR_FINDER;

        } else if (thetaStarButton.isSelected()) {
            finder = Finder.THETA_FINDER;
        }
        pathPlan.setTargetDestination(new WorldPoint(x, y));
        pathPlan.setFinder(finder);
        pathPlan.setAllowDiagonal(allowDiagonal.isSelected());

        if (proxy != null) {
            try {
                World.createFood(0, x, y);
            } catch (CommandExecException ex) {
                Logger.getLogger(MindView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void refreshPlan() {
        if (pathPlan == null || position == null) {
            return;
        }
        synchronized (this) {
            long now = System.currentTimeMillis();
            if (now - lastPathPlanUpdate < 1000) {
                return;
            }
            lastPathPlanUpdate = now;
            List<GridCell> plan = pathPlan.getPlan();
            Integer[] gridSize = pathPlan.getGridSize();
            final int columnCount = gridSize[0] + 1;
            final int rowCount = gridSize[1];
            final GridCell[][] grid = pathPlan.getGrid();
            final GridCell currentGridPosition = pathPlan.worldPointToGridCell(position);
            final GridCell destinationGridPosition = pathPlan.worldPointToGridCell(pathPlan.getDestination());
            final GridCell targetGridPosition = pathPlan.worldPointToGridCell(pathPlan.getTargetDestination());
            TableModel model
                    = new AbstractTableModel() {
                @Override
                public String getColumnName(int col) {
                    if (col == 0) {
                        return "";
                    }
                    return String.valueOf(col - 1);
                }

                @Override
                public int getRowCount() {
                    return rowCount;
                }

                @Override
                public int getColumnCount() {
                    return columnCount;
                }

                @Override
                public Object getValueAt(int row, int col) {
                    if (grid == null) {
                        return "";
                    }
                    if (col == 0) {
                        return row;
                    }
                    if (currentGridPosition != null && col == currentGridPosition.getX() + 1 && row == currentGridPosition.getY()) {
                        return "c";
                    }
                    if (destinationGridPosition != null && col == destinationGridPosition.getX() + 1 && row == destinationGridPosition.getY()) {
                        return "D";
                    }
                    if (targetGridPosition != null && col == targetGridPosition.getX() + 1 && row == targetGridPosition.getY()) {
                        return "T";
                    }
                    if (grid[col - 1][row].isWalkable()) {
                        return "";
                    }
                    return "x";
                }

                @Override
                public boolean isCellEditable(int row, int col) {
                    return true;
                }

                @Override
                public void setValueAt(Object value, int row, int col) {
                }
            };
            gridTable.setModel(model);
            String text = "";
            if (plan == null) {
                text = "No path found!";
            } else {
                int i = 0;
                for (GridCell cell : plan) {
                    i++;
                    text = text + i + ") goto cell [" + Util.gridCellToString(cell) + "] (" + Util.worldPointToString(pathPlan.gridCellCenter(cell)) + ")\n";
                }
            }
            planTextArea.setText(text);
        }
    }

    private void updatePositionLabel() {
        if (pathPlan == null || position == null) {
            return;
        }
        GridCell c = pathPlan.worldPointToGridCell(position);
        positionLabel.setText(Util.worldPointToString(position)
                + " [" + String.valueOf(c.getX()) + ", " + String.valueOf(c.getY()) + "] pitch "
                + String.format("%.2f", pitch));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MindView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MindView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MindView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MindView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MindView mv;
                mv = new MindView("Teste");
                mv.setVisible(true);
                mv.StartTimer();
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton aStarButton;
    private javax.swing.JPanel actionTab;
    private javax.swing.JCheckBox allowDiagonal;
    private javax.swing.JRadioButton autoButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTable gridTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton jumpPointButton;
    private javax.swing.JScrollPane mapTab;
    private javax.swing.JPanel planTab;
    private javax.swing.JTextArea planTextArea;
    private javax.swing.JLabel positionLabel;
    private javax.swing.JPanel positionPanel;
    private javax.swing.JTextField targetXLabel;
    private javax.swing.JTextField targetYLabel;
    private javax.swing.JTextArea text;
    private javax.swing.JRadioButton thetaStarButton;
    // End of variables declaration//GEN-END:variables
}
