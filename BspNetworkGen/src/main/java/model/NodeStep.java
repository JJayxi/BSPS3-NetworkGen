package model;

import java.awt.image.BufferedImage;
import model.ga.GeneticAlgorithm;
import model.gaenv.EnvDisplay;
import model.gaenv.Environment;

public class NodeStep {
    private EnvDisplay envDisplay;
    private Environment env;
    private GeneticAlgorithm ga;
    
    public NodeStep(BufferedImage img, int nodeRadius, int nodeNumber, int proximityPenalty,
                    int populationSize, float crossoverRate, float mutationRate) {
        envDisplay = new EnvDisplay(img, nodeRadius);
        env = new Environment(nodeNumber, nodeRadius, proximityPenalty, img);
        ga = new GeneticAlgorithm(populationSize, crossoverRate, mutationRate, env);
    }
    
    public void setMap(BufferedImage img) {
        env.setMap(img);
        envDisplay.setMap(img);
        ga.envUpdated();
    }
    public void setNodeRadius(int nodeRadius) {
        envDisplay.setNodeRadius(nodeRadius);
        env.setNodeRadius(nodeRadius);
    }
    
    public void setNodeNumber(int nodeNumber) {
        env.setNumNodes(nodeNumber);
    }
    
    public void setProximityPenalty(int proximityPenalty) {
        env.setProxPenalty(proximityPenalty);
    }
    
    public void setPopulationSize(int populationSize) {
        ga.setPopulationSize(populationSize);
    }
    
    public void setCrossoverRate(double crossoverRate) {
        ga.setCrossoverRate(crossoverRate);
    }
    
    public void setMutationRate(double mutationRate) {
        ga.setMutationRate(mutationRate);
    }
    
    public EnvDisplay getEnvDisplay() {
        return envDisplay;
    }
    
    public GeneticAlgorithm getGA() {
        return ga;
    }
}
