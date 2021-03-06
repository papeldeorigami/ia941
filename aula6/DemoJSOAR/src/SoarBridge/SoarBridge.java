/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SoarBridge;

import Simulation.Environment;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jsoar.kernel.Agent;
import org.jsoar.kernel.Phase;
import org.jsoar.kernel.RunType;
import org.jsoar.kernel.io.InputBuilder;
import org.jsoar.kernel.io.InputWme;
import org.jsoar.kernel.memory.Wme;
import org.jsoar.kernel.memory.Wmes;
import org.jsoar.kernel.symbols.IntegerSymbol;
import org.jsoar.kernel.symbols.DoubleSymbol;
import org.jsoar.kernel.symbols.Identifier;
import org.jsoar.kernel.symbols.StringSymbol;
import org.jsoar.kernel.symbols.Symbol;
import org.jsoar.kernel.symbols.SymbolFactory;
import org.jsoar.runtime.ThreadedAgent;
import org.jsoar.util.commands.SoarCommands;
import ws3dproxy.CommandExecException;
import ws3dproxy.CommandUtility;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;
import ws3dproxy.util.Constants;

/**
 *
 * @author Danilo Lucentini and Ricardo Gudwin
 */
public class SoarBridge
{

    class ThingWithDistance {
        private final Thing thing;
        private final double distance;
        
        public ThingWithDistance(Thing thing, double distance) {
            this.thing = thing;
            this.distance = distance;
        }
        
        public double getDistance() {
            return this.distance;
        }

        public Thing getThing() {
            return this.thing;
        }
    }
    
    // Log Variable
    Logger logger = Logger.getLogger(SoarBridge.class.getName());

    // SOAR Variables
    Agent agent = null;
    public Identifier inputLink = null;
    InputBuilder builder = null;

    InputBuilder creature = null;

    Environment env;
    public Creature c;
    public String input_link_string = "";
    public String output_link_string = "";

    List<Thing> thingsMemory = null;
    List<WorldPoint> plan = null;
    int deliveredCount;
    
    /**
     * Constructor class
     * @param _e Environment
     * @param path Path for Rule Base
     * @param startSOARDebugger set true if you wish the SOAR Debugger to be started
     */
    public SoarBridge(Environment _e, String path, Boolean startSOARDebugger)
    {
        env = _e;
        c = env.getCreature();
        try
        {
            ThreadedAgent tag = ThreadedAgent.create();
            agent = tag.getAgent();
            SoarCommands.source(agent.getInterpreter(), path);
            builder = InputBuilder.create(agent.getInputOutput());
            inputLink = builder.io.getInputLink();
            thingsMemory = new ArrayList<Thing>();
            plan = new ArrayList<WorldPoint>();
            deliveredCount = 0;

            // Debugger line
            if (startSOARDebugger)
            {
                agent.openDebugger();
            }
        }
        catch (Exception e)
        {
            logger.severe("Error while creating SOAR Kernel");
            e.printStackTrace();
        }
    }

    private Identifier CreateIdWME(Identifier id, String s) {
        SymbolFactory sf = agent.getSymbols();
        Identifier newID = sf.createIdentifier('I');
        builder.io.addInputWme(id, sf.createString(s), newID);
        return(newID);
    }
    
    private void CreateIntegerWME(Identifier id, String s, int value) {
        SymbolFactory sf = agent.getSymbols();
        IntegerSymbol newID = sf.createInteger(value);
        builder.io.addInputWme(id, sf.createString(s), newID);
    }
    
    private void CreateFloatWME(Identifier id, String s, double value) {
        SymbolFactory sf = agent.getSymbols();
        DoubleSymbol newID = sf.createDouble(value);
        builder.io.addInputWme(id, sf.createString(s), newID);
    }
    
    private void CreateStringWME(Identifier id, String s, String value) {
        SymbolFactory sf = agent.getSymbols();
        StringSymbol newID = sf.createString(value);
        builder.io.addInputWme(id, sf.createString(s), newID);
    }
    
    private String getItemType(int categoryType)
    {
        String itemType = null;

        switch (categoryType)
        {
            case Constants.categoryBRICK:
                itemType = "BRICK";
                break;
            case Constants.categoryJEWEL:
                itemType = "JEWEL";
                break;
            case Constants.categoryFOOD:
            case Constants.categoryNPFOOD:
            case Constants.categoryPFOOD:
                itemType = "FOOD";
                break;
            case Constants.categoryCREATURE:
                itemType = "CREATURE";
                break;
        }
        return itemType;
    }
    
