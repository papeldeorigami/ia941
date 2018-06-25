package detectors;

import java.util.HashMap;
import java.util.Map;

import edu.memphis.ccrg.lida.pam.tasks.BasicDetectionAlgorithm;
import ws3dproxy.model.WorldPoint;

public class TargetReachedDetector extends BasicDetectionAlgorithm {

    private final String modality = "";
    private Map<String, Object> detectorParams = new HashMap<>();

    @Override
    public void init() {
        super.init();
        detectorParams.put("mode", "targetReached");
    }

    @Override
    public double detect() {
        boolean targetReached = (boolean) sensoryMemory.getSensoryContent(modality, detectorParams);
        double activation = 0.0;
        if (targetReached) {
            activation = 1.0;
        }
        return activation;
    }
}
