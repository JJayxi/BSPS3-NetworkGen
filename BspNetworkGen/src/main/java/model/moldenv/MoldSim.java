package model.moldenv;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MoldSim {

    private BufferedImage map;
    private float offsetLength;
    private int agentCount;
    private float angle;

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

	Vect pos, dir;

	public Agent(float x, float y) {
	    pos = new Vect(x, y);
	    dir = (new Vect((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1)).setMag(1);
	}

	boolean smell() {
	    int F, FL, FR;

	    Vect offsetDirection = this.dir.copy().setMag(offsetLength);
	    Vect leftVector = offsetDirection.rotate((float)(-Math.PI * .25));
	    Vect rightVector = offsetDirection.rotate((float)(Math.PI * .25));
	    Vect Fdir = new Vect(wrap(this.pos.x + offsetDirection.x, map.getWidth()), wrap(this.pos.y + offsetDirection.y, map.getHeight()));
	    Vect FLdir = new Vect(wrap(this.pos.x + leftVector.x, map.getWidth()), wrap(this.pos.y + leftVector.y, map.getHeight()));
	    Vect FRdir = new Vect(wrap(this.pos.x + rightVector.x, map.getWidth()), wrap(this.pos.y + rightVector.y, map.getHeight()));

	    F = map.getRGB((int)(Fdir.x), (int)(Fdir.y));
	    FL = map.getRGB((int)(FLdir.x), (int)(FLdir.y));
	    FR = map.getRGB((int)(FRdir.x), (int)(FRdir.y));

	    if (F > FL && F > FR) {
		return true;
	    } else if (F < FL && F < FR) {
		if (Math.random() < .5) {
		    this.turnLeft();
		    return true;
		} else {
		    this.turnRight();
		    return true;
		}
	    } else if (FL < FR) {
		this.turnRight();
		return true;
	    } else if (FR < FL) {
		this.turnLeft();
		return true;
	    } else {
		return false;
	    }
	}

	void turnLeft() {
	    this.dir.rotate(-angle);
	}

	void turnRight() {
	    this.dir.rotate(angle);
	}

	void move() {
	    this.pos.x = wrap(this.pos.x + this.dir.x, map.getWidth());
	    this.pos.y = wrap(this.pos.y + this.dir.y, map.getHeight());
	}

    }

    private ArrayList<Agent> swarm;
    private ArrayList<Vect> solution;
    private final int mapWidth, mapHeight;
    
    public MoldSim(int agentCount, float offsetLength, float angle, int[] sol, int width, int height) {
	this.mapWidth = width;
	this.mapHeight = height;
	
	solution = new ArrayList<>();
	for(int i = 0; i < sol.length / 2; i++) {
	    solution.add(new Vect(sol[i], sol[i + 1]));
	}
	
	map = convertSolToMap(solution);
	
	this.agentCount = agentCount;
	this.offsetLength = offsetLength;
	this.angle = angle;
	
	populateSwarm();
	
   }
    
    private BufferedImage convertSolToMap(ArrayList<Vect> solution) {
	return null;
    }
    
    private void populateSwarm() {
	swarm = new ArrayList<>();
	for(int i = 0; i < agentCount; i++)
	{
	    swarm.add(new Agent((float)(Math.random() * mapWidth), (float)(Math.random() * mapHeight)));
	}
    }
    
    public void updateAgents() {
	swarm.stream().parallel().forEach((a)-> {
	    a.smell();
	});
	
	
    }
}
