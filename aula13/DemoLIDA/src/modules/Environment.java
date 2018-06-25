package modules;

import edu.memphis.ccrg.lida.environment.EnvironmentImpl;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;
import org.xguzm.pathfinding.grid.finders.ThetaStarGridFinder;
import org.xguzm.pathfinding.grid.finders.JumpPointFinder;
import ws3dproxy.CommandExecException;
import ws3dproxy.CommandUtility;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;
import ws3dproxy.model.World;
import ws3dproxy.model.WorldPoint;
import ws3dproxy.util.Constants;

public class Environment extends EnvironmentImpl {

    //public static final double CELL_WIDTH = Constants.CREATURE_SIZE;
    //public static final double CELL_HEIGHT = Constants.CREATURE_SIZE;
    public static final double CELL_WIDTH = 40;
    public static final double CELL_HEIGHT = 40;

    public static enum Finder {
        AUTO,
        A_STAR_FINDER,
        THETA_FINDER,
        JUMP_POINT_FINDER
    }
    public static final Integer[] obstacleCategories = new Integer[]{Constants.categoryBRICK};
    public static final Integer[] getCategories = new Integer[]{Constants.categoryFOOD, Constants.categoryFOOD, Constants.categoryNPFOOD, Constants.categoryPFOOD};
    public static final String INITIAL_ACTION = "none";
    private static final int DEFAULT_TICKS_PER_RUN = 100;
    public static final int INITIAL_POSITION_X = 50;
    public static final int INITIAL_POSITION_Y = 50;
    public static final int INITIAL_DESTINATION_X = 650;
    public static final int INITIAL_DESTINATION_Y = 450;
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
    private WorldPoint lastDestination;
    private WorldPoint lastDestinationTime;
    private WorldPoint lastPosition;
    private boolean targetReached;
    private int gridWidth;
    private int gridHeight;
    private Finder finder = Finder.A_STAR_FINDER;
    private boolean allowDiagonal;
    private GridCell[][] grid;

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
            creature = proxy.createCreature(INITIAL_POSITION_X, INITIAL_POSITION_Y, 0);
            calculateGridSize();
            System.out.println("Starting the WS3D Resource Generator ... ");
//            World.createBrick(2, 120, 0, 160, 400);
//            World.createBrick(3, 240, 200, 280, 600);
//            World.createBrick(4, 400, 0, 440, 280);
//            World.createBrick(4, 400, 400, 440, 600);
//            World.createBrick(4, 520, 300, 560, 340);
//            World.createBrick(4, 680, 200, 800, 400);
            int HALL_WIDTH = 100;
            int BLOCK_WIDTH = 40;
            World.createBrick(2, HALL_WIDTH, 0, HALL_WIDTH + BLOCK_WIDTH, 400);
            World.createBrick(3, 2 * HALL_WIDTH + 2 * BLOCK_WIDTH, 200, 2 * HALL_WIDTH + 3 * BLOCK_WIDTH, 600);
            World.createBrick(4, 3 * HALL_WIDTH + 3 * BLOCK_WIDTH, 0, 3 * HALL_WIDTH + 4 * BLOCK_WIDTH, 280);
            World.createBrick(4, 3 * HALL_WIDTH + 3 * BLOCK_WIDTH, 400, 3 * HALL_WIDTH + 4 * BLOCK_WIDTH, 600);
            World.createBrick(4, 4 * HALL_WIDTH + 4 * BLOCK_WIDTH, 300, 4 * HALL_WIDTH + 5 * BLOCK_WIDTH, 340);
            World.createBrick(4, 5 * HALL_WIDTH + 5 * BLOCK_WIDTH, 200, 5 * HALL_WIDTH + 6 * BLOCK_WIDTH, 400);
            World.createFood(0, INITIAL_DESTINATION_X, INITIAL_DESTINATION_Y);
            //World.grow(1);
            //Thread.sleep(4000);
            creature.updateState();
            System.out.println("DemoLIDA has started...");
            creature.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTargetDestination(int x, int y, boolean useThetaStar, boolean allowDiagonal, Finder finder) {
        targetDestination.setX(x);
        targetDestination.setY(y);
        this.finder = finder;
        this.allowDiagonal = allowDiagonal;
        try {
            World.createFood(0, x, y);
        } catch (CommandExecException ex) {
            Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
        }
        targetReached = false;
        destination = planNextStep();
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
        destination = planNextStep();
    }

