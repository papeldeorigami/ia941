/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import java.util.logging.Logger;
import ws3dproxy.model.WorldPoint;

/**
 *
 * @author Ricardo Andrade <papeldeorigami@googlemail.com>
 */
public class MazePanel extends javax.swing.JFrame {

    private static final Logger logger = Logger
            .getLogger(MazePanel.class.getCanonicalName());

    long lastUpdate;

    /**
     * Creates new form CurrentActionPanel
     */
    public MazePanel() {
        initComponents();
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
        actionTab = new javax.swing.JPanel();
        positionPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        currentActionLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        positionLabel = new javax.swing.JLabel();
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
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        planTextArea = new javax.swing.JTextArea();
        mapTab = new javax.swing.JScrollPane();
        gridTable = new javax.swing.JTable();

        positionPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("Current action:");

        currentActionLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        currentActionLabel.setText("None");

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
                    .addComponent(jLabel1)
                    .addComponent(currentActionLabel))
                .addGap(46, 46, 46)
                .addGroup(positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(positionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(positionPanelLayout.createSequentialGroup()
                        .addComponent(positionLabel)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        positionPanelLayout.setVerticalGroup(
            positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(positionPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(positionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentActionLabel)
                    .addComponent(positionLabel))
                .addGap(64, 64, 64))
        );

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
                .addGroup(actionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(positionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        actionTabLayout.setVerticalGroup(
            actionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actionTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(positionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Action", actionTab);

        planTab.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Plan:");

        planTextArea.setEditable(false);
        planTextArea.setColumns(20);
        planTextArea.setRows(5);
        jScrollPane1.setViewportView(planTextArea);

        javax.swing.GroupLayout planTabLayout = new javax.swing.GroupLayout(planTab);
        planTab.setLayout(planTabLayout);
        planTabLayout.setHorizontalGroup(
            planTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(planTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(planTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(planTabLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE))
                .addContainerGap())
        );
        planTabLayout.setVerticalGroup(
            planTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(planTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addContainerGap())
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int x = Integer.valueOf(targetXLabel.getText());
        int y = Integer.valueOf(targetYLabel.getText());
//        Finder finder = Finder.JUMP_POINT_FINDER;
//        if (aStarButton.isSelected()) {
//            finder = Finder.A_STAR_FINDER;
//        
//        } else if (thetaStarButton.isSelected()) {
//            finder = Finder.THETA_FINDER;
//        }
//        environmentModule.setTargetDestination(x, y, thetaStarButton.isSelected(), allowDiagonal.isSelected(), finder);
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton aStarButton;
    private javax.swing.JPanel actionTab;
    private javax.swing.JCheckBox allowDiagonal;
    private javax.swing.JRadioButton autoButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel currentActionLabel;
    private javax.swing.JTable gridTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton jumpPointButton;
    private javax.swing.JScrollPane mapTab;
    private javax.swing.JPanel planTab;
    private javax.swing.JTextArea planTextArea;
    private javax.swing.JLabel positionLabel;
    private javax.swing.JPanel positionPanel;
    private javax.swing.JTextField targetXLabel;
    private javax.swing.JTextField targetYLabel;
    private javax.swing.JRadioButton thetaStarButton;
    // End of variables declaration//GEN-END:variables

    public void initPanel(String[] param) {
//        module = agent.getSubmodule(ModuleName.ActionSelection);
//        environmentModule = (Environment) agent.getSubmodule(ModuleName.Environment);
//        
//        if (module == null) {
//            logger
//                    .log(Level.WARNING,
//                            "Error initializing panel, Module does not exist in agent.");
//        } else {
//            module.addListener(this);
//        }
//
//        selectedActionsSize = DEFAULT_SELECTED_ACTIONS_SIZE;
//
//        if (param.length > 0) {
//            try {
//                selectedActionsSize = Integer.parseInt(param[0]);
//            } catch (NumberFormatException e) {
//                logger.log(Level.WARNING,
//                        "parse error, using default selectActionsSize");
//            }
//        } else {
//            logger.log(Level.INFO, "using default selectActionsSize");
//        }
//        
//        currentActionLabel.setText("");
//        targetXLabel.setText(String.valueOf(Environment.INITIAL_DESTINATION_X));
//        targetYLabel.setText(String.valueOf(Environment.INITIAL_DESTINATION_Y));
//        updatePositionLabel();
    }

    public void refresh() {
        display(null);
    }

//    private void refreshPlan() {
//        synchronized(this) {
//            long now = System.currentTimeMillis();
//            if (now - lastUpdate < 1000) {
//                return;
//            }
//            lastUpdate = now;
//            List<GridCell> plan = environmentModule.getPlan();
//            Integer[] gridSize = environmentModule.getGridSize();
//            final int columnCount = gridSize[0] + 1;
//            final int rowCount = gridSize[1];
//            final GridCell[][] grid = environmentModule.getGrid();
//            final GridCell currentGridPosition = environmentModule.getGridPosition();
//            TableModel model = 
//                new AbstractTableModel() {
//                    @Override
//                    public String getColumnName(int col) {
//                        if (col == 0) {
//                            return "";
//                        }
//                        return String.valueOf(col - 1);
//                    }
//                    @Override
//                    public int getRowCount() { return rowCount; }
//                    @Override
//                    public int getColumnCount() { return columnCount; }
//                    @Override
//                    public Object getValueAt(int row, int col) {
//                        if (grid == null) {
//                            return "";
//                        }
//                        if (col == 0) {
//                            return row;
//                        }
//                        if (col == currentGridPosition.getX() + 1 && row == currentGridPosition.getY()) {
//                            return "o";
//                        }
//                        if (grid[col - 1][row].isWalkable()) {
//                            return "";
//                        }
//                        return "x";
//                    }
//                    @Override
//                    public boolean isCellEditable(int row, int col)
//                        { return true; }
//                    @Override
//                    public void setValueAt(Object value, int row, int col) {
//                    }
//                };
//            gridTable.setModel(model);
//            String text = "";
//            if (plan == null) {
//                text = "No path found!";
//            } else {
//                int i = 0;
//                for (GridCell cell: plan) {
//                    i++;
//                    final int x = cell.getX();
//                    final int y = cell.getY();
//                    text = text + i + ") goto cell [" + x + "," + y + "]\n";
//                }
//            }
//            planTextArea.setText(text);
//        }
//    }
    
    public void display(Object o) {
////        refreshPlan();
    }

//    private void updatePositionLabel() {
//        WorldPoint p = environmentModule.getPosition();
//        GridCell c = environmentModule.worldPointToGridCell(p);
//        positionLabel.setText(worldPointToString(p)
//        + " [" + String.valueOf(c.getX()) + ", " + String.valueOf(c.getY()) + "] pitch "
//        + String.format("%.2f", environmentModule.getPitch()));
//    }

    private String worldPointToString(WorldPoint p) {
        if (p == null) {
            return "";            
        }
        return String.valueOf(Math.round(p.getX())) + ", " + String.valueOf(Math.round(p.getY()));
    }
    /**
     *
     * @param action
     */
//    public void receiveAction(Action action) {
//        ActionDetail detail = new ActionDetail(TaskManager.getCurrentTick(),
//                currentSelectionCount++, action);
//        synchronized (this) {
//            selectedActions.addFirst(detail);
//            if (selectedActions.size() > selectedActionsSize) {
//                selectedActions.pollLast();
//            }
//            if (selectedActions.size() > 0) {
//                String actionText = selectedActions.getFirst().getAction().getLabel();
//                if (actionText.equals("action.moveToDestination")) {
//                    actionText = actionText + " (" + worldPointToString(environmentModule.getDestination()) + ")";
//                }
//                currentActionLabel.setText(actionText);
//            } else {
//                currentActionLabel.setText("None");
//            }
//            updatePositionLabel();
//        }
//    }
}
