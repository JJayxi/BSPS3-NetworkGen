package model.gaenv;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import model.Displayer;
import java.awt.geom.AffineTransform;

public class EnvDisplay implements Displayer {
    private int nodeRadius;
    private BufferedImage map;
    private int[] currentSol;
    
    public EnvDisplay(BufferedImage map, int nodeRadius) {
       this.map = map;
       this.nodeRadius = nodeRadius;       
       
       currentSol = null;
    }
    
    public void setSol(int[] sol) {
        currentSol = sol;
    }
    
    @Override
    public void display(Graphics2D g, int width, int height) {
        double zoomX = (double)width / map.getWidth();
        double zoomY = (double)height / map.getHeight();
        //Affine Transformation on graphics2d to zoom out
        AffineTransform at = new AffineTransform();
        at.scale(zoomX, zoomY);
        g.setTransform(at);

        g.drawImage(map, 0, 0, null);
        if(currentSol == null) return;
        for (int i = 0; i < currentSol.length / 2; i++) {
            int x = currentSol[i], y = currentSol[i + 1];
            g.setColor(new Color(200,200,230, 120));
            g.fillOval(x - nodeRadius, y - nodeRadius, nodeRadius * 2, nodeRadius * 2);
            g.setColor(new Color(255, 0, 0));
            g.fillOval(x-4, y-4, 8, 8);
        }
        
    }

    public void setMap(BufferedImage img) {
        map = img;
    }
    
    public void setNodeRadius(int nodeRadius) {
        this.nodeRadius = nodeRadius;
    }
}