    @Override
    public Object getState(Map<String, ?> params) {
        Object requestedObject = null;
        String mode = (String) params.get("mode");
        switch (mode) {
            case "targetReached":
                requestedObject = targetReached;
                break;
            case "destination":
//                if (destination == null) {
//                    destination = planNextStep();
//                }
                requestedObject = destination;
                break;
            case "position":
                requestedObject = getPosition();
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
        block = null;
        food = null;
        jewel = null;
        leafletJewel = null;
        thingAhead.clear();
        destination = null;

        WorldPoint position = getPosition();
        
        if (targetDestination != null && (position != null) && (position.distanceTo(targetDestination) <= CELL_WIDTH)) {
            targetReached = true;
            destination = null;
            return;
        }
        
        storeNewThingsInVision();

        for (Thing thing : creature.getThingsInVision()) {
            if (Environment.distanceToThingLessThan(thing, position, CELL_WIDTH)) {
                if (thing.getCategory() == Constants.categoryBRICK) {
                    block = thing;
                    destination = null;
                    break;
                } else if (isGettable(thing)) {
                    thingAhead.add(thing);                    
                }
            }
        }
        
        if (block == null) {
            destination = planNextStep();
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
                    double angle = -1;
                    if (block != null) {
                        angle = oppositeDirectionTo(block);
                    }
                    //creature.rotate(angle);
                    CommandUtility.sendSetAngle(creature.getIndex(), angle, -angle, angle);
                    //resetState();
                    break;
                case "moveToDestination":
                    final WorldPoint position = creature.getPosition();
                    if (destination == null) {
                        break;
                    }
//                    if (position.distanceTo(destination) <= 1) {
//                        destination = planNextStep();
//                        break;
//                    }
                    double speed = Math.max(0.5, Math.min(3, position.distanceTo(destination) / gridWidth));
                    creature.start();
                    creature.moveto(speed, destination.getX(), destination.getY());
                    break;
                case "stop":
                    creature.stop();
                    System.out.println("Agent stoped");
                    targetReached = false;
                    break;
                case "gotoFood":
                    if (food != null) {
                        creature.moveto(3.0, food.getX1(), food.getY1());
                    }
                    //CommandUtility.sendGoTo(creature.getIndex(), 3.0, 3.0, food.getX1(), food.getY1());
                    break;
                case "gotoJewel":
                    if (leafletJewel != null) {
                        creature.moveto(3.0, leafletJewel.getX1(), leafletJewel.getY1());
                    }
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
     * Represent the world, with its obstacles, as a grid with each cell based
     * on the size of the creature
     *
     */
    private GridCell[][] createWorldGrid() {
        grid = new GridCell[gridWidth][gridHeight];
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                boolean obstacle = checkObstaclesInCell(x, y);
                final GridCell gridCell = new GridCell(x, y, !obstacle);
                grid[x][y] = gridCell;
                //System.out.println(gridCell + " = " + !obstacle);
            }
        }

        return grid;
    }

    WorldPoint gridCellCenter(GridCell gridCell) {
        return new WorldPoint(gridCell.getX() * CELL_WIDTH + CELL_WIDTH / 2, gridCell.getY() * CELL_HEIGHT + CELL_HEIGHT / 2);
    }

    /**
     * Draw the shortest path to complete all leaflets
     */
    private List<GridCell> rebuildPlan() {
        NavigationGrid<GridCell> navGrid = new NavigationGrid(grid, false);

        GridFinderOptions opt = new GridFinderOptions();
        opt.allowDiagonal = this.allowDiagonal;
        final WorldPoint position = creature.getPosition();
        final GridCell start = worldPointToGridCell(position);
        final GridCell end = worldPointToGridCell(targetDestination);
        
        if (start == null || end == null) {
            // System.out.println("Warning: Invalid position or target");
            return null;
        }

        List<GridCell> newPlan;
        switch (finder) {
            case THETA_FINDER: {
                ThetaStarGridFinder thetaStarFinder = new ThetaStarGridFinder(GridCell.class, opt);
                newPlan = thetaStarFinder.findPath(start.getX(), start.getY(), end.getX(), end.getY(), navGrid);
                break;
            }
            case A_STAR_FINDER: {
                AStarGridFinder aStarFinder = new AStarGridFinder(GridCell.class, opt);
                newPlan = aStarFinder.findPath(start.getX(), start.getY(), end.getX(), end.getY(), navGrid);
                break;
            }
            case JUMP_POINT_FINDER: {
                JumpPointFinder jumpPointFinder = new JumpPointFinder(GridCell.class, opt);
                newPlan = jumpPointFinder.findPath(start.getX(), start.getY(), end.getX(), end.getY(), navGrid);
                break;
            }
            default: {
                JumpPointFinder jumpPointFinder = new JumpPointFinder(GridCell.class, opt);
                newPlan = jumpPointFinder.findPath(start.getX(), start.getY(), end.getX(), end.getY(), navGrid);
                if (newPlan == null) {
                    ThetaStarGridFinder thetaStarFinder = new ThetaStarGridFinder(GridCell.class, opt);
                    newPlan = thetaStarFinder.findPath(start.getX(), start.getY(), end.getX(), end.getY(), navGrid);
                } else {
                    break;                    
                }
                if (newPlan == null) {
                    AStarGridFinder aStarFinder = new AStarGridFinder(GridCell.class, opt);
                    newPlan = aStarFinder.findPath(start.getX(), start.getY(), end.getX(), end.getY(), navGrid);
                }
                
            }
        }
        if (newPlan == null || newPlan.isEmpty()) {
            //System.out.println("Warning: could not plan a route to the target destination - use old plan");
            return null;
        }
        // avoid infinite loop because of cell too close
        GridCell firstStep = newPlan.get(0);
        while (tooClose(firstStep) && !newPlan.isEmpty()) {
            newPlan.remove(0);
            if (!newPlan.isEmpty())
                firstStep = newPlan.get(0);
        }
        if (newPlan.isEmpty()) {
            //System.out.println("Warning: could not plan a route to the target destination - use old plan");
            return null;
        }
        return newPlan;
    }

    private boolean distanceLessThanGivenSize(WorldPoint point1, WorldPoint point2, int width, int height) {
        return (Math.abs(point1.getX() - point2.getX()) < width)
                && (Math.abs(point1.getY() - point2.getY()) < height);
    }

    private WorldPoint planNextStep() {
        WorldPoint currentDestination = destination;
        if ((creature == null) || (targetDestination == null) || targetReached) {
            return null;
        }

        if (obstacles.isEmpty()) {
            destination = targetDestination;
            return targetDestination;
        }
        
        // check if the next step is not within the cell containing the target destination
        if ((currentDestination != null) && tooClose(currentDestination)) {
            return currentDestination;
        }

        createWorldGrid();
        
        plan = rebuildPlan();

        if (plan == null || plan.isEmpty()) {
            return targetDestination;
        }

        final GridCell nextDestinationGrid = plan.get(0);

        WorldPoint destinationGridCenter = gridCellCenter(nextDestinationGrid);
        
        return destinationGridCenter;
    }

    /**
     * Store thing in memory
     *
     * This routine checks if already persisted before storing
     *
     * @param thingName
     */
    private boolean storeInPersistentMemoryIfNotAlreadyExists(Thing thing) {
        boolean result = false;
        boolean exists = false;
        for (Thing t : thingsMemory) {
            if (t.getName().equals(thing.getName())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            thingsMemory.add(thing);
            if (isObstacle(thing)) {
                obstacles.add(thing);
            }
            result = true;
        }
        return result;
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

    public GridCell worldPointToGridCell(WorldPoint p) {
        if (p == null)
            return null;
        return new GridCell((int) Math.floor(p.getX() / CELL_WIDTH), (int) Math.floor(p.getY() / CELL_HEIGHT));
    }

    private boolean lineIntesects(int line1Start, int line1End, int line2Start, int line2End) {
        if (line1Start <= line2Start) {
            return line2Start < line1End;
        } else {
            return line2End > line1Start;
        }
    }

    private boolean checkObstaclesInCell(int x, int y) {
        final int SEC = 20;
        int cellLeft = (int) Math.floor(x * CELL_WIDTH);
        int cellRight = (int) Math.floor((x + 1) * CELL_WIDTH) - 1;
        int cellTop = (int) Math.floor(y * CELL_HEIGHT);
        int cellBottom = (int) Math.floor((y + 1) * CELL_HEIGHT) - 1;
        for (Thing t : obstacles) {
            int obstacleLeft = (int) (Math.min(t.getX1(), t.getX2()) - SEC);
            int obstacleRight = (int) (Math.max(t.getX1(), t.getX2()) + SEC) - 1;
            int obstacleTop = (int) (Math.min(t.getY1(), t.getY2()) - SEC);
            int obstacleBottom = (int) (Math.max(t.getY1(), t.getY2()) + SEC) - 1;
            if (lineIntesects(cellLeft, cellRight, obstacleLeft, obstacleRight)
                    && lineIntesects(cellTop, cellBottom, obstacleTop, obstacleBottom)) {
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

    private boolean isGettable(Thing t) {
        for (final int i : getCategories) {
            if (i == t.getCategory()) {
                return true;
            }
        }
        return false;
    }

    private boolean storeNewThingsInVision() {
        boolean result = false;
        List<Thing> thingsInVision = creature.getThingsInVision();
        for (Thing t : thingsInVision) {
            if (storeInPersistentMemoryIfNotAlreadyExists(t)) {
                result = true;
            }
        }
        return result;
    }

    private void calculateGridSize() {
        gridWidth = (int) Math.floor(environmentWidth / CELL_WIDTH);
        gridHeight = (int) Math.floor(environmentHeight / CELL_HEIGHT);
    }
    
    public Integer[] getGridSize() {
        return new Integer[] {gridWidth, gridHeight};
    }

    public List<GridCell> getPlan() {
        return plan;
    }

    private boolean tooClose(WorldPoint point) {
        return (point != null) && (distanceLessThanGivenSize(point, creature.getPosition(), (int) CELL_WIDTH, (int) CELL_HEIGHT));
    }

    private boolean tooClose(GridCell cell) {
        return (cell != null) && (tooClose(gridCellCenter(cell)));
    }

    public WorldPoint getPosition() {
        WorldPoint position = creature.getPosition();
        return position;
    }

    public double getPitch() {
        return creature.getPitch();
    }
    
    private double oppositeDirectionTo(Thing thing) {
        WorldPoint thingPosition = thing.getCenterPosition();
        WorldPoint creaturePosition = creature.getPosition();
        if (creature.getPitch() >= 0) {
            if (creaturePosition.getX() <= thingPosition.getX()) {
                return 1.0;
            } else {
                return -1.0;
            }
        } else {
            if (creaturePosition.getX() <= thingPosition.getX()) {
                return -1.0;
            } else {
                return 1.0;
            }            
        }
    }    

    public WorldPoint getDestination() {
        return destination;
    }

    public GridCell[][] getGrid() {
        return grid;
    }

    public static boolean distanceToThingLessThan(Thing t, WorldPoint position, double distance) {
        int x = (int) position.getX();
        int y = (int) position.getY();
        int thingLeft = (int) (Math.min(t.getX1(), t.getX2()) - distance);
        int thingRight = (int) (Math.max(t.getX1(), t.getX2()) + distance) - 1;
        int thingTop = (int) (Math.min(t.getY1(), t.getY2()) - distance);
        int thingBottom = (int) (Math.max(t.getY1(), t.getY2()) + distance) - 1;
        return (x >= thingLeft && x <= thingRight && y >= thingTop && y <= thingBottom);
    }

    public GridCell getGridPosition() {
        return worldPointToGridCell(getPosition());
    }

}
