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
package service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import ws3dproxy.model.Creature;

/**
 *
 * @author Ricardo Andrade <papeldeorigami@googlemail.com>
 */
public class WorldProxyTest {
    
    /**
     * Test of getInstance method, of class WorldProxy.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        WorldProxy result = WorldProxy.getInstance();
        assertNotNull(result);
    }
    
    /**
     * Test of createCreature method, of class WorldProxy.
     */
    @Test
    public void testCreateCreature() {
        System.out.println("createCreature");
        double x = 0.0;
        double y = 0.0;
        double pitch = 0.0;
        Creature result = WorldProxy.createCreature(x, y, pitch);
        assertNotNull(result);
    }

}
