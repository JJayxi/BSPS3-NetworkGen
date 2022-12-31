package model.ga;

import java.util.ArrayList;
import model.gaenv.GAEnv;

public class GeneticAlgorithm {
    
    public class Pair<A, B> {
        public A a;
        public B b;
        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }
    
    private int populationSize;
    private ArrayList<Pair<int[], Integer>> population;
    private double crossoverRate;
    private double mutationRate;
    private GAEnv env;
    private int genCounter = 0;
    
    
    
    public GeneticAlgorithm(int populationSize, double crossoverRate, double mutationRate, GAEnv env) {
        this.populationSize = populationSize;
        population = new ArrayList<>();
        this.env = env;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        
        generatePopulation(env.getMaxValue());
    }

    public int getPopulationSize() {
	return populationSize;
    }

    public double getCrossoverRate() {
	return crossoverRate;
    }

    public double getMutationRate() {
	return mutationRate;
    }
    
    
    
    public void reset() {
        population = new ArrayList<>();
        generatePopulation(env.getMaxValue());
        genCounter = 0; 
    }
    
    public void envUpdated() {
        evaluatePopulation(population);
    }
    
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        population = new ArrayList<>();
        generatePopulation(env.getMaxValue());
        
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }
    
    private void generatePopulation(int maxval) {
        for(int i = 0; i  < populationSize; i++) {
            population.add(new Pair(null, 0));
            population.get(i).a = new int[env.getSolLen()];
            for(int j = 0; j < env.getSolLen(); j++) {
                population.get(i).a[j] = (int)(Math.random() * maxval);
            }
        }
    }
    
    public ArrayList<Pair<int[], Integer>> select() {
        //Collections.shuffle(population);
        //population.sort((Pair<int[], Integer> p1, Pair<int[], Integer> p2) -> p2.b - p1.b);
        ArrayList<Pair<int[], Integer>> selected = new ArrayList<>();
        
        for(int i = 0; i < crossoverRate * populationSize; i++) {
            Pair<int[], Integer> p1 = population.get((int)(Math.random() * populationSize)); //chooseForTournament(population.size()));
            Pair<int[], Integer> p2 = population.get((int)(Math.random() * populationSize)); //chooseForTournament(population.size()));
            
            selected.add(new Pair<>((p1.b > p2.b) ? p1.a.clone() : p2.a.clone(), 0));
        }
        
        return selected;
    }
    
    private int[] selectAgent(ArrayList<Pair<int[], Integer>> population) {
        Pair<int[], Integer> p1 = population.get((int)(Math.random() * populationSize)); //chooseForTournament(population.size()));
        Pair<int[], Integer> p2 = population.get((int)(Math.random() * populationSize)); //chooseForTournament(population.size()));
        if(p1.b > p2.b) return p1.a.clone();
        return p2.a.clone();
    }
    
    private int[] crossAgent(int[] parent1, int[] parent2) {
	int[] crossoverSol = new int[env.getSolLen()];
	int crossoverPoint = (int)(Math.random() * env.getSolLen());
	for(int j = 0; j < env.getSolLen(); j++) {
	    if(j >= crossoverPoint) 
		crossoverSol[j] = parent1[j];
	    else 
		crossoverSol[j] = parent2[j];
	}
	return crossoverSol;
    }
    
    public ArrayList<Pair<int[], Integer>> crossover(ArrayList<Pair<int[], Integer>> population) {
        ArrayList<Pair<int[], Integer>> crossed = new ArrayList<>();
        
        for(int i = 0; i < populationSize; i++) {
            if(Math.random() < crossoverRate) {
                int[] parent1 = selectAgent(population);
                int[] parent2 = selectAgent(population);
                int[] crossedAgent = crossAgent(parent1, parent2);
                crossed.add(new Pair(crossedAgent, 0));
            } else {
                crossed.add(new Pair(selectAgent(population), 0));            
            }
        }
        return crossed;
    }
    
    //private double lerp(double val1, double val2, double x) {
    //    return val1 * (1 - Math.min(x, 1)) + val2 * Math.min(x, 1);
    //}
    private int maxDev = 150;
    public ArrayList<Pair<int[], Integer>> mutate(ArrayList<Pair<int[], Integer>> population) {
        for(int i = 0; i < population.size(); i++) {
            for(int j = 0; j < env.getSolLen(); j++) {
                if(Math.random() > mutationRate)continue;
                int dev = (int)(Math.random() * maxDev - maxDev / 2);
                population.get(i).a[j] += dev; // (int)(Math.random() * 600); //+= dev;
                //if(env.verify(population.get(i).a)) continue;
                //population.get(i).a[j] -= dev; 
            }
            
            population.get(i).a = env.correct(population.get(i).a);
        }
        
        return population;
    }
    
    public void evaluatePopulation(ArrayList<Pair<int[], Integer>> population) {
        population.stream().parallel().forEach(
                (Pair<int[], Integer> p) -> {
                    p.b = env.eval(p.a);
                }
        );
    }
    
    public void performGeneration() {
	
        ArrayList<Pair<int[], Integer>> pop = crossover(population);
        pop = mutate(pop);
	evaluatePopulation(pop);
        pop.sort((p1, p2) -> p2.b - p1.b);
        genCounter++;
        population = pop;
    }
    
    public Pair<int[], Integer> getBest() {

        return population.stream().reduce(population.get(0), (p1, p2) -> p1.b > p2.b ? p1 : p2);
    }
    
    public int[] getBestSol() {
        return getBest().a;
    }
    
    public Pair<int[], Integer> getWorst() {
        return population.stream().reduce(population.get(0), (p1, p2) -> p1.b < p2.b ? p1 : p2);
    }
    
    public Pair<int[], Integer> getMedian() {
        return population.get(population.size() / 2);
    }
    
    public int getCurrentGeneration() {
        return genCounter;
    } 
    
}
