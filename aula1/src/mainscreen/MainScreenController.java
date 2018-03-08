/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainscreen;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.WorldFacade;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;
import ws3dproxy.model.SelfAttributes;

/**
 * mainScreen Controller class
 *
 */
public class MainScreenController {

    @FXML
    public Button buttonLeft;

    @FXML
    public Button buttonRight;

    @FXML
    public Button buttonUp;
    
    @FXML
    public Button buttonDown;
    
    @FXML
    public Button buttonMind;
    
    @FXML
    public Button buttonCreateCreature;
    
    @FXML
    public Label fuelLabel;    

    @FXML
    public Label speedLabel;

    @FXML
    public Label pitchLabel;

    @FXML
    public Label serotonineLabel;

    @FXML
    public Label endorphineLabel;

    @FXML
    public Label scoreLabel;

    @FXML
    public Label positionLabel;

    @FXML
    public Label leafletCountLabel;

    private WorldFacade worldFacade;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        WorldFacade w = getWorldFacade();
        buttonMind.setOnAction(e -> {
            try {
                w.showMindWindow();
            } catch (CommandExecException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        buttonCreateCreature.setOnAction(e -> createCreature());
        buttonLeft.setOnAction(e -> {
            try {
                w.moveCreatureLeft();
            } catch (CommandExecException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        buttonRight.setOnAction(e -> {
            try {
                w.moveCreatureRight();
            } catch (CommandExecException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        buttonUp.setOnAction(e -> {
            try {
                w.moveCreatureForward();
            } catch (CommandExecException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        buttonDown.setOnAction(e -> {
            try {
                w.moveCreatureBackwards();
            } catch (CommandExecException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public void createCreature() {
        buttonCreateCreature.setDisable(true);
        buttonCreateCreature.setText("Creating...");
 
        Task<Void> createCreatureTask = new Task<Void>() {
            
            @Override
            public Void call() throws Exception {
                worldFacade.getCreature();
                return null;
            }
            
            @Override
            public void succeeded() {
                buttonCreateCreature.setText("Creature Created");
                buttonLeft.setDisable(false);
                buttonRight.setDisable(false);
                buttonUp.setDisable(false);
                buttonDown.setDisable(false);
                buttonMind.setDisable(false);
                startUpdateAttributesTimer();
            }
            
        };
        new Thread(createCreatureTask).start();
    }
    
    private WorldFacade getWorldFacade() {
        if (worldFacade == null)
            worldFacade = new WorldFacade();
        return worldFacade;
    }

    private void startUpdateAttributesTimer() {

        class UpdateTimer {
            Timer timer;
            
            static final int SECONDS = 1;

            public UpdateTimer() {
                timer = new Timer();
                timer.schedule(new IntervalTask(), SECONDS * 1000);
            }

            class IntervalTask extends TimerTask {
                
                @Override
                public void run() {
                    timer.cancel(); //Terminate the timer thread
                    Task<Void> updateAttributesTask = new Task<Void>() {
                        Creature creature;
                        SelfAttributes attributes;

                        @Override
                        public Void call() throws Exception {
                            creature = getWorldFacade().getCreature();
                            attributes = creature.getAttributes();
                            return null;
                        }

                        @Override
                        public void succeeded() {
                            fuelLabel.setText(String.format("%.1f", attributes.getFuel()));
                            speedLabel.setText(String.format("%.1f", attributes.getSpeed()));
                            pitchLabel.setText(String.format("%.1f", attributes.getPitch()));
                            serotonineLabel.setText(String.format("%.1f", attributes.getSerotonin()));
                            endorphineLabel.setText(String.format("%.1f", attributes.getEndorphine()));
                            scoreLabel.setText(String.format("%.1f", attributes.getScore()));
                            positionLabel.setText(String.format("%.1f, %.1f", creature.getPosition().getX(), creature.getPosition().getY()));
                            leafletCountLabel.setText(String.format("%d", creature.getLeaflets().size()));
                        }

                    };
                    new Thread(updateAttributesTask).start();
                    timer = new Timer();
                    timer.schedule(new IntervalTask(), SECONDS * 1000);
                }
            }
                
        }
        
        UpdateTimer updateTimer = new UpdateTimer();
        System.out.println("Update timer started");
    }
    
}
