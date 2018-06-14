package detectors;

import java.util.HashMap;
import java.util.Map;

import edu.memphis.ccrg.lida.pam.tasks.BasicDetectionAlgorithm;
import ws3dproxy.model.WorldPoint;

public class DestinationDetector extends BasicDetectionAlgorithm {

    private final String modality = "";
    private Map<String, Object> detectorParams = new HashMap<>();

    @Override
    public void init() {
        super.init();
        detectorParams.put("mode", "destination");
    }

    @Override
    public double detect() {
        WorldPoint destination = (WorldPoint) sensoryMemory.getSensoryContent(modality, detectorParams);
        double activation = 0.0;
        if (destination != null) {
            activation = 1.0;
        }
        return activation;
    }
}
