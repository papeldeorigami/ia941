package codelets.behaviors;

import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import ws3dproxy.CommandExecException;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;
import ws3dproxy.model.World;

/**
 *
 * @author Ricardo Andrade <papeldeorigami@googlemail.com>
 */
public class DeliverTest {
    
    @Test
    public void testCheckAllLeafletsComplete() {
        System.out.println("checkAllLeafletsComplete");
        Deliver instance = new Deliver();
        WS3DProxy proxy = new WS3DProxy();
        World w = World.getInstance();
        try {
            w.reset();
            Creature c = proxy.createCreature();
            c.updateState();
            instance.checkAllLeafletsComplete(c.getLeaflets());
            w.reset();
        } catch (CommandExecException ex) {
            Logger.getLogger(DeliverTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
