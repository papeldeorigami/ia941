package detectors;

import java.util.HashMap;
import java.util.Map;

import edu.memphis.ccrg.lida.pam.tasks.BasicDetectionAlgorithm;
import java.awt.geom.Rectangle2D;
import java.util.List;
import modules.Environment;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

public class BlockDetector extends BasicDetectionAlgorithm {

    private final String modality = "";
    private Map<String, Object> detectorParams = new HashMap<>();
    private Map<String, Object> detectorParams2 = new HashMap<>();

    @Override
    public void init() {
        super.init();
        detectorParams.put("mode", "block");
        detectorParams2.put("mode", "position");
    }

    @Override
    public double detect() {
        Thing block = (Thing) sensoryMemory.getSensoryContent(modality, detectorParams);
        WorldPoint position = (WorldPoint) sensoryMemory.getSensoryContent(modality, detectorParams2);
        double activation = 0.0;
        if (block != null) {
//            final double minimumDistanceToThing = minimumDistanceToThing(block, position);
//            activation = Math.max(0, 1 - minimumDistanceToThing / Environment.CELL_WIDTH);
              activation = 1.0;
        }
        return activation;
    }
}
