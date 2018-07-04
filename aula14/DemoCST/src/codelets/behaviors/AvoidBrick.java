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
import support.Util;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

public class AvoidBrick extends Codelet {

	private MemoryObject closestBrickMO;
	private MemoryObject selfInfoMO;
	private MemoryContainer legsMO;
        private int legsMOIndex = -1;
	private int creatureBasicSpeed;
	private double reachDistance;

	public AvoidBrick(int creatureBasicSpeed, int reachDistance) {
		this.creatureBasicSpeed=creatureBasicSpeed;
		this.reachDistance=reachDistance;
	}

	@Override
	public void accessMemoryObjects() {
		closestBrickMO=(MemoryObject)this.getInput("CLOSEST_BRICK");
		selfInfoMO=(MemoryObject)this.getInput("INNER");
		legsMO=(MemoryContainer)this.getOutput("LEGS");
	}

	@Override
	public void proc() {
		// Find distance between creature and closest brick
		//If far, go towards it
		//If close, stops

                Thing closestBrick = (Thing) closestBrickMO.getI();
                CreatureInnerSense cis = (CreatureInnerSense) selfInfoMO.getI();
                
                WorldPoint position;
                synchronized(cis) {
                    position = cis.position;
                }                    

		if(closestBrick != null)
		{
			JSONObject message=new JSONObject();
			try {
				if (Util.distanceToThingLessThan(closestBrick, position, reachDistance)) {
                                        double angle = -1;
                                        angle = Util.oppositeDirectionTo(cis.position, cis.pitch, closestBrick);
                                        //creature.rotate(angle);
                                        //CommandUtility.sendSetAngle(creature.getIndex(), angle, -angle, angle);
                                    
                                        message.put("ACTION", "ROTATE");
                                        message.put("SPEED", creatureBasicSpeed);	
                                        message.put("ANGLE", angle);	
                                        message.put("BEHAVIOR", this.getClass().getSimpleName());
                                        if (legsMOIndex < 0) {
                                            legsMOIndex = legsMO.setI(message.toString(), EvaluationConstants.LEGS_AVOID_BRICK_EVALUATION);
                                        } else {                                    
                                            legsMO.setI(message.toString(), EvaluationConstants.LEGS_AVOID_BRICK_EVALUATION, legsMOIndex);
                                        }
                                        return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}
                if (legsMOIndex >= 0) {
                    legsMO.setI("", 0.0, legsMOIndex);
                }
	}//end proc
        
        @Override
        public void calculateActivation() {
        
        }

}
