package modules;

import edu.memphis.ccrg.lida.sensorymemory.SensoryMemoryImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

public class SensoryMemory extends SensoryMemoryImpl {

    private Map<String, Object> sensorParam;
    private Thing block;
    private Thing food;
    private Thing jewel;
    private List<Thing> thingAhead;
    private Thing leafletJewel;

    private WorldPoint destination;
    private boolean targetReached;
    private WorldPoint position;
   
    public SensoryMemory() {
        this.sensorParam = new HashMap<>();
        this.food = null;
        this.block = null;
        this.jewel = null;
        this.thingAhead = new ArrayList<>();
        this.leafletJewel = null;

        this.destination = null;
        this.targetReached = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void runSensors() {
        sensorParam.clear();
        sensorParam.put("mode", "block");
        block = (Thing) environment.getState(sensorParam);
        sensorParam.clear();
        sensorParam.put("mode", "food");
        food = (Thing) environment.getState(sensorParam);
        sensorParam.clear();
        sensorParam.put("mode", "jewel");
        jewel = (Thing) environment.getState(sensorParam);
        sensorParam.clear();
        sensorParam.put("mode", "thingAhead");
        thingAhead = (List<Thing>) environment.getState(sensorParam);
        sensorParam.clear();
        sensorParam.put("mode", "leafletJewel");
        leafletJewel = (Thing) environment.getState(sensorParam);

        sensorParam.clear();
        sensorParam.put("mode", "destination");
        destination = (WorldPoint) environment.getState(sensorParam);
        sensorParam.clear();
        sensorParam.put("mode", "targetReached");
        targetReached = (boolean) environment.getState(sensorParam);
        sensorParam.clear();
        sensorParam.put("mode", "position");
        position = (WorldPoint) environment.getState(sensorParam);
    }

    @Override
    public Object getSensoryContent(String modality, Map<String, Object> params) {
        Object requestedObject = null;
        String mode = (String) params.get("mode");
        switch (mode) {
            case "targetReached":
                requestedObject = targetReached;
                break;
            case "destination":
                requestedObject = destination;
                break;
            case "position":
                requestedObject = position;
                break;
            case "block":
                requestedObject = block;
                break;
            case "food":
                requestedObject = food;
                break;
            case "jewel":
                requestedObject = jewel;
                break;
            case "thingAhead":
                requestedObject = thingAhead;
                break;
            case "leafletJewel":
                requestedObject = leafletJewel;
                break;
            default:
                break;
        }
        return requestedObject;
    }

    @Override
    public Object getModuleContent(Object... os) {
        return null;
    }

    @Override
    public void decayModule(long ticks) {
    }

}
