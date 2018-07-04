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

package codelets.behaviors;

import java.awt.Point;
import java.awt.geom.Point2D;

import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import memory.CreatureInnerSense;
import java.util.List;
import ws3dproxy.model.Thing;

public class BuryUnnecessaryJewel extends Codelet {

	private MemoryObject closestJewelToBuryMO;
	private MemoryObject innerSenseMO;
        private MemoryObject knownMO;
	private int reachDistance;
        private int handsMOIndex = -1;
	private MemoryContainer handsMO;
        Thing closestJewelToBury;
        CreatureInnerSense cis;
        List<Thing> known;

	public BuryUnnecessaryJewel(int reachDistance) {
                setTimeStep(50);
		this.reachDistance=reachDistance;
	}

	@Override
	public void accessMemoryObjects() {
		closestJewelToBuryMO=(MemoryObject)this.getInput("CLOSEST_JEWEL_TO_BURY");
		innerSenseMO=(MemoryObject)this.getInput("INNER");
		handsMO=(MemoryContainer)this.getOutput("HANDS");
                knownMO = (MemoryObject)this.getOutput("KNOWN_JEWELS");
	}

	@Override
	public void proc() {
                String jewelName="";
                closestJewelToBury = (Thing) closestJewelToBuryMO.getI();
                cis = (CreatureInnerSense) innerSenseMO.getI();
                known = (List<Thing>) knownMO.getI();
		//Find distance between closest jewel and self
		//If closer than reachDistance, get the jewel
		
                String info = "";
		if(closestJewelToBury != null)
		{
			double jewelX=0;
			double jewelY=0;
			try {
				jewelX=closestJewelToBury.getX1();
				jewelY=closestJewelToBury.getY1();
                                jewelName = closestJewelToBury.getName();
                                

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			double selfX=cis.position.getX();
			double selfY=cis.position.getY();

			Point2D pJewel = new Point();
			pJewel.setLocation(jewelX, jewelY);

			Point2D pSelf = new Point();
			pSelf.setLocation(selfX, selfY);

			double distance = pSelf.distance(pJewel);
			JSONObject message=new JSONObject();
			try {
				if(distance<reachDistance){ //pick it up
					message.put("OBJECT", jewelName);
					message.put("ACTION", "BURY");
                                        message.put("BEHAVIOR", this.getClass().getSimpleName());
                                        info = message.toString();
                                        DestroyClosestJewel();
                                        if (handsMOIndex < 0) {
                                            handsMOIndex = handsMO.setI(info, EvaluationConstants.HANDS_BURY_JEWEL_EVALUATION);
                                        } else {                                    
                                            handsMO.setI(info, EvaluationConstants.HANDS_BURY_JEWEL_EVALUATION, handsMOIndex);
                                        }
                                        return;
				}
				
//				System.out.println(message);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        //System.out.println("Before: "+known.size()+ " "+known);
        
        //System.out.println("After: "+known.size()+ " "+known);
	//System.out.println("GetClosestJewel: "+ handsMO.getInfo());	
            if (handsMOIndex >= 0) {
                handsMO.setI("", 0.0, handsMOIndex);
            }
	}
        
        @Override
        public void calculateActivation() {
        
        }
        
        public void DestroyClosestJewel() {
           int r = -1;
           int i = 0;
           synchronized(known) {
             for (Thing t : known) {
              if (closestJewelToBury != null) 
                 if (t.getName().equals(closestJewelToBury.getName())) r = i;
              i++;
             }   
             if (r != -1) known.remove(r);
             closestJewelToBury = null;
           }
        }

}
