package codelets.perception;

import br.unicamp.cst.core.entities.Codelet;
import java.util.HashMap;
import java.util.Map;


public class TargetReachedDetector extends Codelet {

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
        boolean targetReached = false; //(boolean) sensoryMemory.getSensoryContent(modality, detectorParams);
        double activation = 0.0;
        if (targetReached) {
            activation = 1.0;
        }
        return;
    }
}
