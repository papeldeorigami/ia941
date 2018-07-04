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
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;

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

}
