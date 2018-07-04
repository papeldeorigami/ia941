/** ***************************************************************************
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
 **************************************************************************** */
package codelets.behaviors;

import org.json.JSONException;
import org.json.JSONObject;
import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import memory.CreatureInnerSense;
import support.PathPlan;
import ws3dproxy.model.WorldPoint;

public class GoToDestination extends Codelet {

    public static final double MINIMUM_SPEED = 0.5;
    
    private MemoryObject pathPlanMO;
    private MemoryObject innerSenseMO;
    private MemoryContainer legsMO;
    private int legsMOIndex = -1;
    private int creatureBasicSpeed;
    private double reachDistance;

    public GoToDestination(int creatureBasicSpeed, int reachDistance) {
        setTimeStep(50);		
        this.creatureBasicSpeed = creatureBasicSpeed;
        this.reachDistance = reachDistance;
    }

    @Override
    public void accessMemoryObjects() {
        pathPlanMO = (MemoryObject) this.getInput("PATH_PLAN");
        innerSenseMO = (MemoryObject) this.getInput("INNER");
        legsMO = (MemoryContainer) this.getOutput("LEGS");
    }

    @Override
    public void proc() {
        PathPlan pathPlan = (PathPlan) pathPlanMO.getI();
        CreatureInnerSense cis = (CreatureInnerSense) innerSenseMO.getI();

        boolean targetReached;
        synchronized (pathPlan) {
            targetReached = pathPlan.isTargetReached();
        }

        if (targetReached) {
            JSONObject message = new JSONObject();
            try {
                message.put("ACTION", "STOP");
                message.put("BEHAVIOR", this.getClass().getSimpleName());
                setLegsMO(message.toString(), false);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }            
        }
        
        WorldPoint position;
        synchronized (cis) {
            position = cis.position;
        }

        WorldPoint destination;
        synchronized (pathPlan) {
            // pathPlan.planNextStep(position);
            destination = pathPlan.getDestination();
        }

        if (destination != null && position != null) {
            double distance = position.distanceTo(destination);
            double speed = Math.max(MINIMUM_SPEED, Math.min(creatureBasicSpeed, position.distanceTo(destination) / pathPlan.getGridWidth()));
            JSONObject message = new JSONObject();
            try {
                message.put("ACTION", "GOTO");
                message.put("X", (int) destination.getX());
                message.put("Y", (int) destination.getY());
                message.put("SPEED", speed);
                message.put("BEHAVIOR", this.getClass().getSimpleName());
                setLegsMO(message.toString(), false);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        setLegsMO("", true);
    }

    @Override
    public void calculateActivation() {

    }

    private void setLegsMO(String command, boolean setLowestEvaluation) {
        if (setLowestEvaluation) {
            if (legsMOIndex >= 0) {
                legsMO.setI(command, 0.0, legsMOIndex);
            }
            return;
        }
        if (legsMOIndex < 0) {
            legsMOIndex = legsMO.setI(command, EvaluationConstants.LEGS_GO_TO_DESTINATION_EVALUATION);
        } else {
            legsMO.setI(command, EvaluationConstants.LEGS_GO_TO_DESTINATION_EVALUATION, legsMOIndex);
        }
    }
}