    /**
     * Remove all the input link WMEs 
     */
    private void clearInputLink() {
        InputWme inputCreature;
        inputCreature = builder.getWme("CREATURE");
        if (inputCreature != null)
            inputCreature.remove();
    }

    /**
     * Create the WMEs at the InputLink of SOAR
     */
    private void prepareInputLink() {
        // Entity Variables
        InputBuilder creatureSensor;
        InputBuilder creatureParameters;
        InputBuilder creatureLeaflets;
        InputBuilder creatureKnapsack;
        InputBuilder creaturePosition;
        InputBuilder creatureMemory;
        InputBuilder planWme;
    
        //SymbolFactory sf = agent.getSymbols();
        // Always remove the current WMEs before updating the input        
        clearInputLink();
        Creature c = env.getCreature();
        try {
            if (agent != null) {
                //SimulationCreature creatureParameter = (SimulationCreature)parameter;
                // Initialize Creature Entity
                creature = builder.push("CREATURE").markWme("CREATURE");
                // Initialize the plan structure
                planWme = creature.push("PLAN");
                int waypointCounter = 0;
                for (WorldPoint p: plan) {
                    InputBuilder waypoint = planWme.push("WAYPOINT-" + waypointCounter);
                    waypoint.add("X", p.getX());
                    waypoint.add("Y", p.getY());
                    waypointCounter++;
                }
                // Initialize Creature Memory
                creatureMemory = creature.push("MEMORY");
                for (Thing t: thingsMemory) {
                    InputBuilder entity = creatureMemory.push("ENTITY");
                    entity.add("DISTANCE", GetGeometricDistanceToCreature(t.getX1(), t.getY1(), t.getX2(), t.getY2(), c.getPosition().getX(), c.getPosition().getY()));
                    entity.add("X", t.getX1());
                    entity.add("Y", t.getY1());
                    entity.add("X2", t.getX2());
                    entity.add("Y2", t.getY2());
                    entity.add("TYPE", getItemType(t.getCategory()));
                    entity.add("NAME", t.getName());
                    entity.add("COLOR", Constants.getColorName(t.getMaterial().getColor()));
                }
                // Set Creature Parameters
                Calendar lCDateTime = Calendar.getInstance();
                creatureParameters = creature.push("PARAMETERS");
                creatureParameters.add("MINFUEL", 400);
                creatureParameters.add("TIMESTAMP", lCDateTime.getTimeInMillis());
                // Setting creature Position
                creature.add("PITCH", c.getPitch());
                creaturePosition = creature.push("POSITION");
                creaturePosition.add("X", c.getPosition().getX());
                creaturePosition.add("Y", c.getPosition().getY());
                // Set Creature LEAFLETS
                creatureLeaflets = creature.push("LEAFLETS");
                HashMap<String, Integer> collectedColors = new HashMap<String, Integer>();
                collectedColors.put("Red", 0);
                collectedColors.put("Green", 0);
                collectedColors.put("Blue", 0);
                collectedColors.put("Yellow", 0);
                collectedColors.put("Magenta", 0);
                collectedColors.put("White", 0);
                int order = 0;
                int totalCollected = 0;
                int leafletsCount = 0; // avoid more than 3 leaflets
                for (Leaflet l : c.getLeaflets()) {
                    InputBuilder leaflet = creatureLeaflets.push("LEAFLET");
                    leaflet.add("ORDER", order++);
                    leaflet.add("ID", l.getID().toString());
                    leaflet.add("PAYMENT", l.getPayment());
                    HashMap<String, Integer[]> items = l.getItems();
                    HashMap<String, Integer> colors = new HashMap<>();
                    colors.put("Red", 0);
                    colors.put("Green", 0);
                    colors.put("Blue", 0);
                    colors.put("Yellow", 0);
                    colors.put("Magenta", 0);
                    colors.put("White", 0);
                    for (HashMap.Entry<String, Integer[]> entry : items.entrySet()) {
                        String color = entry.getKey();
                        Integer needed = entry.getValue()[0];
                        Integer collected = entry.getValue()[1];
                        colors.put(color, needed);
                        collectedColors.merge(color, collected, Integer::sum);
                        totalCollected += collected;
                    }
                    for (HashMap.Entry<String, Integer> entry : colors.entrySet()) {
                        leaflet.add(entry.getKey(), entry.getValue());
                    }
                    if (totalCollected >= 9) {
                        leaflet.add("COMPLETE", "true");
                    } else {
                        leaflet.add("COMPLETE", "false");
                    }
                    leafletsCount++;
                    if (leafletsCount == 3) {
                        break;
                    }
                }
                // Set Creature KNAPSACK (Bag)
                creatureKnapsack = creature.push("KNAPSACK");
                for (HashMap.Entry<String, Integer> entry : collectedColors.entrySet()) {
                    creatureKnapsack.add(entry.getKey(), entry.getValue());
                }
                // Set creature sensors
                creatureSensor = creature.push("SENSOR");
                // Create Fuel Sensors
                InputBuilder fuel = creatureSensor.push("FUEL");
                fuel.add("VALUE", c.getFuel());
                // Create Visual Sensors
                InputBuilder visual = creatureSensor.push("VISUAL");
                List<Thing> thingsList = (List<Thing>) c.getThingsInVision();
                for (Thing t : thingsList) {
                    InputBuilder entity = visual.push("ENTITY");
                    entity.add("DISTANCE", GetGeometricDistanceToCreature(t.getX1(), t.getY1(), t.getX2(), t.getY2(), c.getPosition().getX(), c.getPosition().getY()));
                    entity.add("X", t.getX1());
                    entity.add("Y", t.getY1());
                    entity.add("X2", t.getX2());
                    entity.add("Y2", t.getY2());
                    entity.add("TYPE", getItemType(t.getCategory()));
                    entity.add("NAME", t.getName());
                    entity.add("COLOR", Constants.getColorName(t.getMaterial().getColor()));
                }
            }
        } catch (Exception e) {
            logger.severe("Error while Preparing Input Link");
            e.printStackTrace();
        }
    }

