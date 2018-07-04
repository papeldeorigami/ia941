/*
 * Copyright (C) 2018 Ricardo Andrade <papeldeorigami@googlemail.com>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package support;

import java.util.List;
import memory.CreatureInnerSense;
import org.xguzm.pathfinding.grid.GridCell;
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

/**
 *
 * @author Ricardo Andrade <papeldeorigami@googlemail.com>
 */
public class Util {

    static public double calculateDistance(double x1, double y1, double x2, double y2) {
        return (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
    }

    static public boolean jewelIsNecessaryForSomeLeaflet(Thing t, CreatureInnerSense cis) {
        List<Leaflet> leaflets = cis.leaflets;
        for (Leaflet leaflet : leaflets) {
            if (leaflet.getMissingNumberOfType(t.getAttributes().getColor()) > 0) {
                return true;
            }
        }
        return false;
    }

    static public double oppositeDirectionTo(WorldPoint creaturePosition, double creaturePitch, Thing thing) {
        WorldPoint thingPosition = thing.getCenterPosition();
        if (creaturePitch >= 0) {
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

    public static boolean distanceToThingLessThan(Thing t, WorldPoint position, double distance) {
        int x = (int) position.getX();
        int y = (int) position.getY();
        int thingLeft = (int) (Math.min(t.getX1(), t.getX2()) - distance);
        int thingRight = (int) (Math.max(t.getX1(), t.getX2()) + distance) - 1;
        int thingTop = (int) (Math.min(t.getY1(), t.getY2()) - distance);
        int thingBottom = (int) (Math.max(t.getY1(), t.getY2()) + distance) - 1;
        return (x >= thingLeft && x <= thingRight && y >= thingTop && y <= thingBottom);
    }

    static String worldPointToString(WorldPoint p) {
        if (p == null) {
            return "";            
        }
        return String.valueOf(Math.round(p.getX())) + ", " + String.valueOf(Math.round(p.getY()));
    }

    static String gridCellToString(GridCell c) {
        if (c == null) {
            return "";            
        }
        return String.valueOf(String.valueOf(c.getX()) + ", " + String.valueOf(c.getY()));
    }
}
