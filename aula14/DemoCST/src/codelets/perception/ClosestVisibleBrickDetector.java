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

package codelets.perception;



import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import java.util.Collections;
import memory.CreatureInnerSense;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import support.Util;
import ws3dproxy.model.Thing;

/**
 * @author klaus
 *
 */
public class ClosestVisibleBrickDetector extends Codelet {

	private MemoryObject visionMO;
	private MemoryObject closestBrickMO;
	private MemoryObject innerSenseMO;
	
        private List<Thing> visibleThings;

	public ClosestVisibleBrickDetector() {
	}


	@Override
	public void accessMemoryObjects() {
		this.visionMO=(MemoryObject)this.getInput("VISION");
		this.innerSenseMO=(MemoryObject)this.getInput("INNER");
		this.closestBrickMO=(MemoryObject)this.getOutput("CLOSEST_BRICK");	
	}
	@Override
	public void proc() {
                Thing closest_brick=null;
                visibleThings = Collections.synchronizedList((List<Thing>) visionMO.getI());
                CreatureInnerSense cis = (CreatureInnerSense) innerSenseMO.getI();
                synchronized(visibleThings) {
		   if(visibleThings.size() != 0){
			//Iterate over objects in vision, looking for the closest brick
                        CopyOnWriteArrayList<Thing> myVisibleThings = new CopyOnWriteArrayList<>(visibleThings);
                        for (Thing t : myVisibleThings) {
				String objectName=t.getName();
				if(objectName.contains("Brick")){ //Then, it is an brick
                                        if(closest_brick == null){    
                                                closest_brick = t;
					}
                                        else {
						double Dnew = Util.calculateDistance(t.getX1(), t.getY1(), cis.position.getX(), cis.position.getY());
                                                double Dclosest= Util.calculateDistance(closest_brick.getX1(), closest_brick.getY1(), cis.position.getX(), cis.position.getY());
						if(Dnew<Dclosest){
                                                        closest_brick = t;
						}
					}
				}
			}
                        
                        if(closest_brick!=null){    
				if(closestBrickMO.getI() == null || !closestBrickMO.getI().equals(closest_brick)){
                                      closestBrickMO.setI(closest_brick);
				}
				
			}else{
				//couldn't find any nearby bricks
                                closest_brick = null;
                                closestBrickMO.setI(closest_brick);
			}
		   }
                   else  { // if there are no known bricks closest_brick must be null
                        closest_brick = null;
                        closestBrickMO.setI(closest_brick);
		   }
                }
	}//end proc

@Override
        public void calculateActivation() {
        
        }        
}
