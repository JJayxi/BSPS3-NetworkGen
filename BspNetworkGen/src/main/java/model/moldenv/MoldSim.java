package model.moldenv;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import model.Displayer;

public class MoldSim implements Displayer {

    class Vect {

        float x, y;

        public Vect(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Vect rotate(float angle) {
            return new Vect(
                    (float) (x * Math.cos(angle) - y * Math.sin(angle)),
                    (float) (x * Math.sin(angle) + y * Math.cos(angle))
            );
        }

        public Vect copy() {
            return new Vect(x, y);
        }

        public Vect setMag(float len) {
            float dr = len / (float) Math.sqrt(x * x + y * y);
            x *= dr;
            y *= dr;

            return this;
        }
    }

    private float wrap(float val, float a) {
        return (val + a) % a;
    }

    class Agent {

        public static final int agentWeight = -524288; ////(new Color(255, 0, 0, 0xF8)).getRGB()
        Vect pos, dir;

        public Agent(float x, float y) {
            pos = new Vect(x, y);
            dir = (new Vect((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1)).setMag(1);
        }

        public int getWeight() {
            return agentWeight;
        }

        public void smell() {
            int F, FL, FR;

            Vect offdir = dir.copy().setMag(offsetLength);
            Vect leftdir = offdir.rotate((float) (-Math.PI * .25));
            Vect rightdir = offdir.rotate((float) (Math.PI * .25));
            Vect Fdir = new Vect(wrap(this.pos.x + offdir.x, map.getWidth()), wrap(this.pos.y + offdir.y, map.getHeight()));
            Vect FLdir = new Vect(wrap(this.pos.x + leftdir.x, map.getWidth()), wrap(this.pos.y + leftdir.y, map.getHeight()));
            Vect FRdir = new Vect(wrap(this.pos.x + rightdir.x, map.getWidth()), wrap(this.pos.y + rightdir.y, map.getHeight()));

            F = map.getRGB((int) (Fdir.x), (int) (Fdir.y))>>24;
            int f = dataPointImage.getRGB((int) (Fdir.x), (int) (Fdir.y))>>24;
            //F = (F > f) ? F : f;
	    F += f;
            FL = map.getRGB((int) (FLdir.x), (int) (FLdir.y))>>24;
            int fl = dataPointImage.getRGB((int) (FLdir.x), (int) (FLdir.y))>>24;
            //FL = (FL > fl) ? FL : fl;
	    FL += fl;
            FR = map.getRGB((int) (FRdir.x), (int) (FRdir.y))>>24;
            int fr = dataPointImage.getRGB((int) (FRdir.x), (int) (FRdir.y))>>24 ;
            //FR = (FR > fr) ? FR : fr;
	    FR += fr;
	    
            if (F > FL && F > FR)  return;
            if (F < FL && F < FR) {
                if (Math.random() < .5) 
		    this.turnLeft();
		else 
		    this.turnRight();
                return;
            }
            if (FL < FR)
                this.turnRight();
	    else if (FR < FL) 
		this.turnLeft();
        }

        public void turnLeft() {
            dir = dir.rotate(-angle);
        }
        public void turnRight() {
            dir = dir.rotate(angle);
        }
        public void move() {
            this.pos.x = wrap(this.pos.x + dir.x, map.getWidth());
            this.pos.y = wrap(this.pos.y + dir.y, map.getHeight());
        }

    }

    private ArrayList<Agent> swarm;
    private final int mapWidth, mapHeight;

    private BufferedImage map;
    private float offsetLength;
    private int agentCount;
    private float angle;
    private BufferedImage dataPointImage, newmap, popmap;

    public void setOffsetLength(float offsetLength) {
	this.offsetLength = offsetLength;
    }

    public void setAngle(float angle) {
	this.angle = angle;
    }
    
    

