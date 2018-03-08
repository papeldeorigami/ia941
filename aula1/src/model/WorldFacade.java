/*
 * Copyright (C) 2018 Ricardo Andrade <papeldeorigami@googlemail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package model;

import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.CommandExecException;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;

/**
 *
 * @author Ricardo Andrade <papeldeorigami@googlemail.com>
 */
public class WorldFacade {
    
    protected WS3DProxy proxy;
    
    private Creature creature;
    
    public WorldFacade() {
        getProxy();
        try {
            ws3dproxy.model.World w = ws3dproxy.model.World.getInstance();
            w.reset();
        } catch (CommandExecException ex) {
            Logger.getLogger(WorldFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private WS3DProxy getProxy() {
        if (proxy == null)
            proxy = new WS3DProxy();
        return proxy;
    }

    public Creature getCreature() throws CommandExecException {
        if (creature == null)
            creature = getProxy().createCreature(100, 100, 90);
        return creature;
    }

    public void showMindWindow() throws CommandExecException {
        getCreature().addMindWindow();
    }

    public void moveCreatureLeft() throws CommandExecException {
        getCreature().move(.1, .1, .1);
    }
    
}
