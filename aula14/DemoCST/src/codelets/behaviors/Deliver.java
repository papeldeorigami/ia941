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


import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import java.util.HashMap;
import java.util.List;
import memory.CreatureInnerSense;
import ws3dproxy.model.Leaflet;

public class Deliver extends Codelet {

	private MemoryObject innerSenseMO;
        private int handsMOIndex = -1;
	private MemoryContainer handsMO;
        private int legsMOIndex = -1;
	private MemoryContainer legsMO;
        CreatureInnerSense cis;

	public Deliver() {
                setTimeStep(50);
	}

	@Override
	public void accessMemoryObjects() {
		innerSenseMO=(MemoryObject)this.getInput("INNER");
		handsMO=(MemoryContainer)this.getOutput("HANDS");
		legsMO=(MemoryContainer)this.getOutput("LEGS");
	}

	@Override
	public void proc() {
                cis = (CreatureInnerSense) innerSenseMO.getI();
                
                if (cis == null)
                    return;
		
                String info = "";
                boolean allLeafletsComplete = false;
                synchronized(cis) {
                    final List<Leaflet> leaflets = cis.leaflets;
                    allLeafletsComplete = checkAllLeafletsComplete(leaflets);
                }
                if (allLeafletsComplete) {
			JSONObject message = new JSONObject();
			try {
                                message.put("OBJECT", "ALL");
                                message.put("ACTION", "DELIVER");
                                message.put("BEHAVIOR", this.getClass().getSimpleName());
                                info = message.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
                        if (handsMOIndex < 0) {
                            handsMOIndex = handsMO.setI(info, EvaluationConstants.HANDS_DELIVER_EVALUATION);
                        } else {                                    
                            handsMO.setI(info, EvaluationConstants.HANDS_DELIVER_EVALUATION, handsMOIndex);
                        }
                        message = new JSONObject();
			try {
                                message.put("ACTION", "STOP");
                                message.put("BEHAVIOR", this.getClass().getSimpleName());
                                info = message.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
                        if (legsMOIndex < 0) {
                            legsMOIndex = legsMO.setI(info, EvaluationConstants.LEGS_STOP_EVALUATION);
                        } else {                                    
                            legsMO.setI(info, EvaluationConstants.LEGS_STOP_EVALUATION, legsMOIndex);
                        }
                        return;
		}
                if (handsMOIndex >= 0) {
                    handsMO.setI("", 0.0, handsMOIndex);
                }
                if (legsMOIndex >= 0) {
                    legsMO.setI("", 0.0, legsMOIndex);
                }
	}
        
        @Override
        public void calculateActivation() {
        
        }
        
        protected boolean checkAllLeafletsComplete(List<Leaflet> leaflets) {
            if (leaflets == null || leaflets.isEmpty())
                return false;
            for (Leaflet leaflet: leaflets) {
                final HashMap<String, Integer> whatToCollect = (HashMap<String, Integer>) leaflet.getWhatToCollect();
                for (String color: whatToCollect.keySet()) {
                    if ((Integer) whatToCollect.get(color) > 0)
                        return false;

                }
            }
            return true;
        }

}
