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
import java.util.List;
import memory.CreatureInnerSense;
import support.PathPlan;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

/**
 * Detect bricks in the vision field.
 * 	This class saves every new brick at sight (in visionMo) to the knownBricksMO
 * 
 * @author klaus
 *
 */
public class DestinationDetector extends Codelet {

	private MemoryObject innerSenseMO;
        private MemoryObject pathPlanMO;
        private MemoryObject knownBricksMO;

	public DestinationDetector(){
            setTimeStep(50);		
	}

	@Override
	public void accessMemoryObjects() {
            this.innerSenseMO=(MemoryObject)this.getInput("INNER");
            this.pathPlanMO=(MemoryObject)this.getInput("PATH_PLAN");
            this.knownBricksMO=(MemoryObject)this.getInput("KNOWN_BRICKS");
	}

	@Override
	public void proc() {
            PathPlan pathPlan = (PathPlan) pathPlanMO.getI();
            List<Thing> obstacles = Collections.synchronizedList((List<Thing>) knownBricksMO.getI());
            CreatureInnerSense cis = (CreatureInnerSense) innerSenseMO.getI();
            
            WorldPoint position;
            synchronized(cis) {
                position = cis.position;
            }
            synchronized(pathPlan) {
                synchronized(obstacles) {
                    pathPlan.planNextStep(position, obstacles);
                }
            }
	}
        
        @Override
        public void calculateActivation() {
        
        }


}//end class


