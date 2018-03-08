/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainscreen;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.WorldFacade;
import ws3dproxy.CommandExecException;

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
            }
            
        };
        new Thread(createCreatureTask).start();
    }
    
    private WorldFacade getWorldFacade() {
        if (worldFacade == null)
            worldFacade = new WorldFacade();
        return worldFacade;
    }
    
}
