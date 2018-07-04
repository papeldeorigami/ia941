package codelets.perception;

import br.unicamp.cst.core.entities.Codelet;
import java.util.HashMap;
import java.util.Map;

import ws3dproxy.model.WorldPoint;

public class DestinationDetector extends Codelet {

    private final String modality = "";
    private Map<String, Object> detectorParams = new HashMap<>();

    @Override
    public void accessMemoryObjects() {
    }

    @Override
    public void calculateActivation() {
    }

    @Override
    public void proc() {
        WorldPoint destination = null; //(WorldPoint) sensoryMemory.getSensoryContent(modality, detectorParams);
        double activation = 0.0;
        if (destination != null) {
            activation = 1.0;
        }
        return;
    }
}
