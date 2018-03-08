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

import org.junit.Test;
import static org.junit.Assert.*;
import ws3dproxy.CommandExecException;
import ws3dproxy.model.Creature;

/**
 *
 * @author Ricardo Andrade <papeldeorigami@googlemail.com>
 */
public class WorldFacadeTest {
    
    @Test
    public void testGetCreature() {
        System.out.println("getCreature");
        Creature result;
        try {
            (new WorldFacade()).getCreature();
        } catch (CommandExecException ex) {
            fail();
        }
    }
    
    @Test
    public void testMoveCreatureLeft() {
        System.out.println("moveCreatureLeft");
        try {
            (new WorldFacade()).moveCreatureLeft();
        } catch (CommandExecException ex) {
            fail();
        }
    }

}