    private double GetGeometricDistanceToCreature(double x1, double y1, double x2, double y2, double xCreature, double yCreature)
    {
          float squared_dist = 0.0f;
          double maxX = Math.max(x1, x2);
          double minX = Math.min(x1, x2);
          double maxY = Math.max(y1, y2);
          double minY = Math.min(y1, y2);

          if(xCreature > maxX)
          {
            squared_dist += (xCreature - maxX)*(xCreature - maxX);
          }
          else if(xCreature < minX)
          {
            squared_dist += (minX - xCreature)*(minX - xCreature);
          }

          if(yCreature > maxY)
          {
            squared_dist += (yCreature - maxY)*(yCreature - maxY);
          }
          else if(yCreature < minY)
          {
            squared_dist += (minY - yCreature)*(minY - yCreature);
          }

          return Math.sqrt(squared_dist);
    }

    private void resetSimulation() {
        agent.initialize();
    }
    
    /**
     * Run SOAR until HALT
     */
    private void runSOAR() 
    {
        agent.runForever(); 
    }
    
    private int stepSOAR() {
        agent.runFor(1, RunType.PHASES);
        Phase ph = agent.getCurrentPhase();
        if (ph.equals(Phase.INPUT)) return(0);
        else if (ph.equals(Phase.PROPOSE)) return(1);
        else if (ph.equals(Phase.DECISION)) return(2);
        else if (ph.equals(Phase.APPLY)) return(3);
        else if (ph.equals(Phase.OUTPUT)) {
            if (agent.getReasonForStop() == null) return(4);
            else return(5);
        }
        else return(6);
    }

