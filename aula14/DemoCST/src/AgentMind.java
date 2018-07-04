/*****************************************************************************
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
 *****************************************************************************/

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import codelets.behaviors.BuryUnnecessaryJewel;
import codelets.behaviors.Deliver;
import codelets.behaviors.EatClosestApple;
import codelets.behaviors.Forage;
import codelets.behaviors.GetClosestLeafletJewel;
import codelets.behaviors.GoToClosestApple;
import codelets.behaviors.GoToClosestLeafletJewel;
import codelets.motor.HandsActionCodelet;
import codelets.motor.LegsActionCodelet;
import codelets.perception.AppleDetector;
import codelets.perception.ClosestAppleDetector;
import codelets.perception.ClosestJewelToBuryDetector;
import codelets.perception.ClosestLeafletJewelDetector;
import codelets.perception.JewelDetector;
import codelets.sensors.InnerSense;
import codelets.sensors.Vision;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import memory.CreatureInnerSense;
import support.MindView;
import ws3dproxy.model.Thing;

/**
 *
 * @author rgudwin
 */
public class AgentMind extends Mind {
    
    private static int creatureBasicSpeed=3;
    private static int reachDistance=40;
    
    public AgentMind(Environment env) {
                super();
                
                // Declare Memory Objects
	        MemoryContainer legsMO;
	        MemoryContainer handsMO;
                MemoryObject visionMO;
                MemoryObject innerSenseMO;
                MemoryObject closestAppleMO;
                MemoryObject knownApplesMO;
                MemoryObject closestLeafletJewelMO;
                MemoryObject closestJewelToBuryMO;
                MemoryObject knownJewelsMO;
                
                //Initialize Memory Objects
                legsMO=createMemoryContainer("LEGS");
		handsMO=createMemoryContainer("HANDS");
                List<Thing> vision_list = Collections.synchronizedList(new ArrayList<Thing>());
		visionMO=createMemoryObject("VISION",vision_list);
                CreatureInnerSense cis = new CreatureInnerSense();
		innerSenseMO=createMemoryObject("INNER", cis);
                Thing closestApple = null;
                closestAppleMO=createMemoryObject("CLOSEST_APPLE", closestApple);
                List<Thing> knownApples = Collections.synchronizedList(new ArrayList<Thing>());
                knownApplesMO=createMemoryObject("KNOWN_APPLES", knownApples);

                Thing closestLeafletJewel = null;
                closestLeafletJewelMO=createMemoryObject("CLOSEST_LEAFLET_JEWEL", closestLeafletJewel);
                Thing closestJewelToBury = null;
                closestJewelToBuryMO=createMemoryObject("CLOSEST_JEWEL_TO_BURY", closestJewelToBury);

                List<Thing> knownJewels = Collections.synchronizedList(new ArrayList<Thing>());
                knownJewelsMO=createMemoryObject("KNOWN_JEWELS", knownJewels);
                
                // Create and Populate MindViewer
                MindView mv = new MindView("MindView");

                mv.addMO(knownJewelsMO);
                mv.addMO(closestLeafletJewelMO);
                mv.addMO(closestJewelToBuryMO);

                mv.addMO(knownApplesMO);
                mv.addMO(visionMO);
                mv.addMO(closestAppleMO);
                mv.addMO(innerSenseMO);
                mv.addMO(handsMO);
                mv.addMO(legsMO);
                mv.StartTimer();
                mv.setVisible(true);
                
		// Create Sensor Codelets	
		Codelet vision=new Vision(env.c);
		vision.addOutput(visionMO);
                insertCodelet(vision); //Creates a vision sensor
		
		Codelet innerSense=new InnerSense(env.c);
		innerSense.addOutput(innerSenseMO);
                insertCodelet(innerSense); //A sensor for the inner state of the creature
		
		// Create Actuator Codelets
		Codelet legs=new LegsActionCodelet(env.c);
		legs.addInput(legsMO);
                insertCodelet(legs);

		Codelet hands=new HandsActionCodelet(env.c);
		hands.addInput(handsMO);
                insertCodelet(hands);
		
		// Create Perception Codelets
                Codelet ad = new AppleDetector();
                ad.addInput(visionMO);
                ad.addOutput(knownApplesMO);
                insertCodelet(ad);
                
		Codelet closestAppleDetector = new ClosestAppleDetector();
		closestAppleDetector.addInput(knownApplesMO);
		closestAppleDetector.addInput(innerSenseMO);
		closestAppleDetector.addOutput(closestAppleMO);
                insertCodelet(closestAppleDetector);

                Codelet jewelDetector = new JewelDetector();
                jewelDetector.addInput(visionMO);
                jewelDetector.addOutput(knownJewelsMO);
                insertCodelet(jewelDetector);
                
		Codelet closestLeafletJewelDetector = new ClosestLeafletJewelDetector();
		closestLeafletJewelDetector.addInput(knownJewelsMO);
		closestLeafletJewelDetector.addInput(innerSenseMO);
		closestLeafletJewelDetector.addOutput(closestLeafletJewelMO);
                insertCodelet(closestLeafletJewelDetector);
                
		Codelet closestJewelToBuryDetector = new ClosestJewelToBuryDetector();
		closestJewelToBuryDetector.addInput(knownJewelsMO);
		closestJewelToBuryDetector.addInput(innerSenseMO);
		closestJewelToBuryDetector.addOutput(closestJewelToBuryMO);
                insertCodelet(closestJewelToBuryDetector);

                // Create Behavior Codelets
		Codelet goToClosestApple = new GoToClosestApple(creatureBasicSpeed,reachDistance);
		goToClosestApple.addInput(closestAppleMO);
		goToClosestApple.addInput(innerSenseMO);
		goToClosestApple.addOutput(legsMO);
                insertCodelet(goToClosestApple);
		
		Codelet eatApple=new EatClosestApple(reachDistance);
		eatApple.addInput(closestAppleMO);
		eatApple.addInput(innerSenseMO);
		eatApple.addOutput(handsMO);
                eatApple.addOutput(knownApplesMO);
                insertCodelet(eatApple);
                
		Codelet goToClosestLeafletJewel = new GoToClosestLeafletJewel(creatureBasicSpeed,reachDistance);
		goToClosestLeafletJewel.addInput(closestLeafletJewelMO);
		goToClosestLeafletJewel.addInput(innerSenseMO);
		goToClosestLeafletJewel.addOutput(legsMO);
                insertCodelet(goToClosestLeafletJewel);
		
		Codelet getClosestLeafletJewel=new GetClosestLeafletJewel(reachDistance);
		getClosestLeafletJewel.addInput(closestLeafletJewelMO);
		getClosestLeafletJewel.addInput(innerSenseMO);
		getClosestLeafletJewel.addOutput(handsMO);
                getClosestLeafletJewel.addOutput(knownJewelsMO);
                insertCodelet(getClosestLeafletJewel);

                Codelet bury=new BuryUnnecessaryJewel(reachDistance);
		bury.addInput(closestJewelToBuryMO);
		bury.addInput(innerSenseMO);
		bury.addOutput(handsMO);
                bury.addOutput(knownJewelsMO);
                insertCodelet(bury);

                Codelet forage=new Forage();
		forage.addInput(knownApplesMO);
                forage.addOutput(legsMO);
                insertCodelet(forage);
                
                Codelet deliver=new Deliver();
		deliver.addInput(innerSenseMO);
                deliver.addOutput(handsMO);
                deliver.addOutput(legsMO);
                insertCodelet(deliver);
                
                // sets a time step for running the codelets to avoid heating too much your machine
                for (Codelet c : this.getCodeRack().getAllCodelets())
                    c.setTimeStep(200);
		
		// Start Cognitive Cycle
		start(); 
    }             
    
}
