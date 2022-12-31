package model.gaenv;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Environment implements GAEnv{
    private int numNodes;
    private int nodeRadius;
    private int proxPenalty;
    private BufferedImage popmap;
    
    private int[][] map;
    private int[][] processed;
    
    public Environment (int numNodes, int nodeRadius, int proxPenalty, 
            BufferedImage mapImage) {
        this.numNodes = numNodes;
        this.nodeRadius = nodeRadius;
        this.proxPenalty = proxPenalty;
	this.popmap = mapImage;
        
        map = imageToMap(mapImage);
        System.out.println("Preprocessing map");
        preprocess();
    }

    public BufferedImage getPopmap() {
	return popmap;
    }
    
    

    public int getMaxValue() {
        return map.length;
    }
    
    public int getWidth() {
        return map[0].length;
    }
    
    public int getHeight() {
        return map.length;
    }

    public int getNumNodes() {
	return numNodes;
    }

    public int getNodeRadius() {
	return nodeRadius;
    }

    public int getProxPenalty() {
	return proxPenalty;
    }
    
    
    
    public void setMap(BufferedImage mapImage) {
        map = imageToMap(mapImage);
	popmap = mapImage;
        preprocess();
    }

    public void setNumNodes(int numNodes) {
        this.numNodes = numNodes;
    }

    public void setNodeRadius(int nodeRadius) {
        this.nodeRadius = nodeRadius;
        preprocess();
    }

    public void setProxPenalty(int proxPenalty) {
        this.proxPenalty = proxPenalty;
    }
    
    
    
    @Override
    public int getSolLen() {
        return numNodes * 2;
    }
    
    private int[][] imageToMap(BufferedImage image) {
	maxdist = (int)(Math.sqrt(image.getWidth() * image.getWidth() * 2));
        int[][] map = new int[image.getHeight()][image.getWidth()];
        for(int i = 0; i < image.getHeight(); i++) {
            for(int j = 0; j < image.getWidth(); j++) {
                Color c = new Color(image.getRGB(j, i));       
                map[i][j] = 255 - c.getRed();
            }
        }
        
        return map;
    }
    
    private void preprocess() {
        processed = new int[map.length][map[0].length];
        for(int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[0].length; j++) {
		
                processed[i][j] = reach(j, i);
            }
        }
    }
    
    private int reach_old(int x, int y) {
        int sum = 0;
	int lasth = -1;
        for(int k = 0; k <= nodeRadius; k++) {
            double a = Math.PI * k/nodeRadius + Math.PI / 2;
            int h = y + (int)(Math.sin(a) * nodeRadius);
            if(h < 0 || h >= map.length || h == lasth) continue;

            lasth = h;
            int dw = (int)(Math.cos(a) * nodeRadius);
            
            for(int w = x + dw; w < x - dw; w++) {
                if(w < 0 || w >= map[0].length)continue;
                sum += map[h][w];
            }
        }
        
        return sum;
    }
    
    private int reach(int x, int y) {
	int sum = 0;
	for(int h = -nodeRadius; h <= nodeRadius; h++) {
	    double a = Math.acos((double)h / nodeRadius);
	    if(h + y < 0 || h + y >= map.length) continue;
	    int dw = (int)(Math.sin(a) * nodeRadius);
	    for(int w = x - dw; w <= x + dw; w++) {
                if(w < 0 || w >= map[0].length)continue;
                sum += map[h + y][w];
            }
	}
	
	return sum;
    }
    
    @Override
    public boolean verify(int[] sol) {
        if(numNodes * 2 != sol.length)return false;
        for(int i = 0; i < numNodes; i++) {
         if(sol[i * 2] < 0 || sol[i * 2] >= map[0].length)return false;
         if(sol[i * 2 + 1] < 0 || sol[i * 2 + 1] >= map.length)return false;
        }
        
        return true;
    }
    
        @Override
    public int[] correct(int[] sol) {
        for(int i = 0; i < numNodes; i++) {
            if(sol[i * 2] < 0) sol[i * 2] = 0;
            else if(sol[i * 2] >= map[0].length)sol[i * 2] = map[0].length - 1;
            if(sol[i * 2 + 1] < 0) sol[i * 2 + 1] = 0;
            else if(sol[i * 2 + 1] >= map.length)sol[i * 2 + 1] = map.length - 1;
        }
        return sol;
    }
    
    @Override
    public int eval(int[] sol) {
        if(sol.length != 2*numNodes)return Integer.MIN_VALUE;
        
        
        int totalReach = 0;
        for(int i = 0; i < numNodes; i++) {
            if(sol[i+1] >= map[0].length || sol[i+1] < 0 || sol[i] >= map.length || sol[i] < 0)
                totalReach -= 10000000;
            else totalReach += processed[sol[i + 1]][sol[i]];
        }
        
        int totalPenalty = 0;
        for(int i = 0; i < numNodes * 2 - 1; i++)
	for(int j = i + 1; j < numNodes * 2 - 1; j++)
	    if(i != j)
	    totalPenalty += penalty(
		sol[i], sol[i + 1], 
		sol[j], sol[j + 1]);
	/*
		sol[2 * i], sol[2 * i + 1], 
		sol[2 * j], sol[2 * j + 1]);*/
            
        
        
        return totalReach / numNodes - totalPenalty / (numNodes);
    }
    
    public int[] exportForSlimeMold(int[] sol) {
        int[] ar = new int[numNodes * 3];
        for(int i = 0; i < numNodes; i++)  {
            ar[i * 3] = sol[i * 2];
            ar[i * 3 + 1] = sol[i * 2 + 1];
            ar[i * 3 + 2] = reach(sol[i * 2], sol[i * 2 + 1]);
        }
        return ar;
    }
    
    private int maxdist;
    public int penalty(int x1, int y1, int x2, int y2) {
        int dist = (int) Point2D.distance(x1, y1, x2, y2) + 1;
        return 30000 * proxPenalty / dist;
        //return proxPenalty * (maxdist - dist);
    }
    
}