    private String GetParameterValue(String par) {
        List<Wme> Commands = Wmes.matcher(agent).filter(agent.getInputOutput().getOutputLink());
        List<Wme> Parameters = Wmes.matcher(agent).filter(Commands.get(0));
        String parvalue = "";
        for (Wme w : Parameters) 
           if (w.getAttribute().toString().equals(par)) parvalue = w.getValue().toString();
        return(parvalue);
    }
    
    
    /**
     * Process the OutputLink given by SOAR and return a list of commands to WS3D
     * @return A List of SOAR Commands
     */
    private ArrayList<Command> processOutputLink() 
    {
        ArrayList<Command> commandList = new ArrayList<Command>();

        try
        {
            if (agent != null)
            {
                List<Wme> Commands = Wmes.matcher(agent).filter(agent.getInputOutput().getOutputLink());

                for (Wme com : Commands)
                {
                    String name  = com.getAttribute().asString().getValue();
                    Command.CommandType commandType = Enum.valueOf(Command.CommandType.class, name);
                    Command command = null;

                    switch(commandType)
                    {
                        case MOVE:
                            Float rightVelocity = null;
                            Float leftVelocity = null;
                            Float linearVelocity = null;
                            Float xPosition = null;
                            Float yPosition = null;
                            rightVelocity = tryParseFloat(GetParameterValue("VelR"));
                            leftVelocity = tryParseFloat(GetParameterValue("VelL"));
                            linearVelocity = tryParseFloat(GetParameterValue("Vel"));
                            xPosition = tryParseFloat(GetParameterValue("X"));
                            yPosition = tryParseFloat(GetParameterValue("Y"));
                            command = new Command(Command.CommandType.MOVE);
                            CommandMove commandMove = (CommandMove)command.getCommandArgument();
                            if (commandMove != null)
                            {
                                if (rightVelocity != null) commandMove.setRightVelocity(rightVelocity);
                                if (leftVelocity != null)  commandMove.setLeftVelocity(leftVelocity);
                                if (linearVelocity != null) commandMove.setLinearVelocity(linearVelocity);
                                if (xPosition != null) commandMove.setX(xPosition);
                                if (yPosition != null) commandMove.setY(yPosition);
                                commandList.add(command);
                            }
                            else
                            {
                                logger.severe("Error processing MOVE command");
                            }
                            break;

                        case GET:
                            String thingNameToGet = null;
                            command = new Command(Command.CommandType.GET);
                            CommandGet commandGet = (CommandGet)command.getCommandArgument();
                            if (commandGet != null)
                            {
                                thingNameToGet = GetParameterValue("Name");
                                if (thingNameToGet != null) {
                                    commandGet.setThingName(thingNameToGet);
                                    commandList.add(command);
                                } else {
                                    logger.severe("Could not read the thing name for command " + commandType);
                                }
                            }
                            break;

                        case EAT:
                            String thingNameToEat = null;
                            command = new Command(Command.CommandType.EAT);
                            CommandEat commandEat = (CommandEat)command.getCommandArgument();
                            if (commandEat != null)
                            {
                                thingNameToEat = GetParameterValue("Name");
                                if (thingNameToEat != null) {
                                    commandEat.setThingName(thingNameToEat);
                                    commandList.add(command);
                                } else {
                                    logger.severe("Could not read the thing name for command " + commandType);
                                }
                            }
                            break;

                        case HIDE:
                            String thingName = null;
                            command = new Command(Command.CommandType.HIDE);
                            CommandHide commandHide = (CommandHide)command.getCommandArgument();
                            if (commandHide != null)
                            {
                                thingName = GetParameterValue("Name");
                                if (thingName != null) {
                                    commandHide.setThingName(thingName);
                                    commandList.add(command);
                                } else {
                                    logger.severe("Could not read the thing name for command " + commandType);
                                }
                            }
                            break;

                        case MEMORIZE:
                            String thingNameToMemorize = null;
                            command = new Command(Command.CommandType.MEMORIZE);
                            CommandMemorize commandMemorize = (CommandMemorize)command.getCommandArgument();
                            if (commandMemorize != null)
                            {
                                thingNameToMemorize = GetParameterValue("Name");
                                if (thingNameToMemorize != null) {
                                    commandMemorize.setThingName(thingNameToMemorize);
                                    commandList.add(command);
                                } else {
                                    logger.severe("Could not read the thing name for command " + commandType);
                                }
                            }
                            break;
                            
                        case PLAN:
                            command = new Command(Command.CommandType.PLAN);
                            CommandPlan commandPlan = (CommandPlan)command.getCommandArgument();
                            if (commandPlan != null)
                            {
                                String memoryWME = GetParameterValue("Memory");
                                if (memoryWME != null) {
                                    commandPlan.setMemoryWME(memoryWME);
                                    commandList.add(command);
                                } else {
                                    logger.severe("Could not read the memory name for command " + commandType);
                                }
                            }
                            break;

                        case DELIVER:
                            String leafletIdDeliver = null;
                            command = new Command(Command.CommandType.DELIVER);
                            commandList.add(command);
                            break;

                        default:
                            break;
                    }   
                }
            }
        }
        catch (Exception e)
        {
            logger.severe("Error while processing commands");
            e.printStackTrace();
        }

        return ((commandList.size() > 0) ? commandList : null);
    }
    
