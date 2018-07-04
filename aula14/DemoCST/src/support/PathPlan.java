package support;

import java.util.ArrayList;
import java.util.List;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;
import org.xguzm.pathfinding.grid.finders.JumpPointFinder;
import org.xguzm.pathfinding.grid.finders.ThetaStarGridFinder;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

public class PathPlan {

    public static final int INITIAL_DESTINATION_X = 650;
    public static final int INITIAL_DESTINATION_Y = 450;
    
    public static final double CELL_WIDTH = 40;
    public static final double CELL_HEIGHT = 40;

    public static enum Finder {
        AUTO,
        A_STAR_FINDER,
        THETA_FINDER,
        JUMP_POINT_FINDER
    }
    public static final int INITIAL_POSITION_X = 50;
    public static final int INITIAL_POSITION_Y = 50;

    private WorldPoint targetDestination;
    private List<GridCell> plan;
    private List<Thing> obstacles;
    private WorldPoint destination;
    private boolean targetReached;
    private int gridWidth;
    private int gridHeight;
    private Finder finder = Finder.A_STAR_FINDER;
    private boolean allowDiagonal;
    private GridCell[][] grid;

    public PathPlan(int environmentWidth, int environmentHeight) {
        this.targetDestination = new WorldPoint(INITIAL_DESTINATION_X, INITIAL_DESTINATION_Y);
        calculateGridSize(environmentWidth, environmentHeight);
    }
    
    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }
    
    public void setTargetDestination(WorldPoint targetDestionation) {
        this.targetDestination = targetDestionation;
        this.targetReached = false;
    }

    public WorldPoint getTargetDestination() {
        return this.targetDestination;
    }

    public void setFinder(Finder finder) {
        this.finder = finder;
    }
    
    public void setAllowDiagonal(boolean allowDiagonal) {
        this.allowDiagonal = allowDiagonal;
    }
    
    public boolean isTargetReached() {
        return targetReached;
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
    private List<GridCell> rebuildPlan(WorldPoint position) {
        NavigationGrid<GridCell> navGrid = new NavigationGrid(grid, false);

        GridFinderOptions opt = new GridFinderOptions();
        opt.allowDiagonal = this.allowDiagonal;
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
        // avoid infinite loop because of adjacent cells
        GridCell firstStep = newPlan.get(0);
        while ((tooClose(firstStep, position) || worldPointToGridCell(position) == firstStep) && !newPlan.isEmpty()) {
            newPlan.remove(0);
            if (!newPlan.isEmpty()) {
                firstStep = newPlan.get(0);
            }
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

    public WorldPoint planNextStep(WorldPoint position) {
        return planNextStep(position, obstacles);
    }
    
    public WorldPoint planNextStep(WorldPoint position, List<Thing> obstacles) {
        WorldPoint currentDestination = destination;
        
        this.obstacles = obstacles;
        if ((targetDestination == null) || targetReached || (obstacles == null)) {
            destination = null;
            return null;
        }

        if (obstacles.isEmpty()) {
            destination = targetDestination;
            return targetDestination;
        }

        if (targetDestination != null && (position != null) && (position.distanceTo(targetDestination) <= CELL_WIDTH)) {
            targetReached = true;
            destination = null;
            return null;
        }
        // check if the next step is not within the cell containing the target destination
//        if ((currentDestination != null) && (tooClose(currentDestination, position) && (worldPointToGridCell(position) != worldPointToGridCell(currentDestination)))) {
//            return currentDestination;
//        }

        createWorldGrid();

        plan = rebuildPlan(position);

        if (plan == null || plan.isEmpty()) {
            destination = targetDestination;
            return destination;
        }

        final GridCell nextDestinationGrid = plan.get(0);

        WorldPoint destinationGridCenter = gridCellCenter(nextDestinationGrid);

        destination = destinationGridCenter;
        
        return destination;
    }

    public GridCell worldPointToGridCell(WorldPoint p) {
        if (p == null) {
            return null;
        }
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

    private void calculateGridSize(int environmentWidth, int environmentHeight) {
        gridWidth = (int) Math.floor(environmentWidth / CELL_WIDTH);
        gridHeight = (int) Math.floor(environmentHeight / CELL_HEIGHT);
    }

    public Integer[] getGridSize() {
        return new Integer[]{gridWidth, gridHeight};
    }

    public List<GridCell> getPlan() {
        return plan;
    }

    private boolean tooClose(WorldPoint point, WorldPoint position) {
        return (point != null) && (distanceLessThanGivenSize(point, position, (int) CELL_WIDTH, (int) CELL_HEIGHT));
    }

    private boolean tooClose(GridCell cell, WorldPoint position) {
        return (cell != null) && (tooClose(gridCellCenter(cell), position));
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
}
