package modules;

import edu.memphis.ccrg.lida.sensorymemory.SensoryMemoryImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

public class SensoryMemory extends SensoryMemoryImpl {

    private Map<String, Object> sensorParam;
    private Thing block;
    private Thing food;
    private Thing jewel;
    private List<Thing> thingAhead;
    private Thing leafletJewel;

    // attributes necessary to calculate paths
    private static final int WIDTH = 80;
    private static final int HEIGHT = 60;
    private GridCell[][] cells;    
    private NavigationGrid<GridCell> navGrid;
    private List<GridCell> path;
    private WorldPoint position;
    private WorldPoint destination;
    private WorldPoint targetDestination;
   
    public SensoryMemory() {
        this.sensorParam = new HashMap<>();
        this.food = null;
        this.block = null;
        this.jewel = null;
        this.thingAhead = new ArrayList<>();
        this.leafletJewel = null;

        this.targetDestination = null;
        this.position = null;
        this.destination = null;
        this.cells = new GridCell[WIDTH][HEIGHT];
        this.navGrid = null;
        this.path = new ArrayList<>();
        cells = createCells();
        this.navGrid = new NavigationGrid(cells, false);
        path.add(new GridCell(20,15));
        path.add(new GridCell(30,30));
        path.add(new GridCell(40,35));
        path.add(new GridCell(50,40));
    }

    private GridCell[][] createCells() {
        GridCell[][] c = new GridCell[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                c[x][y] = new GridCell(x, y, true);
            }            
        }
        // add some imaginary obstacles, just for testing purposes
        c[25][10].setWalkable(false);
        c[25][11].setWalkable(false);
        c[25][12].setWalkable(false);
        c[25][13].setWalkable(false);
        c[25][14].setWalkable(false);
        c[25][15].setWalkable(false);
        return c;
    }
   
    @SuppressWarnings("unchecked")
    @Override
    public void runSensors() {
        sensorParam.clear();
        sensorParam.put("mode", "position");
        position = (WorldPoint) environment.getState(sensorParam);
        sensorParam.clear();
        sensorParam.put("mode", "targetDestination");
        targetDestination = (WorldPoint) environment.getState(sensorParam);
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
    }

    @Override
    public Object getSensoryContent(String modality, Map<String, Object> params) {
        Object requestedObject = null;
        String mode = (String) params.get("mode");
        switch (mode) {
            case "destination":
                destination = targetDestination; // TODO calculate path
                requestedObject = destination;
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
