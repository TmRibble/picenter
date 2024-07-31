package com.picenter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window {
    public static final String SYSTEM_NAME = "picenter_window";

    // backend
    private static Object[][] values = new Object[][]{{"Local Temp", 0}, {"Local RH", 0}, {"City Temp", 0}, {"City RH", 0}, {"Wind Speed", 0}, {"Wind Direction", 0}};

    private static Logger logger;

    private static boolean active;

    private static String drawText;

    private static int index;

    // frontend
    private static JFrame window;

    private static JPanel graphics;
    private static AffineTransform old;

    public static boolean init(Logger log) {
        logger = log;

        try {
            window = new JFrame(Library.APP_PUBLIC_NAME);
            graphics = new JPanel(){

                @Override
                public void paintComponent(Graphics g){
                    super.paintComponent(g);

                    draw((Graphics2D) g);
                }

            };

            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
            window.setResizable(false);
            window.setBounds(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().x - Library.WINDOW_WIDTH / 2,
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().y - Library.WINDOW_HEIGHT / 2,
                    Library.WINDOW_WIDTH, 
                    Library.WINDOW_HEIGHT
            );

            window.add(graphics);

            active = true;

            return true;
        } catch (Exception e) {
            logger.log("Error while initializing graphics.", SYSTEM_NAME, Logger.ERROR);
            logger.logError(e, null, SYSTEM_NAME);

            active = false;

            return false;
        }
    }

    public static void updateValue(String key, Integer value){
        for (Object[] keyValue : values) {
            if(keyValue[0].equals(key)){
                keyValue[1] = value;
            }
        }
    }

    private static void draw(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 24));

        g.setColor(Color.BLACK);
        
        g.setStroke(new BasicStroke(4));
        g.drawString("Weather", 80, 50);

        g.setStroke(new BasicStroke(3));
        g.drawLine(40, 60, 215, 60);
        
        index = 0;
        for (Object[] keyValue : values) {
            if(!keyValue[0].toString().contains("Wind")){    
                drawText = keyValue[0].toString() + " : " + keyValue[1].toString() + (keyValue[0].toString().contains("RH") ? "%" : " F");

                g.drawString(drawText, 127 - g.getFontMetrics().stringWidth(drawText) / 2, 90 + 28*index);

                index++;
            }
        }

        // reverse button
        g.fillPolygon(new int[]{700, 700, 680, 680, 645, 680, 680}, new int[]{100, 140, 130, 140, 120, 100, 110}, 7);

        //fast forward button
        old = g.getTransform();
        g.rotate(Math.toRadians(180), 808, 120);

        g.fillPolygon(new int[]{830, 830, 810, 810, 785, 810, 810}, new int[]{100, 140, 130, 140, 120, 100, 110}, 7);

        g.setTransform(old);

        //pause button
        if(Music.isStopped()){
            //play

            g.fillPolygon(new int[]{730, 730, 760}, new int[]{100, 140, 120}, 3);
        }else{
            //pause

            g.fillRect(727, 100, 10, 40);
            g.fillRect(747, 100, 10, 40);
        }

        //song progress bar.
        g.setStroke(new BasicStroke(0.5f));
        g.drawRect(645, 70, 185, 10);

        g.fillRect(645, 70, (int) (185 * Music.getProgress()), 10);

        g.setStroke(new BasicStroke(3));

        g.drawString("- " + values[4][1] + " MPH", 190, 271);

        g.rotate(Math.toRadians((Integer) (values[5][1])), 102, 265);

        g.fillPolygon(new int[]{100, 105, 105, 110, 102, 95, 100}, new int[]{330, 330, 210, 210, 200, 210, 210}, 7);

        
    }

    public static final boolean isActive() {
        return active;
    }

}