    /**
     * Perform a complete SOAR step
     * @throws ws3dproxy.CommandExecException
     */
    public void step() throws CommandExecException
    {
        if (phase != -1) finish_msteps();
        //resetSimulation();
        c.updateState();
        prepareInputLink();
        input_link_string = stringInputLink();
        //printInputWMEs();
        runSOAR();
        //stepSOAR();
        output_link_string = stringOutputLink();
        //printOutputWMEs();
        List<Command> commandList = processOutputLink();
        processCommands(commandList);
        //resetSimulation();
    }
    
    
    public void prepare_mstep() {
        resetSimulation();
        c.updateState();
        prepareInputLink();
        input_link_string = stringInputLink();
    }
    
    public int phase=-1;
    public void mstep() throws CommandExecException
    {
        if (phase == -1) prepare_mstep();
        phase = stepSOAR();
        if (phase == 5) {
            post_mstep();
            phase = -1;
        }
    }
    
    public void finish_msteps() throws CommandExecException {
        while (phase != -1) mstep();
    }
    
    public void post_mstep() throws CommandExecException {
        output_link_string = stringOutputLink();
        //printOutputWMEs();
        List<Command> commandList = processOutputLink();
        processCommands(commandList);
        //resetSimulation();
    }

    private void processCommands(List<Command> commandList) throws CommandExecException
    {

        if (commandList != null)
        {
            for (Command command:commandList)
            {
                switch (command.getCommandType())
                {
                    case MOVE:
                        processMoveCommand((CommandMove)command.getCommandArgument());
                    break;

                    case GET:
                        processGetCommand((CommandGet)command.getCommandArgument());
                    break;

                    case EAT:
                        processEatCommand((CommandEat)command.getCommandArgument());
                    break;

                    case HIDE:
                        processHideCommand((CommandHide)command.getCommandArgument());
                    break;

                    case MEMORIZE:
                        processMemorizeCommand((CommandMemorize)command.getCommandArgument());
                    break;

                    case PLAN:
                        processPlanCommand((CommandPlan)command.getCommandArgument());
                    break;
                    
                    case DELIVER:                        
                        processDeliverCommand();
                    break;

                    default:System.out.println("Nenhum comando definido ...");
                        // Do nothing
                    break;
                }
            }
        }
        //else System.out.println("comando nulo ...");
    }

    /**
     * Send Move Command to World Server
     * @param soarCommandMove Soar Move Command Structure
     */
    private void processMoveCommand(CommandMove soarCommandMove) throws CommandExecException
    {
        if (soarCommandMove != null)
        {
            if (soarCommandMove.getX() != null && soarCommandMove.getY() != null)
            {
                CommandUtility.sendGoTo(c.getIndex(), soarCommandMove.getRightVelocity(), soarCommandMove.getLeftVelocity(), soarCommandMove.getX(), soarCommandMove.getY());
            }
            else
            {
                CommandUtility.sendSetTurn(c.getIndex(),soarCommandMove.getLinearVelocity(),soarCommandMove.getRightVelocity(),soarCommandMove.getLeftVelocity());
            }
        }
        else
        {
            logger.severe("Error processing processMoveCommand");
        }
    }

    /**
     * Send Get Command to World Server
     * @param soarCommandGet Soar Get Command Structure
     */
    private void processGetCommand(CommandGet soarCommandGet) throws CommandExecException
    {
        if (soarCommandGet != null)
        {
            final String thingName = soarCommandGet.getThingName();
            c.putInSack(thingName);
            removeFromPersistentMemory(thingName);
            rebuildPlan();
        }
        else
        {
            logger.severe("Error processing processGetCommand");
        }
    }

     /**
     * Send Eat Command to World Server
     * @param soarCommandEat Soar Eat Command Structure
     */
    private void processEatCommand(CommandEat soarCommandEat) throws CommandExecException
    {
        if (soarCommandEat != null)
        {
            String thingName = soarCommandEat.getThingName();
            c.eatIt(thingName);
            removeFromPersistentMemory(thingName);
            rebuildPlan();
        }
        else
        {
            logger.severe("Error processing processEatCommand");
        }
    }
    
