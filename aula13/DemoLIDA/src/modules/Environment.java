package modules;

import edu.memphis.ccrg.lida.environment.EnvironmentImpl;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.ThetaStarGridFinder;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;
import ws3dproxy.model.SelfAttributes;
import ws3dproxy.model.Thing;
import ws3dproxy.model.World;
import ws3dproxy.model.WorldPoint;
import ws3dproxy.util.Constants;

public class Environment extends EnvironmentImpl {

    public static final Integer[] obstacleCategories = new Integer[]{ Constants.categoryBRICK, Constants.categoryCREATURE };
    public static final String INITIAL_ACTION = "moveToDestination";
    private static final int DEFAULT_TICKS_PER_RUN = 100;
    public static final int INITIAL_DESTINATION_X = 300;
    public static final int INITIAL_DESTINATION_Y = 400;
    private int ticksPerRun;    
    private WS3DProxy proxy;
    private Creature creature;
    private Thing block;
    private Thing food;
    private Thing jewel;
    private WorldPoint targetDestination;
    private List<Thing> thingAhead;
    private Thing leafletJewel;
    private String currentAction;
    
    // attributes necessary to calculate paths
    private List<Thing> thingsMemory;
    private List<Thing> obstacles;
    private List<GridCell> plan;
    private int environmentWidth;
    private int environmentHeight;
    private WorldPoint destination;
    private double creatureWidth;
    private double creatureHeight;
    private int gridWidth;
    private int gridHeight;
   
    public Environment() {
        this.ticksPerRun = DEFAULT_TICKS_PER_RUN;
        this.proxy = new WS3DProxy();
        this.creature = null;
        this.block = null;
        this.food = null;
        this.jewel = null;
        this.thingAhead = new ArrayList<>();
        this.leafletJewel = null;
        
        this.currentAction = INITIAL_ACTION;      
        this.targetDestination = new WorldPoint(INITIAL_DESTINATION_X, INITIAL_DESTINATION_Y);
    }

