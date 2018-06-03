package myagent.featuredetectors;

import java.util.HashMap;
import java.util.Map;

import edu.memphis.ccrg.lida.pam.tasks.BasicDetectionAlgorithm;

/**
 * White screen feature detector
 * @author Ricardo Andrade
 */
public class WhiteScreenFeatureDetector extends BasicDetectionAlgorithm {
	
    public static final int TOLERANCE = 5;
    
    /*
     * The white background color is going to be detected
     */
    private int backgroundColor = 0xFFFFFFFF;

    private Map<String, Object> smParams = new HashMap<String, Object>();
    
    /*
     * The square in the environment is 20x20 = 400 pixels.  Thus it takes up ~40% of the area.
     * The circle has radius = 10 so its area ~= 314 pixels.  Thus it takes up ~31% of the area.
     */
    @Override
    public void init() {
       super.init();
       smParams.put("mode","all");

       backgroundColor = (Integer) getParam("backgroundColor", 0xFFFFFFFF);
    }

    @Override
    public double detect() {
        int[] layer = (int[]) sensoryMemory.getSensoryContent("visual",smParams);
        for(int i=0;i<layer.length;i++){
            if(layer[i]==backgroundColor){
                return 1.0;
            }
        }
        return 0.0;
    }
}
