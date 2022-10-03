package model.ga;

import java.util.ArrayList;
import java.util.Collections;
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
        
        generatePopulation();
    }
    
    public void envUpdated() {
        population = evaluatePopulation(population);
    }
    
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
        population = new ArrayList<>();
        generatePopulation();
        
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }
    
    private void generatePopulation() {
        for(int i = 0; i  < populationSize; i++) {
            population.add(new Pair(null, 0));
            population.get(i).a = new int[env.getSolLen()];
            for(int j = 0; j < env.getSolLen(); j++) {
                population.get(i).a[j] = (int)(Math.random() * 600);
            }
        }
    }
    
    public ArrayList<Pair<int[], Integer>> select() {
        Collections.shuffle(population);
        //population.sort((Pair<int[], Integer> p1, Pair<int[], Integer> p2) -> p2.b - p1.b);
        ArrayList<Pair<int[], Integer>> selected = new ArrayList<>();
        
        for(int i = 0; i < populationSize / 2; i++) {
            Pair<int[], Integer> p1 = population.remove(0); //chooseForTournament(population.size()));
            Pair<int[], Integer> p2 = population.remove(0); //chooseForTournament(population.size()));
            selected.add( (p1.b > p2.b) ? p1 : p2);
        }
        
        return selected;
    }
    
    private int chooseForTournament(int len) {
        float p = 0.4f;
        float f = p;
        for(int i = 0; i < len; i++) {
            if(Math.random() < f)return i;
            f = f * (1 - p);
        }
        return 0;
    }
    
    public ArrayList<Pair<int[], Integer>> crossover(ArrayList<Pair<int[], Integer>> selected) {
        for(int i = 0; i < populationSize / 2; i++) {
            Pair<int[], Integer> parent1 = selected.get(i);
            Pair<int[], Integer> parent2 = selected.get(i + 1);
            
            int[] crossoverSol = new int[env.getSolLen()];
            for(int j = 0; j < env.getSolLen() / 2; j++) {
                if(Math.random() > 0.5) {
                    crossoverSol[2*j] = parent1.a[j*2];
                    crossoverSol[2*j + 1] = parent1.a[2*j + 1];
                } else  {
                    crossoverSol[2*j] = parent2.a[j*2];
                    crossoverSol[2*j + 1] = parent2.a[2*j + 1];
                }
            }
            
            selected.add(new Pair(crossoverSol, 0));
        }
        
        return selected;
    }
    
    public ArrayList<Pair<int[], Integer>> mutate(ArrayList<Pair<int[], Integer>> population) {
        for(int i = 0; i < population.size(); i++) {
            for(int j = 0; j < env.getSolLen(); j++) {
                if(Math.random() > mutationRate)continue;
                int maxDev = 100 /(genCounter/2000 + 1);
                population.get(i).a[j] += (int)(Math.random() * maxDev - maxDev / 2); 
            }
            
            
            
        }
        
        return population;
    }
    
    public ArrayList<Pair<int[], Integer>> evaluatePopulation(ArrayList<Pair<int[], Integer>> population) {
         for(int i = 0; i < populationSize; i++) {
            population.get(i).b = env.eval(population.get(i).a);
        }
         
        return population;
    }
    
    public void performGeneration() {
       ArrayList<Pair<int[], Integer>> pop = select();
       pop = crossover(pop);
       pop = mutate(pop);
       pop = evaluatePopulation(pop);
       pop.sort((Pair<int[], Integer> p1, Pair<int[], Integer> p2) -> p2.b - p1.b);
       genCounter++;
       population = pop;
    }
    
    public Pair<int[], Integer> getBest() {
        return population.get(0);
    }
    
    public Pair<int[], Integer> getWorst() {
        return population.get(population.size() - 1);
    }
    
    public Pair<int[], Integer> getMedian() {
        return population.get(population.size() / 2);
    }
    
    public int getCurrentGeneration() {
        return genCounter;
    } 
    
}