    @Override
    public void init() {
        super.init();
        ticksPerRun = (Integer) getParam("environment.ticksPerRun", DEFAULT_TICKS_PER_RUN);
        taskSpawner.addTask(new BackgroundTask(ticksPerRun));
        
        try {
            System.out.println("Reseting the WS3D World ...");
            final World world = proxy.getWorld();
            world.reset();
            environmentHeight = world.getEnvironmentHeight();
            environmentWidth = world.getEnvironmentWidth();
            this.thingsMemory = new ArrayList<>();
            this.obstacles = new ArrayList<>();
            creature = proxy.createCreature(100, 100, 0);
            calculateGridSize();
            System.out.println("Starting the WS3D Resource Generator ... ");
            World.createBrick(0, 180, 180, 200, 200);
            //World.grow(1);
            //Thread.sleep(4000);
            creature.updateState();
            System.out.println("DemoLIDA has started...");
            creature.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTargetDestination(int x, int y) {
        targetDestination.setX(x);
        targetDestination.setY(y);
    }

    private class BackgroundTask extends FrameworkTaskImpl {

        public BackgroundTask(int ticksPerRun) {
            super(ticksPerRun);
        }

        @Override
        protected void runThisFrameworkTask() {
            updateEnvironment();
            performAction(currentAction);
        }
    }

    @Override
    public void resetState() {
        currentAction = INITIAL_ACTION;
    }

    @Override
    public Object getState(Map<String, ?> params) {
        Object requestedObject = null;
        String mode = (String) params.get("mode");
        switch (mode) {
            case "destination":
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

    
    public void updateEnvironment() {
        creature.updateState();
        storeNewThingsInVision();
        destination = planNextStep();
        block = null;
        food = null;
        jewel = null;
        leafletJewel = null;
        thingAhead.clear();
        
        for (Thing thing : creature.getThingsInVision()) {
            if (creature.calculateDistanceTo(thing) <= Constants.OFFSET) {
                if (thing.getCategory() == Constants.categoryBRICK) {
                    block = thing;
                }
            }
            
        }
        
//        for (Thing thing : creature.getThingsInVision()) {
//            if (creature.calculateDistanceTo(thing) <= Constants.OFFSET) {
//                // Identifica o objeto proximo
//                thingAhead.add(thing);
//                break;
//            } else if (thing.getCategory() == Constants.categoryJEWEL) {
//                if (leafletJewel == null) {
//                    // Identifica se a joia esta no leaflet
//                    for(Leaflet leaflet: creature.getLeaflets()){
//                        if (leaflet.ifInLeaflet(thing.getMaterial().getColorName()) &&
//                                leaflet.getTotalNumberOfType(thing.getMaterial().getColorName()) > leaflet.getCollectedNumberOfType(thing.getMaterial().getColorName())){
//                            leafletJewel = thing;
//                            break;
//                        }
//                    }
//                } else {
//                    // Identifica a joia que nao esta no leaflet
//                    jewel = thing;
//                }
//            } else if (food == null && creature.getFuel() <= 300.0
//                        && (thing.getCategory() == Constants.categoryFOOD
//                        || thing.getCategory() == Constants.categoryPFOOD
//                        || thing.getCategory() == Constants.categoryNPFOOD)) {
//                
//                    // Identifica qualquer tipo de comida
//                    food = thing;
//            }
//           
//        }
    }
    
    
    
    @Override
    public void processAction(Object action) {
        String actionName = (String) action;
        currentAction = actionName.substring(actionName.indexOf(".") + 1);
    }

    private void performAction(String currentAction) {
        try {
            //System.out.println("Action: "+currentAction);
            switch (currentAction) {
                case "rotate":
                    creature.rotate(1.0);
                    //CommandUtility.sendSetTurn(creature.getIndex(), -1.0, -1.0, 3.0);
                    break;
                case "moveToDestination":
                    final WorldPoint position = creature.getPosition();
                    if ((destination != null) && (distanceLessThanGivenSize(position, destination, (int) creatureWidth / 2, (int) creatureHeight / 2))) {
                        break;
                    }
                    if (destination != null) 
                        creature.moveto(3.0, destination.getX(), destination.getY());
                    break;
                case "gotoFood":
                    if (food != null) 
                        creature.moveto(3.0, food.getX1(), food.getY1());
                        //CommandUtility.sendGoTo(creature.getIndex(), 3.0, 3.0, food.getX1(), food.getY1());
                    break;
                case "gotoJewel":
                    if (leafletJewel != null)
                        creature.moveto(3.0, leafletJewel.getX1(), leafletJewel.getY1());
                        //CommandUtility.sendGoTo(creature.getIndex(), 3.0, 3.0, leafletJewel.getX1(), leafletJewel.getY1());
                    break;                    
                case "get":
                    //creature.move(0.0, 0.0, 0.0);
                    //CommandUtility.sendSetTurn(creature.getIndex(), 0.0, 0.0, 0.0);
                    if (thingAhead != null) {
                        for (Thing thing : thingAhead) {
                            if (thing.getCategory() == Constants.categoryJEWEL) {
                                creature.putInSack(thing.getName());
                            } else if (thing.getCategory() == Constants.categoryFOOD || thing.getCategory() == Constants.categoryNPFOOD || thing.getCategory() == Constants.categoryPFOOD) {
                                creature.eatIt(thing.getName());
                            }
                        }
                    }
                    this.resetState();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Represent the world, with its obstacles, as a grid with each cell based on the size of the creature
     * 
     */
    private GridCell[][] createWorldGrid() {       
        GridCell[][] grid = new GridCell[gridWidth][gridHeight];
        for (int x = 0; x < gridWidth; x++) {
            for (int y = gridHeight - 1; y > 0; y--) {
                boolean obstacle = checkObstaclesInCell(x, y);
                final GridCell gridCell = new GridCell(x, y, !obstacle);
                grid[x][y] = gridCell;
                //System.out.println(gridCell + " = " + !obstacle);
            }            
        }        

        return grid;
    }
   
    WorldPoint gridCellCenter(GridCell gridCell) {
        return new WorldPoint(gridCell.getX() * creatureWidth + creatureWidth / 2, gridCell.getY() * creatureHeight + creatureHeight / 2);
    }
            
    /**
     * Draw the shortest path to complete all leaflets
     */
    private List<GridCell> rebuildPlan() {
        GridCell[][] cells = createWorldGrid();
        NavigationGrid<GridCell> navGrid = new NavigationGrid(cells, false);

        //ThetaStarGridFinder<GridCell> finder = new ThetaStarGridFinder(GridCell.class);
        AStarGridFinder<GridCell> finder = new AStarGridFinder(GridCell.class);
        final GridCell start = worldPointToGridCell(creature.getPosition());
        final GridCell end = worldPointToGridCell(targetDestination);
        List<GridCell> newPlan = finder.findPath(start.getX(), start.getY(), end.getX(), end.getY(), navGrid);
        return newPlan;
    }
    
    private boolean distanceLessThanGivenSize(WorldPoint point1, WorldPoint point2, int width, int height) {
        return (Math.abs(point1.getX() - point2.getX()) < width)
                        && (Math.abs(point1.getY() - point2.getY()) < height);
    }
    
    private WorldPoint planNextStep() {
        WorldPoint currentDestination = destination;
        if ((creature == null) || (targetDestination == null)) {
            return null;
        }
        
        plan = rebuildPlan();

        if (plan == null || plan.isEmpty()) {
            System.out.println("Warning: could not plan a route to the target destination");
            return currentDestination;
        }
        
        final GridCell nextDestinationGrid = plan.get(0);
        WorldPoint destinationGridCenter = gridCellCenter(nextDestinationGrid);
        
        // check if the next step is not within the cell containing the target destination
        if (distanceLessThanGivenSize(destinationGridCenter, targetDestination, (int) creatureWidth, (int) creatureHeight))
        {
            return targetDestination;
        } else {
            return destinationGridCenter;
        }
    }

    /**
     * Store thing in memory
     * 
     * This routine checks if already persisted before storing
     * 
     * @param thingName 
     */
    private void storeInPersistentMemoryIfNotAlreadyExists(Thing thing) {
        boolean exists = false;
        for (Thing t : thingsMemory) {
            if (t.getName().equals(thing.getName())) {
                exists = true;
                break;
            }
        }
        if(!exists) {
            thingsMemory.add(thing);            
            if (isObstacle(thing)) {
                obstacles.add(thing);
            }
        }
    }

    /**
     * Remove thing from memory
     * 
     * @param thingName 
     */
    private void removeFromPersistentMemory(String thingName) {
        for (Iterator<Thing> it = thingsMemory.iterator(); it.hasNext();) {
            Thing t = it.next();
            if (t.getName().equals(thingName)) {
                it.remove();
                break;
            }
        }
    }

    private GridCell worldPointToGridCell(WorldPoint p) {
        return new GridCell((int) Math.round(p.getX() / creatureWidth), (int) Math.round(p.getY() / creatureHeight));
    }
    
    private boolean checkObstaclesInCell(int x, int y) {
        for (Thing t : obstacles) {
            int left = (int) (Math.min(t.getX1(), t.getX2()) / creatureWidth);
            int right = (int) (Math.max(t.getX1(), t.getX2()) / creatureWidth);
            int top = (int) (Math.min(t.getY1(), t.getY2()) / creatureHeight);
            int bottom = (int) (Math.max(t.getY1(), t.getY2()) / creatureHeight);
            if ((left <= x) && (right >= x) && (top <= y) && (bottom >= y)) {
                return true;
            }
        }
        return false;
    }

    private boolean isObstacle(Thing t) {
        for (final int i : obstacleCategories) {
             if (i == t.getCategory()) {
                 return true;
             }
        }
        return false;
    }

    private void storeNewThingsInVision() {
        List<Thing> thingsInVision = creature.getThingsInVision();
        for (Thing t : thingsInVision) {
            storeInPersistentMemoryIfNotAlreadyExists(t);
        }
    }

    private void calculateGridSize() {
        final SelfAttributes attributes = creature.getAttributes();
        creatureWidth = Math.abs(attributes.getX2() - attributes.getX1());
        creatureHeight = Math.abs(attributes.getY2() - attributes.getY1());
        gridWidth = (int) Math.round(environmentWidth / creatureWidth);
        gridHeight = (int) Math.round(environmentHeight / creatureHeight);
    }
    
    public List<GridCell> getPlan() {
        return plan;
    }
   
}
