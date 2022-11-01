package model.moldenv;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import model.Displayer;

public class MoldSim implements Displayer{




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
        public static final  int agentWeight = 0x4F;
	Vect pos, dir;

	public Agent(float x, float y) {
	    pos = new Vect(x, y);
	    dir = (new Vect((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1)).setMag(1);
            //System.out.println((new Color(agentWeight<<16)).getRed());
	}
        
        public int getWeight() {
            return agentWeight;
        }
        
	public boolean smell() {
	    int F, FL, FR;

	    Vect offsetDirection = this.dir.copy().setMag(offsetLength);
	    Vect leftVector = offsetDirection.rotate((float)(-Math.PI * .25));
	    Vect rightVector = offsetDirection.rotate((float)(Math.PI * .25));
	    Vect Fdir = new Vect(wrap(this.pos.x + offsetDirection.x, map.getWidth()), wrap(this.pos.y + offsetDirection.y, map.getHeight()));
	    Vect FLdir = new Vect(wrap(this.pos.x + leftVector.x, map.getWidth()), wrap(this.pos.y + leftVector.y, map.getHeight()));
	    Vect FRdir = new Vect(wrap(this.pos.x + rightVector.x, map.getWidth()), wrap(this.pos.y + rightVector.y, map.getHeight()));

//	    F = new Color(map.getRGB((int)(Fdir.x), (int)(Fdir.y))).getBlue();
//	    FL = new Color(map.getRGB((int)(FLdir.x), (int)(FLdir.y))).getBlue();
//	    FR = new Color(map.getRGB((int)(FRdir.x), (int)(FRdir.y))).getBlue();
            F = map.getRGB((int)(Fdir.x), (int)(Fdir.y));
	    FL =map.getRGB((int)(FLdir.x), (int)(FLdir.y));
	    FR = map.getRGB((int)(FRdir.x), (int)(FRdir.y));

            //System.out.println(F + ", comp " + FL + "and FR");
	    if (F > FL && F > FR) {
		return true;
	    }
            if (F < FL && F < FR) {
		if (Math.random() < .5) {
		    this.turnLeft();
		    return true;
		}
                this.turnRight();
                return true;
                
	    }
            if (FL < FR) {
		this.turnRight();
		return true;
	    }
            if (FR < FL) {
		this.turnLeft();
		return true;
	    } 
            return false;
	}

	public void turnLeft() {
	    this.dir = dir.rotate(-angle);
	}

	public void turnRight() {
	    this.dir = dir.rotate(angle);
	}

	public void move() {
	    this.pos.x = wrap(this.pos.x + this.dir.x, map.getWidth());
	    this.pos.y = wrap(this.pos.y + this.dir.y, map.getHeight());
	    //this.pos.x = this.pos.x + this.dir.x;
	    /*if(pos.x >= map.getWidth()) {
		pos.x = map.getWidth() - 1;
		dir.x = -dir.x;
	    } else if (pos.x < 0) {
		pos.x = 0;
		dir.x = -dir.x;
	    }
	    this.pos.y = this.pos.y + this.dir.y;
	    if(pos.y >= map.getHeight()) {
		pos.y = map.getHeight() - 1;
		dir.y = -dir.y;
	    } else if (pos.y < 0) {
		pos.y = 0;
		dir.y = -dir.y;
	    }*/
	}
        


    }
    class DataAgent extends Agent {
       private final int dataWeight;
        
        public DataAgent(float x, float y, int dataWeight) {
            super(x, y);
            this.dataWeight = dataWeight;
        }
        
        @Override
        public int getWeight() {
            return dataWeight;
        }
        
        @Override
        public boolean smell() {
            return false;
        }
        
        @Override
        public void move() {}
    }

    private ArrayList<Agent> swarm;
    private final int mapWidth, mapHeight;
    
    
    private BufferedImage map;
    private float offsetLength;
    private int agentCount;
    private float angle;
    
    public MoldSim(int agentCount, float offsetLength, float angle, int[] sol, int width, int height) {
	this.mapWidth = width;
	this.mapHeight = height;
	this.agentCount = agentCount;
	this.offsetLength = offsetLength;
	this.angle = angle;
	populateSwarm();
        
        int maxforscale = 0;
        for(int i = 0; i < sol.length / 3; i++) {
            if(sol[i * 3 + 2] > maxforscale)maxforscale = sol[i * 3 + 2];
	}
        
        maxforscale /= 256;
        
	for(int i = 0; i < sol.length / 3; i++) {
            int x = sol[i * 3];
            int y = sol[i * 3 + 1];
            int w = sol[i * 3 + 2] / maxforscale;
            int r = 20;
            int n = 30;
            double da = Math.PI * 2 / n;
            for(int j = 0; j < n; j++) {
                float nx = x + (float)(Math.cos(da * j) * r * (Math.random() * 0.3 + 0.7));
                float ny = y + (float)(Math.sin(da * j) * r * (Math.random() * 0.3 + 0.7));
                if(nx >= 0 && nx < width && ny >= 0 && ny < height)
                swarm.add(new DataAgent(nx, ny, 255));
            }
	    
	}
        
        map = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        map.getGraphics().setColor(Color.black);
        map.getGraphics().fillRect(0, 0, width, height);
        
        newmap  = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_INT_RGB);
        newmap.getGraphics().setColor(Color.black);
        newmap.getGraphics().fillRect(0, 0, width, height);
        setAttenuation(0.98f);
   }
    
    private void populateSwarm() {
	swarm = new ArrayList<>();
	for(int i = 0; i < agentCount; i++)
	{
	    swarm.add(new Agent((float)(Math.random() * mapWidth), (float)(Math.random() * mapHeight)));
	}
    }
    
    private void updateAgents() {
	swarm.stream().parallel().forEach((a)-> {
	    a.smell();
            a.move();
	});
    }
    
    private void propagatePheromones() {
        swarm.stream().parallel().forEach((a)-> {
	    map.setRGB(
                    (int)a.pos.x, 
                    (int)a.pos.y, 
                    a.getWeight()<<16
                    );
        });
    }
    
    private float attenuation = 0.9f;
    private float[] baseKernelMatrix = {
        1/14f, 1/7f, 1/14f,
        1/7f, 1/7f, 1/7f,
        1/14f, 1/7f, 1/14f,
    };    
    private float[] diffuseKernelMatrix;
    private Kernel diffuseKernel;
    
    public void setAttenuation(float att) {
        attenuation = att;
        diffuseKernelMatrix = new float[baseKernelMatrix.length];
        for(int i = 0; i < diffuseKernelMatrix.length; i++) {
            diffuseKernelMatrix[i] = baseKernelMatrix[i] * attenuation; 
        }
        diffuseKernel = new Kernel(3, 3, diffuseKernelMatrix);
    }
    
    BufferedImage newmap;
    private void relaxationStep() {
        (new ConvolveOp(diffuseKernel)).filter(map, newmap);  
        BufferedImage temp = newmap;
        newmap = map;
        map = temp;
    }
    
    
    public void updateMap() {
        updateAgents();
        propagatePheromones();
        relaxationStep();
        
    }
    
    @Override
    public void display(Graphics2D g, int width, int height) {
        g.drawImage(map, null, 0, 0);
    }
}
