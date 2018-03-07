/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.CommandExecException;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;
import ws3dproxy.model.World;

/**
 *
 * @author nbt
 */
public class WorldProxy {
    protected static WorldProxy instance;
    
    protected WS3DProxy proxy;
    
    public WorldProxy() {
        getProxy();
        try {
            World w = World.getInstance();
            w.reset();
        } catch (CommandExecException e) {
            System.out.println("Erro capturado");
        }
    }
    
    public static Creature createCreature(double x, double y, double pitch) {
        Creature c = null;
        try {
            c = getInstance().getProxy().createCreature(x, y, pitch);
            c.start();
        } catch (CommandExecException ex) {
            Logger.getLogger(WorldProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }
    
    public static WorldProxy getInstance() {
        if (instance == null)
            instance = new WorldProxy();
        return instance;
    }

    private WS3DProxy getProxy() {
        if (proxy == null)
            proxy = new WS3DProxy();
        return proxy;
    }
}
