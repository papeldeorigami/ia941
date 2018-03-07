/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.WorldFacade;

/**
 * mainScreen Controller class
 *
 */
public class MainScreenController {

    @FXML
    public Button buttonBag;
    
    @FXML
    public Button buttonCreateCreature;
    
    private WorldFacade worldFacade;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        WorldFacade w = getWorldFacade();
        buttonBag.setOnAction(e -> w.showMindWindow());
        buttonCreateCreature.setOnAction(e -> createCreature());
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
