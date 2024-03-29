/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import model.Displayer;

/**
 *
 * @author jason
 */
public class NodePanel extends javax.swing.JPanel {

    /**
     * Creates new form NodePanel
     */
    private Displayer displayer;

    public NodePanel() {
        initComponents();
       /* Environment env = new Environment(6, 50, 5, img);
        System.out.println("Environment Prepared");
        GeneticAlgorithm ga = new GeneticAlgorithm(100, 0.5, 0.05, env);
        System.out.println("Genetic Algorithm created");
        for(int i = 0; i < 500; i++) {
            ga.performGeneration();
            System.out.println(ga.getBest().b);
        }
        
        envD.setSol(ga.getBest().a);*/
    }

    public void setDisplayer(Displayer envD) {
        this.displayer = envD;
    }
    
    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        if(displayer != null)displayer.display(g, getWidth(), getHeight());
        else {
            System.out.println("here");
            g.setColor(Color.black);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