     /**
     * Send Hide Command to World Server
     * @param soarCommandHide Soar Hide Command Structure
     */
    private void processHideCommand(CommandHide soarCommandHide) throws CommandExecException
    {
        if (soarCommandHide != null)
        {
            String thingName = soarCommandHide.getThingName();
            c.hideIt(thingName);
            removeFromPersistentMemory(thingName);
            rebuildPlan();
        }
        else
        {
            logger.severe("Error processing processHideCommand");
        }
    }
    
     /**
     * Store thing in the creature's persistent memory
     * @param soarCommandMemorize Soar Memorize Command Structure
     */
    private void processMemorizeCommand(CommandMemorize soarCommandMemorize) throws CommandExecException
    {
        if (soarCommandMemorize != null)
        {
            storeInPersistentMemory(soarCommandMemorize.getThingName());
            rebuildPlan();
        }
        else
        {
            logger.severe("Error processing processMoveCommand");
        }
    }
    
     /**
     * Process the Plan Command
     * @param soarCommandPlan Soar Memory WME name
     */
    private void processPlanCommand(CommandPlan soarCommandPlan) {
        if (soarCommandPlan != null)
        {
            rebuildPlan();
        }
        else
        {
            logger.severe("Error processing processMoveCommand");
        }        
    }

    private void processDeliverCommand() {
        try {
            c.stop();
            for (Leaflet l: c.getLeaflets()) {
                String leafletId = String.valueOf(l.getID());
                c.deliverLeaflet(leafletId);
                logger.info("Delivered " + leafletId);                
            }
        } catch (CommandExecException ex) {
            Logger.getLogger(SoarBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog(null, "Creature: " + c.getName() + " - All leaflets complete!");            
        logger.info("All leaflets delivered!");
    }

    /**
     * Try Parse a Float Element
     * @param value Float Value
     * @return The Float Value or null otherwise
     */
    private Float tryParseFloat (String value)
    {
        Float returnValue = null;

        try
        {
            returnValue = Float.parseFloat(value);
        }
        catch (Exception ex)
        {
            returnValue = null;
        }

        return returnValue;
    }
    
    public void printWME(Identifier id) {
        printWME(id,0);
        
    }
    
    public void printWME(Identifier id, int level) {
        Iterator<Wme> It = id.getWmes();
        while (It.hasNext()) {
            Wme wme = It.next();
            Identifier idd = wme.getIdentifier();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            for (int i=0;i<level;i++) System.out.print("   ");
            if (testv != null) {
                System.out.print("("+idd.toString()+","+a.toString()+","+v.toString()+")\n");
                printWME(testv,level+1);
            }
            else System.out.print("("+idd.toString()+","+a.toString()+","+v.toString()+")\n");
        }   
    }
    
    public void printInputWMEs(){
        Identifier il = inputLink;
        System.out.println("Input --->");
        printWME(il);
    }
    
    public void printOutputWMEs(){
        Identifier ol = agent.getInputOutput().getOutputLink();
        System.out.println("Output --->");
        printWME(ol);
    }
    
    public String stringWME(Identifier id) {
        String out = stringWME(id,0);
        return(out);
    }
    
    public String stringWME(Identifier id, int level) {
        String out="";
        Iterator<Wme> It = id.getWmes();
        while (It.hasNext()) {
            Wme wme = It.next();
            Identifier idd = wme.getIdentifier();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            for (int i=0;i<level;i++) out += "   ";
            if (testv != null) {
                out += "("+idd.toString()+","+a.toString()+","+v.toString()+")\n";
                out += stringWME(testv,level+1);
            }
            else out += "("+idd.toString()+","+a.toString()+","+v.toString()+")\n";
        }
       return(out); 
    }
    
    public String stringInputLink() {
        Identifier il = inputLink;
        String out = stringWME(il);
        return(out);
    }
    
    public String stringOutputLink() {
        Identifier ol = agent.getInputOutput().getOutputLink();
        String out = stringWME(ol);
        return(out);
    }
    
    public Identifier getInitialState() {
        Set<Wme> allmem = agent.getAllWmesInRete();
        for (Wme w : allmem) {
            Identifier id = w.getIdentifier();
            if (id.toString().equalsIgnoreCase("S1"))
                return(id);
        }
        return(null);
    }
    
    public List<Identifier> getStates() {
        List<Identifier> li = new ArrayList<Identifier>();
        Set<Wme> allmem = agent.getAllWmesInRete();
        for (Wme w : allmem) {
            Identifier id = w.getIdentifier();
            if (id.isGoal()) {
                boolean alreadythere = false;
                for (Identifier icand : li)
                    if (icand == id) alreadythere = true;
                if (alreadythere == false) {
                    li.add(id);
                }
            }
        }
        return(li);
    }
    
    public Set<Wme> getWorkingMemory() {
        return(agent.getAllWmesInRete());
    }

    /**
     * Store thing in memory
     * 
     * Lookup thing data in the creature vision using the given thing name
     * *Warning*: doesn't check if already persisted
     * 
     * @param thingName 
     */
    private void storeInPersistentMemory(String thingName) {
        List<Thing> thingsList = (List<Thing>) c.getThingsInVision();
        for (Thing t: thingsList) {
            if (t.getName().equals(thingName)) {
                thingsMemory.add(t);
                break;
            }
        };
    }

    /**
     * Remove thing from memory
     * 
     * @param thingName 
     */
    private void removeFromPersistentMemory(String thingName) {
        thingsMemory.removeIf(t -> t.getName().equals(thingName));
    }

    /**
     * Draw the shortest path to complete all leaflets
     */
    private void rebuildPlan() {
        HashMap<String, List<ThingWithDistance>> jewelListSortedByDistanceGroupedByColor = new HashMap<>();
        HashMap<String, Integer> jewelsNeededGroupedByColor = new HashMap<>();
        plan.clear();
        // build a map of all available things, sorted by distance and grouped by color
        for (Thing thing: thingsMemory) {
            if (thing.getCategory() == Constants.categoryJEWEL) {
                String color = Constants.getColorName(thing.getMaterial().getColor());
                List<ThingWithDistance> sortedList = jewelListSortedByDistanceGroupedByColor.get(color);
                if (sortedList == null) {
                    sortedList = new ArrayList<>();
                }
                double distance = c.calculateDistanceTo(thing);
                if (distance <= 30) {
                    // something is probably wrong - the object must be gone
                    continue;
                }
                ThingWithDistance thingWithDistance = new ThingWithDistance(thing, distance);
                sortedList.add(thingWithDistance);
                sortedList.sort(Comparator.comparing(ThingWithDistance::getDistance));
                jewelListSortedByDistanceGroupedByColor.put(color, sortedList);
            }
        }
        // build a map of all jewels needed, with total quantity grouped by color
        jewelsNeededGroupedByColor.put("Red", 0);
        jewelsNeededGroupedByColor.put("Green", 0);
        jewelsNeededGroupedByColor.put("Blue", 0);
        jewelsNeededGroupedByColor.put("Yellow", 0);
        jewelsNeededGroupedByColor.put("Magenta", 0);
        jewelsNeededGroupedByColor.put("White", 0);
        int leafletsCount = 0;
        for (Leaflet l : c.getLeaflets()) {
            HashMap<String, Integer[]> items = l.getItems();
            for (HashMap.Entry<String, Integer[]> entry : items.entrySet()) {
                String color = entry.getKey();
                Integer needed = entry.getValue()[0];
                Integer collected = entry.getValue()[1];
                Integer previousNeeded = jewelsNeededGroupedByColor.get(color);
                if (previousNeeded != null) {
                    needed += previousNeeded;
                }
                jewelsNeededGroupedByColor.put(color, needed - collected);
            }
            leafletsCount++;
            if (leafletsCount == 3) {
                break;
            }
        }
        // build a map of nearest jewels needed to complete all leaflets
        List<ThingWithDistance> sortedListOfNeededThings = new ArrayList<>();
        for (String color: jewelsNeededGroupedByColor.keySet()) {
            int needed = jewelsNeededGroupedByColor.get(color);
            List<ThingWithDistance> availableJewels = jewelListSortedByDistanceGroupedByColor.get(color);
            if (availableJewels == null) {
                continue;
            }
            if (needed > 0) {
                for (int i = 0; (i < needed) && (i < availableJewels.size()); i++) {
                    sortedListOfNeededThings.add(availableJewels.get(i));
                    sortedListOfNeededThings.sort(Comparator.comparing(ThingWithDistance::getDistance));
                }
            }
        }
        // build the plan
        for (ThingWithDistance thingWithDistance: sortedListOfNeededThings) {
            Thing thing = thingWithDistance.getThing();
            plan.add(thing.getCenterPosition());
        }
        // TODO check if there is any obstacle between the creature and the first point and calculate intermediary steps
    }

}
