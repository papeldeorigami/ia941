/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Simulation;

import java.util.Random;
import java.util.logging.Logger;
import ws3dproxy.CommandUtility;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;
import ws3dproxy.model.World;

/**
 *
 * @author Danilo
 */
public class Environment
{
    Logger logger = Logger.getLogger(Environment.class.getName());
    WS3DProxy proxy = null;
    //SoarBridge soarBridge = null;
    Creature c = null;
    World w = null;

    public Environment(Boolean resetWorld)
    {
        proxy = new WS3DProxy();
        //proxy.connect();
        try {
        w = proxy.getWorld();
        int color = 1;
        if (resetWorld)
        {
            w.reset();
//            w.grow(1);            
            //CommandUtility.sendNewJewel(1,200.0,125.0);
            // Create Simulation Enviroment - Bricks
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
            color = 0;
        }

        c = proxy.createCreature(new Random().nextInt(600) + 100,new Random().nextInt(400) + 80,new Random().nextInt(2), color);
        c.start();

        } catch (Exception e) {
            logger.severe("Error in starting the Environment ");
            e.printStackTrace();
        }
    }
    
    public Creature getCreature() {
        return(c);
    }

    
}
