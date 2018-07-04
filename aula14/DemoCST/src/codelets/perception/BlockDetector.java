package codelets.perception;

import br.unicamp.cst.core.entities.Codelet;
import java.util.HashMap;
import java.util.Map;

import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

public class BlockDetector extends Codelet {

    private final String modality = "";
    private Map<String, Object> detectorParams = new HashMap<>();
    private Map<String, Object> detectorParams2 = new HashMap<>();

    @Override
    public void accessMemoryObjects() {
    }

    @Override
    public void calculateActivation() {
    }

    @Override
    public void proc() {
        Thing block = null; //(Thing) sensoryMemory.getSensoryContent(modality, detectorParams);
        WorldPoint position = null; //(WorldPoint) sensoryMemory.getSensoryContent(modality, detectorParams2);
        double activation = 0.0;
        if (block != null) {
//            final double minimumDistanceToThing = minimumDistanceToThing(block, position);
//            activation = Math.max(0, 1 - minimumDistanceToThing / Environment.CELL_WIDTH);
              activation = 1.0;
        }
        return;
    }
}