    public MoldSim(int agentCount, float offsetLength, float angle, int[] sol, int width, int height, BufferedImage popmap) {
        this.mapWidth = width;
        this.mapHeight = height;
        this.agentCount = agentCount;
        this.offsetLength = offsetLength;
	this.popmap = popmap;
        this.angle = (float)(Math.PI / 4); //angle;
        populateSwarm();

        dataPointImage = dataPointImageFromSol(sol);

        map = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gm = map.createGraphics();
        gm.setColor(Color.black);
        gm.fillRect(0, 0, width, height);
        //gm.drawImage(dataPointImage, null, 0, 0);

        newmap = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_INT_ARGB);
        gm = newmap.createGraphics();
        gm.setColor(Color.black);
        gm.fillRect(0, 0, width, height);
        setAttenuation(0.96f);
    }

    private int calcDepAtDist(int val, int x1, int y1, int x2, int y2) {
        return (int)(val / Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }
    public BufferedImage dataPointImageFromSol(int[] sol) {
        BufferedImage dataImage = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_4BYTE_ABGR);

        int max_reach = 0;
        for (int i = 0; i < sol.length / 3; i++) {
            if (sol[i * 3 + 2] > max_reach) {
                max_reach = sol[i * 3 + 2];
            }
        }
        max_reach = max_reach / 500;
        System.out.println(max_reach);

        int[][] damap = new int[mapHeight][mapWidth];
        System.out.println("Preprocessing datapoint image");
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                int maxval = 0;
                for (int k = 0; k < sol.length / 3; k++) {
                    int v = 0;
                    if (sol[k * 3 + 1] == i && j == sol[k * 3 + 0]) {
                        v = 255;
                    } else {
                        v = calcDepAtDist(sol[k * 3 + 2] / max_reach,  sol[k * 3 + 0], sol[k * 3 + 1], j, i);
                    }
                    if (v > maxval) {
                        maxval = v; 
                    }
                }
                damap[i][j] = maxval;
            }
        }

        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                dataImage.setRGB(
                        j,
                        i,
                        new Color(
                                255, //(int) Math.min(damap[i][j], 255),
                                0,
                                0
                                ,(int) Math.min(damap[i][j], 255)
                        ).getRGB());
            }
        }

        System.out.println("finished processing datapoint image");

        return dataImage;
    }

    private void populateSwarm() {
        swarm = new ArrayList<>();
        for (int i = 0; i < agentCount; i++) {
            swarm.add(new Agent((float) (Math.random() * mapWidth), (float) (Math.random() * mapHeight)));
        }
    }

    private void updateAgents() {
        swarm.stream().parallel().forEach((a) -> {
            a.smell();
            a.move();
        });
    }

    private void propagatePheromones() {
        swarm.stream().parallel().forEach((a) -> {
            map.setRGB(
                    (int) a.pos.x,
                    (int) a.pos.y,
                    a.getWeight()
            );
        });
    
    }

    private float attenuation;
    private float[] baseKernelMatrix = {
        1 / 14f, 1 / 7f, 1 / 14f,
        1 / 7f, 1 / 7f, 1 / 7f,
        1 / 14f, 1 / 7f, 1 / 14f,};
    private float[] diffuseKernelMatrix;
    private Kernel diffuseKernel;

    public void setAttenuation(float att) {
        attenuation = att;
        diffuseKernelMatrix = new float[baseKernelMatrix.length];
        for (int i = 0; i < diffuseKernelMatrix.length; i++) {
            diffuseKernelMatrix[i] = baseKernelMatrix[i] * attenuation;
        }
        diffuseKernel = new Kernel(3, 3, diffuseKernelMatrix);
    }

    private void relaxationStep() {
        (new ConvolveOp(diffuseKernel)).filter(map, newmap);
        BufferedImage temp = newmap;
        newmap = map;
        map = temp;
    }

    public void updateMap() {
        updateAgents();
        //System.out.println("Updated Agents");
        propagatePheromones();
        //System.out.println("Propageted pehromones");
        relaxationStep();
        //System.out.println("Relaxation");

    }


    @Override
    public void display(Graphics2D g, int width, int height) {
	g.setColor(Color.black);
	g.fillRect(0, 0, width, height);
	g.drawImage(popmap, null, 0, 0);
        g.drawImage(map, null, 0, 0);
	g.drawImage(dataPointImage, null, 0, 0);
    }
}
