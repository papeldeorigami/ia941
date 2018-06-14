/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.panels;

import edu.memphis.ccrg.lida.actionselection.Action;
import edu.memphis.ccrg.lida.actionselection.ActionSelectionListener;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.gui.panels.GuiPanelImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.Environment;

/**
 *
 * @author Ricardo Andrade <papeldeorigami@googlemail.com>
 */
public class CurrentActionPanel extends GuiPanelImpl implements
        ActionSelectionListener {

    private static final Logger logger = Logger
            .getLogger(CurrentActionPanel.class.getCanonicalName());
    private static final int DEFAULT_SELECTED_ACTIONS_SIZE = 10;

    private FrameworkModule module;

    private final LinkedList<ActionDetail> selectedActions = new LinkedList<ActionDetail>();
    private int selectedActionsSize;

    /**
     * Creates new form CurrentActionPanel
     */
    public CurrentActionPanel() {
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

        jLabel1 = new javax.swing.JLabel();
        currentActionLabel = new javax.swing.JLabel();

        jLabel1.setText("Current action:");

        currentActionLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        currentActionLabel.setText("None");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(126, 126, 126)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentActionLabel)
                    .addComponent(jLabel1))
                .addContainerGap(167, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentActionLabel)
                .addContainerGap(170, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel currentActionLabel;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void initPanel(String[] param) {
        module = agent.getSubmodule(ModuleName.ActionSelection);
        if (module == null) {
            logger
                    .log(Level.WARNING,
                            "Error initializing panel, Module does not exist in agent.");
        } else {
            module.addListener(this);
        }

        selectedActionsSize = DEFAULT_SELECTED_ACTIONS_SIZE;

        if (param.length > 0) {
            try {
                selectedActionsSize = Integer.parseInt(param[0]);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING,
                        "parse error, using default selectActionsSize");
            }
        } else {
            logger.log(Level.INFO, "using default selectActionsSize");
        }
        
        currentActionLabel.setText("actions." + Environment.INITIAL_ACTION);
    }

    @Override
    public void refresh() {
        display(module.getModuleContent("behaviors"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void display(Object o) {
    }

    private class ActionDetail {

        private final long tick;
        private final int selectedActionCount;
        private final Action action;

        public ActionDetail(long t, int count, Action a) {
            tick = t;
            selectedActionCount = count;
            action = a;
        }

        public long getTickAtSelection() {
            return tick;
        }

        public int getSelectedActionCount() {
            return selectedActionCount;
        }

        public Action getAction() {
            return action;
        }
    }

    private int currentSelectionCount;

    /**
     *
     * @param action
     */
    @Override
    public void receiveAction(Action action) {
        ActionDetail detail = new ActionDetail(TaskManager.getCurrentTick(),
                currentSelectionCount++, action);
        synchronized (this) {
            selectedActions.addFirst(detail);
            if (selectedActions.size() > selectedActionsSize) {
                selectedActions.pollLast();
            }
            if (selectedActions.size() > 0) {
                currentActionLabel.setText(selectedActions.getFirst().getAction().getLabel());
            } else {
                currentActionLabel.setText("None");
            }
        }
    }
}
