/*****************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *    Klaus Raizer, Andre Paraense, Ricardo Ribeiro Gudwin
 *****************************************************************************/

import ws3dproxy.CommandExecException;
import ws3dproxy.CommandUtility;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;
import ws3dproxy.model.World;

/**
 *
 * @author rgudwin
 */
public class Environment {
    
    public static final int INITIAL_DESTINATION_X = 650;
    public static final int INITIAL_DESTINATION_Y = 450;
    
    public String host="localhost";
    public int port = 4011;
    public String robotID="r0";
    public Creature c = null;
    
    public Environment(int scenario) {
          WS3DProxy proxy = new WS3DProxy();
          try {   
             World w = World.getInstance();
             w.reset();
//             World.createFood(0, 350, 75);
//             World.createFood(0, 100, 220);
//             World.createFood(0, 250, 210);
             prepareScenario(scenario);
             c = proxy.createCreature(100,450,0);
             c.start();
             
             //c.setRobotID("r0");
             //c.startCamera("r0");
             
             
          } catch (CommandExecException e) {
              
          }
          System.out.println("Robot "+c.getName()+" is ready to go.");
		


	}

    private void prepareScenario(int scenario) throws CommandExecException {
        if (scenario == 0) {
            collectJewelsScenario();
        } else {
            mazeScenario();            
        }
    }

    private void collectJewelsScenario() throws CommandExecException {
        CommandUtility.sendNewBrick(4,747.0,2.0,800.0,567.0);
        CommandUtility.sendNewBrick(4,50.0,-4.0,747.0,47.0);
        CommandUtility.sendNewBrick(4,49.0,562.0,796.0,599.0);
        CommandUtility.sendNewBrick(4,-2.0,6.0,50.0,599.0);            
        // Create 9 jewels of each color, to enable easy planning
        CommandUtility.sendNewJewel(0,200.0,100.0);
        CommandUtility.sendNewJewel(0,200.0,220.0);
        CommandUtility.sendNewJewel(0,200.0,440.0);
        CommandUtility.sendNewJewel(0,420.0,100.0);
        CommandUtility.sendNewJewel(0,420.0,220.0);
        CommandUtility.sendNewJewel(0,420.0,440.0);
        CommandUtility.sendNewJewel(0,640.0,100.0);
        CommandUtility.sendNewJewel(0,640.0,220.0);
        CommandUtility.sendNewJewel(0,640.0,440.0);
        CommandUtility.sendNewJewel(1,140.0,140.0);
        CommandUtility.sendNewJewel(1,140.0,340.0);
        CommandUtility.sendNewJewel(1,140.0,500.0);
        CommandUtility.sendNewJewel(1,340.0,220.0);
        CommandUtility.sendNewJewel(1,340.0,340.0);
        CommandUtility.sendNewJewel(1,340.0,500.0);
        CommandUtility.sendNewJewel(1,600.0,140.0);
        CommandUtility.sendNewJewel(1,600.0,340.0);
        CommandUtility.sendNewJewel(1,600.0,500.0);
        CommandUtility.sendNewJewel(2,250.0,170.0);
        CommandUtility.sendNewJewel(2,250.0,240.0);
        CommandUtility.sendNewJewel(2,250.0,400.0);
        CommandUtility.sendNewJewel(2,440.0,170.0);
        CommandUtility.sendNewJewel(2,440.0,240.0);
        CommandUtility.sendNewJewel(2,440.0,400.0);
        CommandUtility.sendNewJewel(2,530.0,170.0);
        CommandUtility.sendNewJewel(2,530.0,240.0);
        CommandUtility.sendNewJewel(2,530.0,400.0);

        CommandUtility.sendNewJewel(3,260.0,100.0);
        CommandUtility.sendNewJewel(3,260.0,220.0);
        CommandUtility.sendNewJewel(3,260.0,440.0);
        CommandUtility.sendNewJewel(3,500.0,100.0);
        CommandUtility.sendNewJewel(3,500.0,220.0);
        CommandUtility.sendNewJewel(3,480.0,440.0);
        CommandUtility.sendNewJewel(3,700.0,100.0);
        CommandUtility.sendNewJewel(3,700.0,220.0);
        CommandUtility.sendNewJewel(3,700.0,440.0);
        CommandUtility.sendNewJewel(4,200.0,140.0);
        CommandUtility.sendNewJewel(4,200.0,340.0);
        CommandUtility.sendNewJewel(4,200.0,500.0);
        CommandUtility.sendNewJewel(4,400.0,220.0);
        CommandUtility.sendNewJewel(4,400.0,340.0);
        CommandUtility.sendNewJewel(4,400.0,500.0);
        CommandUtility.sendNewJewel(4,660.0,140.0);
        CommandUtility.sendNewJewel(4,660.0,340.0);
        CommandUtility.sendNewJewel(4,660.0,500.0);
        CommandUtility.sendNewJewel(5,310.0,170.0);
        CommandUtility.sendNewJewel(5,310.0,240.0);
        CommandUtility.sendNewJewel(5,310.0,400.0);
        CommandUtility.sendNewJewel(5,500.0,170.0);
        CommandUtility.sendNewJewel(5,500.0,240.0);
        CommandUtility.sendNewJewel(5,500.0,400.0);
        CommandUtility.sendNewJewel(5,590.0,170.0);
        CommandUtility.sendNewJewel(5,590.0,240.0);
        CommandUtility.sendNewJewel(5,590.0,400.0);

        World.createFood(0, 350, 75);
        World.createFood(0, 100, 220);
        World.createFood(0, 250, 210);
    }

    private void mazeScenario() throws CommandExecException {
        int HALL_WIDTH = 100;
        int BLOCK_WIDTH = 40;
        World.createBrick(2, HALL_WIDTH, 0, HALL_WIDTH + BLOCK_WIDTH, 400);
        World.createBrick(3, 2 * HALL_WIDTH + 2 * BLOCK_WIDTH, 200, 2 * HALL_WIDTH + 3 * BLOCK_WIDTH, 600);
        World.createBrick(4, 3 * HALL_WIDTH + 3 * BLOCK_WIDTH, 0, 3 * HALL_WIDTH + 4 * BLOCK_WIDTH, 280);
        World.createBrick(4, 3 * HALL_WIDTH + 3 * BLOCK_WIDTH, 400, 3 * HALL_WIDTH + 4 * BLOCK_WIDTH, 600);
        World.createBrick(4, 4 * HALL_WIDTH + 4 * BLOCK_WIDTH, 300, 4 * HALL_WIDTH + 5 * BLOCK_WIDTH, 340);
        World.createBrick(4, 5 * HALL_WIDTH + 5 * BLOCK_WIDTH, 200, 5 * HALL_WIDTH + 6 * BLOCK_WIDTH, 400);
        World.createFood(0, INITIAL_DESTINATION_X, INITIAL_DESTINATION_Y);
    }
}
