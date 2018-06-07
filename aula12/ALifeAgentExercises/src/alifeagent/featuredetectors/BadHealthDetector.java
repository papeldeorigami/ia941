/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alifeagent.featuredetectors;

import java.util.HashMap;
import java.util.Map;

import edu.memphis.ccrg.lida.pam.tasks.BasicDetectionAlgorithm;

/**
 *
 * @author Ricardo Andrade <papeldeorigami@googlemail.com>
 */
public class BadHealthDetector extends BasicDetectionAlgorithm {

    private final String modality = "";
    private Map<String, Object> detectorParams = new HashMap<String, Object>();

    @Override
    public void init() {
        super.init();
        detectorParams.put("mode", "health");
    }

    @Override
    public double detect() {
        double healthValue = (Double) sensoryMemory.getSensoryContent(modality, detectorParams);
        double activation = 0.0;
        if (healthValue <= 0.33) {
            activation = 1.0;
        }
        return activation;
    }
}
