package model.analysis;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import model.NodeStep;
import java.util.Scanner;

public class SensitivityAnalysis {

    private static NodeStep nodestep;
    private static int samples = 100;
    private static double mutationRate = 0.03, crossoverRate = 0.8;
    private static int generationNumber = 1000, populationCount = 100;

    private static double delta_p = 0.5;

    private static int F() {
        nodestep.setMutationRate(mutationRate);
        nodestep.setCrossoverRate(crossoverRate);
        nodestep.setPopulationSize(populationCount);

        int averagescore = 0;
        for (int i = 0; i < samples; i++) {
            for (int j = 0; j < generationNumber; j++) {
                nodestep.getGA().performGeneration();
            }
            averagescore += nodestep.getGA().getBest().b / samples;
            nodestep.getGA().reset();
        }
        return averagescore;
    }

    private static double sensitivityM(double mr) {
        double temp = mutationRate;
        mutationRate = mr;
        double delta = temp * delta_p;
        int f = F();
        mutationRate = mr + delta;
        int fd = F();

        double sensitivity = (fd - f) / delta;
        mutationRate = temp;
        return sensitivity;
    }

    private static double sensitivityC(double cr) {
        double temp = crossoverRate;
        crossoverRate = cr;
        double delta = temp * delta_p;
        int f = F();
        crossoverRate = cr + delta;
        int fd = F();

        double sensitivity = (fd - f) / delta;
        crossoverRate = temp;
        return sensitivity;
    }

    private static double sensitivityP(int p) {
        int temp = populationCount;
        populationCount = p;
        int delta = (int) (temp * delta_p);
        int f = F();
        populationCount = p + delta;
        int fd = F();

        double sensitivity = (fd - f) / delta;
        populationCount = temp;
        return sensitivity;
    }

    private static double sensitivityG(int g) {
        int temp = generationNumber;
        generationNumber = g;
        int delta = (int) (temp * delta_p);
        int f = F();
        generationNumber = g + delta;
        int fd = F();

        double sensitivity = (fd - f) / delta;
        generationNumber = temp;
        return sensitivity;
    }

    private static void samplePrint() {
        System.out.print(F());
    }

    private static void changeVar(double to, int vari) {
        switch (vari) {
            case 0:
                populationCount = (int) to;
                break;
            case 1:
                generationNumber = (int) to;
                break;
            case 2:
                crossoverRate = to;
                break;
            case 3:
                mutationRate = to;
                break;
        }
    }

    private static double analyseVar(double val, int vari) {
        switch (vari) {
            case 0:
                int temp = populationCount;
                populationCount = (int) val;
                double score = F();
                populationCount = temp;
                return score;
            case 1:
                temp = generationNumber;
                generationNumber = (int) val;
                score = F();
                generationNumber = temp;
                return score;
            case 2:
                double temp2 = crossoverRate;
                crossoverRate = val;
                score = F();
                crossoverRate = temp2;
                return score;
            case 3:
                temp2 = mutationRate;
                mutationRate = val;
                score = F();
                mutationRate = temp2;
                return score;
        }
        return 0;
    }

    public static void main(String[] args) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("resource/france/templatemap_quartersize.png"));
        } catch (Exception e) {
        }

        /*
	BufferedImage img, int nodeRadius, int nodeNumber, int proximityPenalty,
        int populationSize, double crossoverRate, double mutationRate
         */
        nodestep = new NodeStep(img, 25, 20, 40, 200, 0.8, 0.02);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            //<editor-fold defaultstate="collapsed" desc="change parameters ">
            while (true) {
                System.out.print("Change parameter (1. yes, 2. no):");

                if (scanner.nextInt() == 1) {
                    System.out.println("Choose parameter to change:");
                    System.out.println("1. node radius");
                    System.out.println("2. number of nodes");
                    System.out.println("3. proximity penalty");
                    System.out.println("4. population size");
                    System.out.println("5. crossover rate");
                    System.out.println("6. mutation rate");
                    switch (scanner.nextInt()) {
                        case 1:
                            System.out.print("Desired node radius:");
                            nodestep.setNodeRadius(scanner.nextInt());
                            break;
                        case 2:
                            System.out.print("Desired number of nodes:");
                            nodestep.setNodeNumber(scanner.nextInt());
                            break;
                        case 3:
                            System.out.print("Desired proximity penalty:");
                            nodestep.setProximityPenalty(scanner.nextInt());
                            break;
                        case 4:
                            System.out.print("Desired population size:");
                            nodestep.setPopulationSize(scanner.nextInt());
                            break;
                        case 5:
                            System.out.print("Desired crossover rate:");
                            nodestep.setCrossoverRate(scanner.nextDouble());
                            break;
                        case 6:
                            System.out.print("Desired mutation rate:");
                            nodestep.setMutationRate(scanner.nextDouble());
                            break;
                    }

                    System.out.println(nodestep);

                } else {
                    break;
                }

            }
            //</editor-fold>

            //System.out.println("Parameter to analyse:");
            //System.out.println("1. Population size");
            //System.out.println("2. Generation number");
            //System.out.println("3. Crossover rate");
            //System.out.println("4. Mutation rate");
            //int param_analyse = scanner.nextInt() - 1;
            //System.out.println("Enter the value at which you wish to analyse this parameter:");
            //double valueAnalyseAt = scanner.nextDouble();
            System.out.println("Parameter to change:");
            System.out.println("1. Population size");
            System.out.println("2. Generation number");
            System.out.println("3. Crossover rate");
            System.out.println("4. Mutation rate");
            int param_change = scanner.nextInt() - 1;

            System.out.print("Starting value:");
            double startval = scanner.nextDouble();
            System.out.print("End value: ");
            double endval = scanner.nextDouble();
            System.out.print("Number of steps:");
            int nsteps = scanner.nextInt();

            double dstep = (endval - startval) / nsteps;

            for (int i = 0; i < nsteps; i++) {
                double v = startval + (dstep * i);
                changeVar(v, param_change);
                double sensitivity = F();//analyseVar(valueAnalyseAt, param_analyse);
                System.out.println(v + " " + sensitivity);
            }
        }
        
    }
}
