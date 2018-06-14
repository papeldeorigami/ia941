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
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;

/**
 * @author klaus
 *
 */
public class ClosestLeafletJewelDetector extends Codelet {

	private MemoryObject knownMO;
	private MemoryObject closestLeafletJewelMO;
	private MemoryObject innerSenseMO;
	
        private List<Thing> known;

	public ClosestLeafletJewelDetector() {
	}


	@Override
	public void accessMemoryObjects() {
		this.knownMO=(MemoryObject)this.getInput("KNOWN_JEWELS");
		this.innerSenseMO=(MemoryObject)this.getInput("INNER");
		this.closestLeafletJewelMO=(MemoryObject)this.getOutput("CLOSEST_LEAFLET_JEWEL");	
	}
	@Override
	public void proc() {
                Thing closest_jewel=null;
                known = Collections.synchronizedList((List<Thing>) knownMO.getI());
                CreatureInnerSense cis = (CreatureInnerSense) innerSenseMO.getI();
                synchronized(known) {
		   if(known.size() != 0){
			//Iterate over objects in vision, looking for the closest apple
                        CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);
                        for (Thing t : myknown) {
				String objectName=t.getName();
				if(objectName.contains("JEWEL") && jewelIsNecessaryForSomeLeaflet((Thing) t, cis)) { //Then, it is a jewel and the creature needs it to fill some leaflet
                                        if(closest_jewel == null){    
                                                closest_jewel = t;
					}
                                        else {
						double Dnew = calculateDistance(t.getX1(), t.getY1(), cis.position.getX(), cis.position.getY());
                                                double Dclosest= calculateDistance(closest_jewel.getX1(), closest_jewel.getY1(), cis.position.getX(), cis.position.getY());
						if(Dnew<Dclosest){
                                                        closest_jewel = t;
						}
					}
				}
			}
                        
                        if(closest_jewel!=null){    
				if(closestLeafletJewelMO.getI() == null || !closestLeafletJewelMO.getI().equals(closest_jewel)){
                                      closestLeafletJewelMO.setI(closest_jewel);
				}
				
			}else{
				//couldn't find any nearby apples
                                closest_jewel = null;
                                closestLeafletJewelMO.setI(closest_jewel);
			}
		   }
                   else  { // if there are no known apples closest_apple must be null
                        closest_jewel = null;
                        closestLeafletJewelMO.setI(closest_jewel);
		   }
                }
	}//end proc

@Override
        public void calculateActivation() {
        
        }
        
        private double calculateDistance(double x1, double y1, double x2, double y2) {
            return(Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
        }

    private boolean jewelIsNecessaryForSomeLeaflet(Thing t, CreatureInnerSense cis) {
        List<Leaflet> leaflets = cis.leaflets;
        for (Leaflet leaflet: leaflets) {
            if (leaflet.getMissingNumberOfType(t.getAttributes().getColor()) > 0)
                return true;
        }
        return false;
    }

}
