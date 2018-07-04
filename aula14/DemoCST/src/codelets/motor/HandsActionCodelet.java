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

package codelets.motor;


import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Leaflet;

/**
 *  Hands Action Codelet monitors working storage for instructions and acts on the World accordingly.
 *  
 * @author klaus
 *
 */


public class HandsActionCodelet extends Codelet{

	private MemoryContainer handsMO;
	private String previousHandsAction="";
        private Creature c;
        private int handsMOIndex;
        private Random r = new Random();
        static Logger log = Logger.getLogger(HandsActionCodelet.class.getCanonicalName());

	public HandsActionCodelet(Creature nc) {
                c = nc;
                log.setLevel(Level.INFO);
	}
	
        @Override
	public void accessMemoryObjects() {
		handsMO=(MemoryContainer)this.getInput("HANDS");
	}
	public void proc() {
            
                String command = (String) handsMO.getI();

		if(command != null && !command.equals("") && (!command.equals(previousHandsAction))){
			JSONObject jsonAction;
			try {
				jsonAction = new JSONObject(command);
				if(jsonAction.has("ACTION") && jsonAction.has("OBJECT")){
					String action=jsonAction.getString("ACTION");
					String objectName=jsonAction.getString("OBJECT");
					if(action.equals("PICKUP")){
                                                try {
                                                 c.putInSack(objectName);
                                                } catch (Exception e) {
                                                    
                                                } 
						log.info("Sending Put In Sack command to agent:****** "+objectName+"**********");							
						
						
						//							}
					}
					if(action.equals("EATIT")){
                                                try {
                                                 c.eatIt(objectName);
                                                } catch (Exception e) {
                                                    
                                                }
						log.info("Sending Eat command to agent:****** "+objectName+"**********");							
					}
					if(action.equals("BURY")){
                                                try {
                                                 c.hideIt(objectName);
                                                 //CommandUtility.sendHideIt(c.getIndex(), objectName);
						log.info("Sending Bury command to agent: " + objectName);
                                                } catch (Exception e) {
                                                    log.severe("Error on hideIt command: "+e.getMessage());
                                                }
					}
					if(action.equals("DELIVER")){
                                                try {
                                                    for (Leaflet leaflet: c.getLeaflets()) {
                                                        final String leafletID = String.valueOf(leaflet.getID());
                                                        c.deliverLeaflet(leafletID);
        						log.info("Sending DELIVER command to agent:****** "+leafletID+"**********");
                                                    }
                                                    JOptionPane.showMessageDialog(null, "Creature: " + c.getName() + " - All leaflets complete!");
                                                } catch (Exception e) {
                                                    
                                                }
					}
//                                        if (handsMOIndex < 0) {
//                                            handsMOIndex = handsMO.setI("", 0.1);
//                                        } else {                                    
//                                            handsMO.setI("", 0.1, handsMOIndex);
//                                        }
				}
//                                else if (jsonAction.has("ACTION")) {
//                                    int x=0,y=0;
//                                    String action=jsonAction.getString("ACTION");
//                                    if(action.equals("FORAGE")){
//                                                try {
//                                                      x = r.nextInt(600);
//                                                      y = r.nextInt(800);
//                                                 c.moveto(1, x,y );
//                                                } catch (Exception e) {
//                                                    
//                                                }
//						System.out.println("Sending Forage command to agent:****** ("+x+","+y+") **********");							
//					}
//                                }
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
//		System.out.println("OK_hands");
		previousHandsAction = command;
	}//end proc

    @Override
    public void calculateActivation() {
        
    }


}
