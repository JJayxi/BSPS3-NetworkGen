package model.analysis;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import model.NodeStep;
import java.util.Scanner;

public class SensitivityAnalysis {
    
    private static NodeStep nodestep;
    private static int samples = 20, genNumber = 300;
    
    private static void samplePrint() {
	int averagescore = 0;
	for(int i = 0; i < samples; i++) {
	    for(int j = 0; j < genNumber; j++) {
		nodestep.getGA().performGeneration();
	    }
	    averagescore += nodestep.getGA().getBest().b / samples;
	    nodestep.getGA().reset();
	}
	System.out.print(averagescore);
    }

    
    public static void main(String[] args) {
	BufferedImage img = null;
        try {
            img = ImageIO.read(new File("resource/testmap.png"));
        } catch (Exception e) {}
	
	/*
	BufferedImage img, int nodeRadius, int nodeNumber, int proximityPenalty,
        int populationSize, double crossoverRate, double mutationRate
	*/
	nodestep = new NodeStep(img, 50, 10, 10, 50, 0.5, 0.02);
	
	Scanner scanner = new Scanner(System.in);
	
	
	
	while(true) {
	    //<editor-fold defaultstate="collapsed" desc="change parameters ">
	    while(true) {
		System.out.print("Change parameter (1. yes, 2. no):");
		
		if(scanner.nextInt() == 1) {
		    System.out.println("Choose parameter to change:");
		    System.out.println("1. node radius");
		    System.out.println("2. number of nodes");
		    System.out.println("3. proximity penalty");
		    System.out.println("4. population size");
		    System.out.println("5. crossover rate");
		    System.out.println("6. mutation rate");
		    switch (scanner.nextInt()) {
			case 1:
			    System.out.print("Desired node radius (Default 50):");
			    nodestep.setNodeRadius(scanner.nextInt());
			    break;
			case 2:
			    System.out.print("Desired number of nodes(Default 10):");
			    nodestep.setNodeNumber(scanner.nextInt());
			    break;
			case 3:
			    System.out.print("Desired proximity penalty (Default 100):");
			    nodestep.setProximityPenalty(scanner.nextInt());
			    break;
			case 4:
			    System.out.print("Desired population size (Default 50):");
			    nodestep.setPopulationSize(scanner.nextInt());
			    break;
			case 5:
			    System.out.print("Desired crossover rate(Default 0.5):");
			    nodestep.setCrossoverRate(scanner.nextDouble());
			    break;
			case 6:
			    System.out.print("Desired mutation rate (Default 0.02):");
			    nodestep.setMutationRate(scanner.nextDouble());
			    break;
		    }
		    
		    System.out.println(nodestep);
		    
		} else break;
		

	    }
	    //</editor-fold>
	    
	    
	    //<editor-fold defaultstate="collapsed" desc="change number of samples and generations per sample">
	    boolean breakloop = true;
	    while(breakloop) {
		System.out.print("Change analysis parameter (1. number of samples, 2. number of generations per sample, 3. no):");
		switch(scanner.nextInt()) {
		    case 1:
			System.out.print("Desired number of samples (Default: 30): ");
			samples = scanner.nextInt();
			break;
		    case 2:
			System.out.print("Desired number of generations (Default: 2000): ");
			genNumber = scanner.nextInt();
			break;
		    case 3:
			breakloop = false;
			break;
		}
		System.out.println("Number of samples: " + samples + ", number of generations per sample: " + genNumber);
	    }
	    //</editor-fold>
	    	
	    System.out.println("Parameter to analyse:");
	    System.out.println("1. Population size");
	    System.out.println("2. Crossover rate");
	    System.out.println("3. Mutation rate");
	    switch(scanner.nextInt()) {
		case 1: {
		    int beforean = nodestep.getGA().getPopulationSize();
		    System.out.print("Starting value:");
		    int startval = scanner.nextInt();
		    System.out.print("End value: ");
		    int endval = scanner.nextInt();
		    System.out.print("Number of steps:");
		    int nsteps = scanner.nextInt();
		    
		    double dstep = (endval - startval) / (double)nsteps;
		    for(int i = 0; i < nsteps; i++) {
			int v = startval + (int)(dstep * i);
			nodestep.setPopulationSize(v);
			System.out.print(v + ",");
			samplePrint();
			System.out.println();
		    }
		    nodestep.setPopulationSize(beforean);
		    break; }
		case 2: {
		    double beforean = nodestep.getGA().getCrossoverRate();
		    System.out.print("Starting value:");
		    double startval = scanner.nextDouble();
		    System.out.print("End value: ");
		    double endval = scanner.nextDouble();
		    System.out.print("Number of steps:");
		    int nsteps = scanner.nextInt();
		    
		    double dstep = (endval - startval) / nsteps;
		    for(int i = 0; i < nsteps; i++) {
			double v = startval + dstep * i;
			nodestep.setCrossoverRate(v);
			System.out.print(v + ",");
			samplePrint();
			System.out.println();
		    }
		    nodestep.setCrossoverRate(beforean);
		    break; }
		case 3: {
		    double beforean = nodestep.getGA().getMutationRate();
		    System.out.print("Starting value:");
		    double startval = scanner.nextDouble();
		    System.out.print("End value: ");
		    double endval = scanner.nextDouble();
		    System.out.print("Number of steps:");
		    int nsteps = scanner.nextInt();
		    
		    double dstep = (endval - startval) / nsteps;
		    for(int i = 0; i < nsteps; i++) {
			double v = startval + dstep * i;
			nodestep.setMutationRate(v);
			System.out.print(v + ",");
			samplePrint();
			System.out.println();
		    }
		    nodestep.setMutationRate(beforean);
		    break; }
	    }
	}
    }
}