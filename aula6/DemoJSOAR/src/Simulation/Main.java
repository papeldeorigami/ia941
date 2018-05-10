/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Simulation;

import support.MindView;
import support.NativeUtils;
import SoarBridge.SoarBridge;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Danilo
 */
public class Main
{
    Logger logger = Logger.getLogger(Main.class.getName());
        
    private void SilenceLoggers() {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.jsoar")).setLevel(ch.qos.logback.classic.Level.OFF);
        Logger.getLogger("Simulation").setLevel(Level.SEVERE);
    }

    public Main() {
        SilenceLoggers();
        try
        {
            NativeUtils.loadFileFromJar("/solution.soar");
            String soarRulesPath = "rules/solution.soar";
            //NativeUtils.loadFileFromJar("/test.soar");
            //String soarRulesPath = "test.soar";
            //NativeUtils.loadFileFromJar("/soar-rules.soar");
            //String soarRulesPath = "soar-rules.soar";

            //Start enviroment data
            Environment e = new Environment(Boolean.TRUE);
            SoarBridge soarBridge = new SoarBridge(e,soarRulesPath,true);
            //SoarBridge soarBridge = new SoarBridge(e,soarRulesPath,false);
            MindView mv = new MindView(soarBridge);
            mv.setVisible(true);
            mv.startDebugState();

            // Run Simulation until some criteria was reached
            Thread.sleep(3000);

            while(true)
            {
                if (mv.getDebugState() == 0) {
                   soarBridge.step();
                   mv.set_input_link_text(soarBridge.input_link_string);
                   mv.set_output_link_text(soarBridge.output_link_string);
                }
                else e.c.updateState();
                Thread.sleep(100);                   
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
            ex.printStackTrace();
            logger.severe("Unknown error"+ex);
        }
    }

    public static void main(String[] args)
    {
        Main m = new Main();
    }


}
