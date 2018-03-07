/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * mainScreen Controller class
 *
 */
public class MainScreenController {

    @FXML
    public Button buttonBag;
    
    /**
     * Initializes the controller class.
     */
    public void initialize() {
        buttonBag.setOnAction(e -> System.out.println("It works"));
    }    
    
}
